package com.ejt.money;

import com.ejt.util.CalendarUtil;
import com.ejt.util.PropertyManager;
import com.ejt.util.Util;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ConvertTransactions {

    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ConvertTransactions.class);

    public static void main(String[] args) {

        if (args.length != 1 && args.length != 2) {
            System.out.println("Usage: " + ConvertTransactions.class.getName() + " <year> {orig|dups}");
            System.exit(1);
        }
        boolean showOriginal = args.length == 2 && "orig".equalsIgnoreCase(args[1]);
        boolean listDups = args.length == 2 && "dups".equalsIgnoreCase(args[1]);

        if (args.length == 2 && !showOriginal && !listDups) {
            System.out.println("Usage: " + ConvertTransactions.class.getName() + " <year> {orig|dups}");
            System.exit(1);
        }
        String tranFilePath = PropertyManager.getString("transactions.file", null);
        logger.debug("tranFilePath is " + tranFilePath);
        String year = args[0];
        logger.debug("year is " + year);

        if (listDups) {
            determineTransactionList(tranFilePath, true, year);
            System.exit(0);
        }

        logger.debug("showOriginal is " + showOriginal);
        List<Transaction> transList = determineTransactionList(tranFilePath, false, year);
        Collections.reverse(transList);

        List<String> transLines = new ArrayList<>();
        for (Transaction t : transList) {
            transLines.add(t.toString(showOriginal));
        }
        Util.writeFile(PropertyManager.getString("mint.files.folder", null) + year + "_trans.list", transLines);
    }

    public static List<Transaction> determineTransactionList(String transFilePath, boolean listDups, String year) {
        List<Transaction> transList = new ArrayList<>();

        BufferedReader reader = null;
        int lineNum = 0;
        try {
            File transFile = new File(transFilePath);
            if (!transFile.exists()) {
                logger.error("Error: transaction file does not exist: " + transFilePath);
                System.exit(1);
            }
            reader = new BufferedReader(new FileReader(transFile));
            while (reader.ready()) {
                lineNum++;
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                List<String> lineTokens = Util.parseQuotedList2(line);
                if (lineNum == 1) {
                    checkHeaders(lineTokens);
                } else if (!lineTokens.isEmpty()) {
                    transList.add(Transaction.create(lineTokens, lineNum));
                }
            }
        } catch (Exception e) {
            logger.error("Error on line " + lineNum + ": " + e.getMessage());
            System.exit(1);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ioe) {
                logger.warn("IOException closing file: " + ioe.getMessage());
            }
        }

        Set<String> usedDupTransSet = new HashSet<String>();
        List<Transaction> filteredTransList = new ArrayList<Transaction>();

        for (Transaction t : transList) {
            if (t.hasTag(Transaction.Tag.DUPLICATE)) {
                continue;
            }
            if ("TEKsystems 401k".equals(t.getAccount())) {
                continue;
            }
            if ("Hide from Budgets & Trends".equals(t.getCategory())) {
                continue;
            }
            if ("Trade Commissions".equals(t.getCategory())) {
                continue;
            }

            String date = CalendarUtil.toString(t.getDate(), "MM-dd-yyyy");
            if (!date.endsWith(year)) {
                continue;
            }
            String trans = date + "|" + t.getDescription() + "|" + t.getAmount() + "|" + t.getOrigType();

            if (trans.endsWith("Interest Charged|0|credit")) {
                continue;
            }

            if (listDups) {
                if (!usedDupTransSet.contains(trans)) {
                    usedDupTransSet.add(trans);
                } else {
                    System.out.println(trans);
                }
            } else {
                filteredTransList.add(t);
            }
        }

        return filteredTransList;
    }

    private static void checkHeaders(List<String> lineTokens) throws Exception {
        if (lineTokens.size() != 9) {
            throw new Exception("checkHeaders: expected 9 tokens");
        }
        if (!"Date".equals(lineTokens.get(0))) {
            throw new Exception("checkHeaders: expected token 1 to be Date");
        }
        if (!"Description".equals(lineTokens.get(1))) {
            throw new Exception("checkHeaders: expected token 2 to be Description");
        }
        if (!"Amount".equals(lineTokens.get(3))) {
            throw new Exception("checkHeaders: expected token 4 to be Amount");
        }
        if (!"Transaction Type".equals(lineTokens.get(4))) {
            throw new Exception("checkHeaders: expected token 5 to be Transaction Type");
        }
        if (!"Category".equals(lineTokens.get(5))) {
            throw new Exception("checkHeaders: expected token 6 to be Category");
        }
        if (!"Account Name".equals(lineTokens.get(6))) {
            throw new Exception("checkHeaders: expected token 7 to be Account Name");
        }
        if (!"Labels".equals(lineTokens.get(7))) {
            throw new Exception("checkHeaders: expected token 8 to be Labels");
        }
        if (!"Notes".equals(lineTokens.get(8))) {
            throw new Exception("checkHeaders: expected token 9 to be Notes");
        }
    }

    public static List<Transaction> readTransactionList(String transFilePath) {
        List<Transaction> transList = new ArrayList<Transaction>();

        BufferedReader reader = null;
        int lineNum = 0;
        try {
            File transFile = new File(transFilePath);
            if (!transFile.exists()) {
                logger.error("Error: transaction file does not exist: " + transFilePath);
                System.exit(1);
            }
            List<String> lines = new ArrayList<String>();
            reader = new BufferedReader(new FileReader(transFile));
            while (reader.ready()) {
                lineNum++;
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (StringUtils.isBlank(line)) {
                    if (lines.size() >= 2) {
                        transList.add(Transaction.createFromLines(lines, lineNum));
                        lines.clear();
                    }
                } else {
                    lines.add(line);
                }
            }
            if (lines.size() >= 2) {
                transList.add(Transaction.createFromLines(lines, lineNum));
            }
        } catch (Exception e) {
            logger.error("Error just above line " + lineNum + ": " + e.getMessage());
            System.exit(1);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ioe) {
                logger.warn("IOException closing file: " + ioe.getMessage());
            }
        }

        return transList;
    }

}
