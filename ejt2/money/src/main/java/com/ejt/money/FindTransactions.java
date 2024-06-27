package com.ejt.money;

import com.ejt.util.PropertyManager;
import com.ejt.util.Util;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class FindTransactions {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(FindTransactions.class);

    public static void main(String[] args) throws Exception {

        if (args.length != 5) {
            System.err.println("Usage: " + FindTransactions.class.getName() + " <year> <E/I> <group> <quarter> <run-code>");
            System.exit(1);
        }
        String year = args[0];
        logger.debug("year is " + year);

        Transaction.Type transType = null;
        String transLabel = "expense";

        if ("E".equals(args[1])) {
            transType = Transaction.Type.EXPENSE;
        } else if ("I".equals(args[1])) {
            transType = Transaction.Type.INCOME;
            transLabel = "income";
        } else {
            System.err.println("Argument 2 must be E or I");
            System.exit(1);
        }

        String qtr = args[3];
        if ("Q1".equals(qtr) || "Q2".equals(qtr) || "Q3".equals(qtr) || "Q4".equals(qtr)) {

        } else {
            System.err.println("Argument 4 must be Q1 or Q2 or Q3 or Q4");
            System.exit(1);
        }

        String runCode = args[4];
        logger.info("run-code is " + runCode);

        String mintFilesFolder = PropertyManager.getString("mint.files.folder." + runCode, null);
        logger.debug("mintFilesFolder is " + mintFilesFolder);

        String group = args[2];
        logger.debug("group is " + group);
        group = StringUtils.replace(group, "_", " ");

        GroupFilter groupFilter = null;

        for (int i = 1; i <= 100; i++) {
            GroupFilter gf = MintUtils.createGroupFilter(transLabel + ".group." + i, transType);

            if (gf != null && gf.getGroupName().equals(group)) {
                groupFilter = gf;
                break;
            }
        }

        if (groupFilter == null) {
            System.err.println("Argument 3 group not valid: " + group);
            System.exit(1);
        }

        List<Transaction> transList = ConvertTransactions.readTransactionList(mintFilesFolder + year + "_trans.list");
        List<Transaction> foundTransList = new ArrayList<>();
        int foundTotal = 0;

        for (Transaction t : transList) {
            if (MintUtils.doesTransactionMatch(t, groupFilter)) {
                if (t.matchesQuarter(qtr)) {
                    foundTransList.add(t);
                }
            }
        }

        for (Transaction t : foundTransList) {
            System.out.print(t.toString(false));
            System.out.print("\n");

            foundTotal += t.getAmount();
        }

        System.out.println(year + " " + transType + " " + groupFilter.getGroupName() + " " + qtr);
        System.out.println("Total is     $" + Util.padString(MintUtils.formatAmount(foundTotal), 8, true, "."));
        System.out.println("Month Avg is $" + Util.padString(MintUtils.formatAmount(foundTotal / 3), 8, true, "."));

    }


}
