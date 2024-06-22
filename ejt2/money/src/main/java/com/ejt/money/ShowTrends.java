package com.ejt.money;

import com.ejt.util.PropertyManager;
import com.ejt.util.Util;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class ShowTrends {

    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ShowTrends.class);

    public static void main(String[] args) throws Exception {

        String[] years = PropertyManager.getStrings("trends.years");
        String[] months = PropertyManager.getStrings("trends.months");

        if (years == null || years.length < 2 || months == null || months.length != years.length) {
            System.err.println("Error: properties trends.years/trends.months not valid");
            System.exit(1);
        }

        boolean showDiff = (years.length == 2);
        int totalDiff = 0;

        String mintFilesFolder = PropertyManager.getString("mint.files.folder", null);
        logger.debug("mintFilesFolder is " + mintFilesFolder);

        List<Map<String, Integer>> totalsList = new ArrayList<Map<String, Integer>>();

        for (int i = 0; i < years.length; i++) {
            List<String> lines = Util.readFile(mintFilesFolder + years[i] + "_budget.list");

            Map<String, Integer> catTotals = new HashMap<>();
            totalsList.add(catTotals);

            int j = 0, incLine = 0, expLine = 0;
            int incTotal = 0, expTotal = 0;
            for (String line : lines) {
                j++;

                if (line.startsWith("Income")) {
                    incLine = j + 2;
                }

                if (incLine > 0 && j >= incLine) {
                    if (StringUtils.isNotBlank(line)) {
                        String cat = StringUtils.trim(line.substring(0, 30));
                        String total = StringUtils.trim(line.substring(150));
                        incTotal += MintUtils.determineCents(total);
                        catTotals.put("I-" + cat, Integer.valueOf(MintUtils.determineCents(total)));
                    } else {
                        incLine = 0;
                    }
                }

                if (line.startsWith("Expense")) {
                    expLine = j + 2;
                }

                if (expLine > 0 && j >= expLine) {
                    if (StringUtils.isNotBlank(line)) {
                        String cat = StringUtils.trim(line.substring(0, 30));
                        String total = StringUtils.trim(line.substring(150));
                        expTotal += MintUtils.determineCents(total);
                        catTotals.put("E-" + cat, Integer.valueOf(MintUtils.determineCents(total)));
                    } else {
                        expLine = 0;
                    }
                }
            }
            catTotals.put("IT", Integer.valueOf(incTotal));
            catTotals.put("ET", Integer.valueOf(expTotal));
        }

        System.out.print("TRENDS\n\n");

        {
            StringBuilder sb = new StringBuilder();
            Util.appendPadString(sb, "Income", 30, false);
            for (int i = 0; i < years.length; i++) {
                Util.appendPadString(sb, years[i], 10, false);

            }
            if (showDiff) {
                Util.appendPadString(sb, "DIFF", 10, false);
            }
            System.out.print(sb.toString());
            System.out.print("\n");
        }

        {
            StringBuilder sb = new StringBuilder();
            Util.appendPadString(sb, "******", 30, false);
            for (int i = 0; i < years.length; i++) {
                Util.appendPadString(sb, "********", 10, false);
            }
            if (showDiff) {
                Util.appendPadString(sb, "********", 10, false);
            }
            System.out.print(sb.toString());
            System.out.print("\n");
        }

        Set<String> incomeGroups = new HashSet<String>();
        for (int i = 0; i < years.length; i++) {
            Map<String, Integer> catTotals = totalsList.get(i);
            for (String cat : catTotals.keySet()) {
                if (cat.startsWith("I-")) {
                    incomeGroups.add(cat.substring(2));
                }
            }
        }

        for (String incGrp : incomeGroups) {
            StringBuilder sb = new StringBuilder();
            Util.appendPadString(sb, incGrp, 28, false);
            for (int i = 0; i < years.length; i++) {
                Map<String, Integer> catTotals = totalsList.get(i);
                Integer amt = catTotals.get("I-" + incGrp);
                if (amt == null) {
                    Util.appendPadString(sb, "N/A", 10, true);
                } else {
                    int numMonths = Util.getInt(months[i], 12);
                    Util.appendPadString(sb, MintUtils.determineAmount(amt.intValue() / numMonths), 10, true);
                }
            }
            if (showDiff) {
                Map<String, Integer> catTotals1 = totalsList.get(0);
                Integer amt1 = catTotals1.get("I-" + incGrp);
                Map<String, Integer> catTotals2 = totalsList.get(1);
                Integer amt2 = catTotals2.get("I-" + incGrp);
                if (amt1 == null || amt2 == null) {
                    Util.appendPadString(sb, "N/A", 10, true);
                } else {
                    int numMonths1 = Util.getInt(months[0], 12);
                    int numMonths2 = Util.getInt(months[1], 12);
                    int diff = (amt2.intValue() / numMonths2) - (amt1.intValue() / numMonths1);
                    totalDiff += diff;
                    Util.appendPadString(sb, MintUtils.determineAmount(diff), 10, true);
                }
            }
            System.out.print(sb.toString());
            System.out.print("\n");
        }

        StringBuilder ib = new StringBuilder();
        Util.appendPadString(ib, "TOTALS", 28, false);
        for (int i = 0; i < years.length; i++) {
            Map<String, Integer> catTotals = totalsList.get(i);
            Integer amt = catTotals.get("IT");
            if (amt == null) {
                Util.appendPadString(ib, "N/A", 10, true);
            } else {
                int numMonths = Util.getInt(months[i], 12);
                Util.appendPadString(ib, MintUtils.determineAmount(amt.intValue() / numMonths), 10, true);
            }
        }
        System.out.print(ib.toString());
        System.out.print("\n");

        System.out.print("\n");

        {
            StringBuilder sb = new StringBuilder();
            Util.appendPadString(sb, "Expense", 30, false);
            for (int i = 0; i < years.length; i++) {
                Util.appendPadString(sb, years[i], 10, false);
            }
            if (showDiff) {
                Util.appendPadString(sb, "DIFF", 10, false);
            }
            System.out.print(sb.toString());
            System.out.print("\n");
        }

        {
            StringBuilder sb = new StringBuilder();
            Util.appendPadString(sb, "*******", 30, false);
            for (int i = 0; i < years.length; i++) {
                Util.appendPadString(sb, "********", 10, false);
            }
            if (showDiff) {
                Util.appendPadString(sb, "********", 10, false);
            }
            System.out.print(sb.toString());
            System.out.print("\n");
        }

        Set<String> expenseGroups = new HashSet<String>();
        for (int i = 0; i < years.length; i++) {
            Map<String, Integer> catTotals = totalsList.get(i);
            for (String cat : catTotals.keySet()) {
                if (cat.startsWith("E-")) {
                    expenseGroups.add(cat.substring(2));
                }
            }
        }

        for (String expGrp : expenseGroups) {
            StringBuilder sb = new StringBuilder();
            Util.appendPadString(sb, expGrp, 28, false);
            for (int i = 0; i < years.length; i++) {
                Map<String, Integer> catTotals = totalsList.get(i);
                Integer amt = catTotals.get("E-" + expGrp);
                if (amt == null) {
                    Util.appendPadString(sb, "N/A", 10, true);
                } else {
                    int numMonths = Util.getInt(months[i], 12);
                    Util.appendPadString(sb, MintUtils.determineAmount(amt.intValue() / numMonths), 10, true);
                }
            }
            if (showDiff) {
                Map<String, Integer> catTotals1 = totalsList.get(0);
                Integer amt1 = catTotals1.get("E-" + expGrp);
                Map<String, Integer> catTotals2 = totalsList.get(1);
                Integer amt2 = catTotals2.get("E-" + expGrp);
                if (amt1 == null || amt2 == null) {
                    Util.appendPadString(sb, "N/A", 10, true);
                } else {
                    int numMonths1 = Util.getInt(months[0], 12);
                    int numMonths2 = Util.getInt(months[1], 12);
                    int diff = (amt1.intValue() / numMonths1) - (amt2.intValue() / numMonths2);
                    totalDiff += diff;
                    Util.appendPadString(sb, MintUtils.determineAmount(diff), 10, true);
                }
            }
            System.out.print(sb.toString());
            System.out.print("\n");
        }

        StringBuilder eb = new StringBuilder();
        Util.appendPadString(eb, "TOTALS", 28, false);
        for (int i = 0; i < years.length; i++) {
            Map<String, Integer> catTotals = totalsList.get(i);
            Integer amt = catTotals.get("ET");
            if (amt == null) {
                Util.appendPadString(eb, "N/A", 10, true);
            } else {
                int numMonths = Util.getInt(months[i], 12);
                Util.appendPadString(eb, MintUtils.determineAmount(amt.intValue() / numMonths), 10, true);
            }
        }
        System.out.print(eb.toString());
        System.out.print("\n");

        System.out.print("\n");

        if (showDiff) {
            StringBuilder sb = new StringBuilder();
            Util.appendPadString(sb, "TOTAL DIFF", 48, false);
            Util.appendPadString(sb, MintUtils.determineAmount(totalDiff), 10, true);
            System.out.print(sb.toString());
            System.out.print("\n\n");
        }
    }
}
