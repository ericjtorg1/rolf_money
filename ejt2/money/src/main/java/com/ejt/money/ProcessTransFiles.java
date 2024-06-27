package com.ejt.money;

import com.ejt.util.PropertyManager;
import com.ejt.util.Util;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class ProcessTransFiles {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ProcessTransFiles.class);

    public static Set<String> EXP_CATEGS = new HashSet<>(), INC_CATEGS = new HashSet<>();


    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Usage: " + ProcessTransFiles.class.getName() + " <year> <run-code>");
            System.exit(1);
        }
        String year = args[0];
        logger.info("year is " + year);

        String runCode = args[1];
        logger.info("run-code is " + runCode);

        String acctTransFilesFolder = PropertyManager.getString("account_trans.files.folder." + runCode, null);
        acctTransFilesFolder = StringUtils.replace(acctTransFilesFolder, "{YEAR}", year);
        String accountCode = PropertyManager.getString("account.code." + runCode, null);
        logger.info("account-code is " + accountCode);

        readInCategories(accountCode);

        List<TransRule> transRules = createTransRules();
        List<Transaction> transactions = new ArrayList<>();

        for (AccountEnum acct : AccountEnum.values()) {
            if (!acct.getCodes().contains(accountCode)) {
                continue;
            }
            logger.info("Process file " + acct.getFile() + " for " + acct.name());

            List<Transaction> acctTransList = acct.getProcFile().process(acctTransFilesFolder + acct.getFile(), transRules);
            transactions.addAll(acctTransList);
        }

        Collections.sort(transactions, new Comparator<Transaction>() {
            public int compare(Transaction t1, Transaction t2) {
                return t1.getDate().compareTo(t2.getDate());
            }
        });

        List<String> transLines = new ArrayList<>();
        for (Transaction t : transactions) {
            transLines.add(t.toString(false));
        }
        Util.writeFile(PropertyManager.getString("mint.files.folder." + runCode, null) + year + "_trans.list", transLines);
    }

    private static List<TransRule> createTransRules() {
        List<TransRule> transRules = new ArrayList<>();

        rules_loop:
        for (int i = 1; i <= 100; i++) {

            for (int j = 0; j <= 100; j++) {
                String ruleId = String.valueOf(i);
                if (j > 0) {
                    ruleId += "_" + j;
                }
                TransRule rule = MintUtils.createTransRule(ruleId);

                if (rule == null) {
                    continue rules_loop;
                }
                logger.debug("loading trans.rule." + ruleId);
                transRules.add(rule);
            }
        }
        logger.info("loaded " + transRules.size() + " trans-rules");
        return transRules;
    }


    private static void readInCategories(String accountCode) {
        String moneyDataFile = PropertyManager.getString("money.data.file." + accountCode, null);
        if (moneyDataFile == null) {
            return;
        }
        List<String> lines = Util.readFile(moneyDataFile);
        boolean insideExp = false, insideInc = false;

        for (String line : lines) {
            if (StringUtils.isBlank(line)) {
                continue;
            }
            if (line.startsWith("Expense Categories")) {
                insideExp = true;
                continue;
            }
            if (line.startsWith("Income Categories")) {
                insideInc = true;
                continue;
            }
            if (line.startsWith("^^^")) {
                insideExp = false;
                insideInc = false;
                continue;
            }
            if (line.startsWith("***")) {
                continue;
            }
            if (insideExp) {
                EXP_CATEGS.add(StringUtils.trim(line));
                continue;
            }
            if (insideInc) {
                INC_CATEGS.add(StringUtils.trim(line));
                continue;
            }
        }
    }
}
