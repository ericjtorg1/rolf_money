package com.ejt.money;

import com.ejt.util.PropertyManager;
import com.ejt.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateReport {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CreateReport.class);
    private String year;

    private final static String PAD = "  ";

    public static void main(String[] args) throws Exception {

        if (args.length != 1) {
            System.err.println("Usage: " + CreateReport.class.getName() + " <year>");
            System.exit(1);
        }
        String mintFilesFolder = PropertyManager.getString("mint.files.folder", null);
        logger.debug("mintFilesFolder is " + mintFilesFolder);

        String year = args[0];
        logger.debug("year is " + year);

        List<Transaction> transList = ConvertTransactions.readTransactionList(mintFilesFolder + year + "_trans.list");

        List<GroupFilter> incomeGroupFilters = new ArrayList<>();
        List<GroupFilter> expenseGroupFilters = new ArrayList<>();

        for (int i = 1; i <= 100; i++) {
            GroupFilter gf = MintUtils.createGroupFilter("income.group." + i, Transaction.Type.INCOME);
            if (gf != null) {
                incomeGroupFilters.add(gf);
            }
            gf = MintUtils.createGroupFilter("expense.group." + i, Transaction.Type.EXPENSE);
            if (gf != null) {
                expenseGroupFilters.add(gf);
            }
        }

        // check integrity
        List<Transaction> expTransNotHandled = new ArrayList<>();
        List<Transaction> incTransNotHandled = new ArrayList<>();

        integrity_loop:
        for (Transaction t : transList) {
            if (t.getType() == Transaction.Type.EXPENSE) {
                for (GroupFilter gf : expenseGroupFilters) {
                    if (MintUtils.doesTransactionMatch(t, gf)) {
                        continue integrity_loop;
                    }
                }
                expTransNotHandled.add(t);

            } else if (t.getType() == Transaction.Type.INCOME) {
                for (GroupFilter gf : incomeGroupFilters) {
                    if (MintUtils.doesTransactionMatch(t, gf)) {
                        continue integrity_loop;
                    }
                }
                incTransNotHandled.add(t);
            }
        }

        if (!incTransNotHandled.isEmpty()) {
            System.out.println("Income Transactions Not covered by groups:");

            for (Transaction t : incTransNotHandled) {
                System.out.println("ID[" + t.getId() + "]");
                System.out.print(t.toString(false));
            }
            System.exit(1);
        }
        if (!expTransNotHandled.isEmpty()) {
            System.out.println("Expense Transactions Not covered by groups:");

            for (Transaction t : expTransNotHandled) {
                System.out.println("ID[" + t.getId() + "]");
                System.out.print(t.toString(false));
            }
            System.exit(1);
        }

        // crunch data
        Map<GroupFilter.AmountLabel, Integer> incomeTotalsMap = computeReportData(incomeGroupFilters, transList);
        Map<GroupFilter.AmountLabel, Integer> expenseTotalsMap = computeReportData(expenseGroupFilters, transList);

        // build report
        List<String> reportLines = new ArrayList<>();

        reportLines.add("REPORT " + year);
        reportLines.add("");

        {
            StringBuilder sb1 = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();

            Util.appendPadString(sb1, "Income", 20, false);
            Util.appendPadString(sb2, "********************", 20, false);

            for (int q = 1; q <= 4; q++) {
                sb1.append(PAD);
                Util.appendPadString(sb1, "Q" + q, 8, false);
                sb2.append(PAD);
                Util.appendPadString(sb2, "********", 8, false);
            }
            sb1.append(PAD);
            Util.appendPadString(sb1, "TOTAL", 8, false);
            sb2.append(PAD);
            Util.appendPadString(sb2, "********", 8, false);

            reportLines.add(sb1.toString());
            reportLines.add(sb2.toString());
        }

        for (GroupFilter gf : incomeGroupFilters) {
            StringBuilder sb = new StringBuilder();
            Util.appendPadString(sb, gf.getGroupName(), 20, false);

            sb.append(PAD);
            Util.appendPadString(sb, MintUtils.formatAmount(
                    MintUtils.computeMonthly(gf.getAmount(GroupFilter.AmountLabel.Q1), 3)), 8, true);

            sb.append(PAD);
            Util.appendPadString(sb, MintUtils.formatAmount(
                    MintUtils.computeMonthly(gf.getAmount(GroupFilter.AmountLabel.Q2), 3)), 8, true);

            sb.append(PAD);
            Util.appendPadString(sb, MintUtils.formatAmount(
                    MintUtils.computeMonthly(gf.getAmount(GroupFilter.AmountLabel.Q3), 3)), 8, true);

            sb.append(PAD);
            Util.appendPadString(sb, MintUtils.formatAmount(
                    MintUtils.computeMonthly(gf.getAmount(GroupFilter.AmountLabel.Q4), 3)), 8, true);

            sb.append(PAD);
            Util.appendPadString(sb,
                    MintUtils.formatAmount(gf.getAmount(GroupFilter.AmountLabel.Total)), 8, true);
            reportLines.add(sb.toString());
        }

        {
            StringBuilder sb = new StringBuilder();
            Util.appendPadString(sb, "TOTAL", 20, false);

            sb.append(PAD);
            Util.appendPadString(sb, MintUtils.formatAmount(
                    MintUtils.computeMonthly(incomeTotalsMap.get(GroupFilter.AmountLabel.Q1), 3)), 8, true);

            sb.append(PAD);
            Util.appendPadString(sb, MintUtils.formatAmount(
                    MintUtils.computeMonthly(incomeTotalsMap.get(GroupFilter.AmountLabel.Q2), 3)), 8, true);

            sb.append(PAD);
            Util.appendPadString(sb, MintUtils.formatAmount(
                    MintUtils.computeMonthly(incomeTotalsMap.get(GroupFilter.AmountLabel.Q3), 3)), 8, true);

            sb.append(PAD);
            Util.appendPadString(sb, MintUtils.formatAmount(
                    MintUtils.computeMonthly(incomeTotalsMap.get(GroupFilter.AmountLabel.Q4), 3)), 8, true);

            sb.append(PAD);
            Util.appendPadString(sb,
                    MintUtils.formatAmount(incomeTotalsMap.get(GroupFilter.AmountLabel.Total)), 8, true);
            reportLines.add(sb.toString());
        }

        reportLines.add("");
        {
            StringBuilder sb1 = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();

            Util.appendPadString(sb1, "Expense", 20, false);
            Util.appendPadString(sb2, "********************", 20, false);

            for (int q = 1; q <= 4; q++) {
                sb1.append(PAD);
                Util.appendPadString(sb1, "Q" + q, 8, false);
                sb2.append(PAD);
                Util.appendPadString(sb2, "********", 8, false);
            }
            sb1.append(PAD);
            Util.appendPadString(sb1, "TOTAL", 8, false);
            sb2.append(PAD);
            Util.appendPadString(sb2, "********", 8, false);

            reportLines.add(sb1.toString());
            reportLines.add(sb2.toString());
        }

        for (GroupFilter gf : expenseGroupFilters) {
            StringBuilder sb = new StringBuilder();
            Util.appendPadString(sb, gf.getGroupName(), 20, false);

            sb.append(PAD);
            Util.appendPadString(sb, MintUtils.formatAmount(
                    MintUtils.computeMonthly(gf.getAmount(GroupFilter.AmountLabel.Q1), 3)), 8, true);

            sb.append(PAD);
            Util.appendPadString(sb, MintUtils.formatAmount(
                    MintUtils.computeMonthly(gf.getAmount(GroupFilter.AmountLabel.Q2), 3)), 8, true);

            sb.append(PAD);
            Util.appendPadString(sb, MintUtils.formatAmount(
                    MintUtils.computeMonthly(gf.getAmount(GroupFilter.AmountLabel.Q3), 3)), 8, true);

            sb.append(PAD);
            Util.appendPadString(sb, MintUtils.formatAmount(
                    MintUtils.computeMonthly(gf.getAmount(GroupFilter.AmountLabel.Q4), 3)), 8, true);

            sb.append(PAD);
            Util.appendPadString(sb,
                    MintUtils.formatAmount(gf.getAmount(GroupFilter.AmountLabel.Total)), 8, true);
            reportLines.add(sb.toString());
        }

        {
            StringBuilder sb = new StringBuilder();
            Util.appendPadString(sb, "TOTAL", 20, false);

            sb.append(PAD);
            Util.appendPadString(sb, MintUtils.formatAmount(
                    MintUtils.computeMonthly(expenseTotalsMap.get(GroupFilter.AmountLabel.Q1), 3)), 8, true);

            sb.append(PAD);
            Util.appendPadString(sb, MintUtils.formatAmount(
                    MintUtils.computeMonthly(expenseTotalsMap.get(GroupFilter.AmountLabel.Q2), 3)), 8, true);

            sb.append(PAD);
            Util.appendPadString(sb, MintUtils.formatAmount(
                    MintUtils.computeMonthly(expenseTotalsMap.get(GroupFilter.AmountLabel.Q3), 3)), 8, true);

            sb.append(PAD);
            Util.appendPadString(sb, MintUtils.formatAmount(
                    MintUtils.computeMonthly(expenseTotalsMap.get(GroupFilter.AmountLabel.Q4), 3)), 8, true);

            sb.append(PAD);
            Util.appendPadString(sb,
                    MintUtils.formatAmount(expenseTotalsMap.get(GroupFilter.AmountLabel.Total)), 8, true);
            reportLines.add(sb.toString());
        }
        TransactionFilter tf;
        List<Transaction> matches;

        reportLines.add("");
        reportLines.add("");
        reportLines.add("Tax-Related");
        reportLines.add("***********");
        tf = new TransactionFilter();
        tf.setTag(Transaction.Tag.TAX_RELATED);
        matches = MintUtils.findTransactions(tf, transList);
        StringBuilder taxRelated = new StringBuilder();
        for (Transaction t : matches) {
            taxRelated.append(t.toString(false)).append("\n");
        }
        reportLines.add(taxRelated.toString());

        reportLines.add("Music-Expense");
        reportLines.add("*************");
        tf = new TransactionFilter();
        tf.setTag(Transaction.Tag.MUSIC_EXP);
        matches = MintUtils.findTransactions(tf, transList);
        StringBuilder musicExpense = new StringBuilder();
        for (Transaction t : matches) {
            musicExpense.append(t.toString(false)).append("\n");
        }
        reportLines.add(musicExpense.toString());

        reportLines.add("Music-Related");
        reportLines.add("*************");
        tf = new TransactionFilter();
        tf.setTag(Transaction.Tag.MUSIC_RELATED);
        matches = MintUtils.findTransactions(tf, transList);
        StringBuilder musicRelated = new StringBuilder();
        for (Transaction t : matches) {
            musicRelated.append(t.toString(false)).append("\n");
        }
        reportLines.add(musicRelated.toString());

        reportLines.add("Donation");
        reportLines.add("********");
        tf = new TransactionFilter();
        tf.setTag(Transaction.Tag.DONATION);
        matches = MintUtils.findTransactions(tf, transList);
        StringBuilder donation = new StringBuilder();
        for (Transaction t : matches) {
            donation.append(t.toString(false)).append("\n");
        }
        reportLines.add(donation.toString());

        Util.writeFile(mintFilesFolder + year + "_report.list", reportLines);
    }

    private static Map<GroupFilter.AmountLabel, Integer> computeReportData(List<GroupFilter> groupFilters,
                                                                           List<Transaction> transList) {
        Map<GroupFilter.AmountLabel, Integer> totalsMap = new HashMap<>();
        int q1Total = 0, q2Total = 0, q3Total = 0, q4Total = 0;

        for (GroupFilter gf : groupFilters) {
            List<Transaction> matches;
            int q1Amt = 0, q2Amt = 0, q3Amt = 0, q4Amt = 0;

            gf.setMonth(0);
            matches = MintUtils.findTransactions(gf, transList);
            q1Amt += MintUtils.computeTotal(matches);

            gf.setMonth(1);
            matches = MintUtils.findTransactions(gf, transList);
            q1Amt += MintUtils.computeTotal(matches);

            gf.setMonth(2);
            matches = MintUtils.findTransactions(gf, transList);
            q1Amt += MintUtils.computeTotal(matches);
            //
            gf.setAmount(GroupFilter.AmountLabel.Q1, q1Amt);
            q1Total += q1Amt;

            gf.setMonth(3);
            matches = MintUtils.findTransactions(gf, transList);
            q2Amt += MintUtils.computeTotal(matches);

            gf.setMonth(4);
            matches = MintUtils.findTransactions(gf, transList);
            q2Amt += MintUtils.computeTotal(matches);

            gf.setMonth(5);
            matches = MintUtils.findTransactions(gf, transList);
            q2Amt += MintUtils.computeTotal(matches);
            //
            gf.setAmount(GroupFilter.AmountLabel.Q2, q2Amt);
            q2Total += q2Amt;

            gf.setMonth(6);
            matches = MintUtils.findTransactions(gf, transList);
            q3Amt += MintUtils.computeTotal(matches);

            gf.setMonth(7);
            matches = MintUtils.findTransactions(gf, transList);
            q3Amt += MintUtils.computeTotal(matches);

            gf.setMonth(8);
            matches = MintUtils.findTransactions(gf, transList);
            q3Amt += MintUtils.computeTotal(matches);
            //
            gf.setAmount(GroupFilter.AmountLabel.Q3, q3Amt);
            q3Total += q3Amt;

            gf.setMonth(9);
            matches = MintUtils.findTransactions(gf, transList);
            q4Amt += MintUtils.computeTotal(matches);

            gf.setMonth(10);
            matches = MintUtils.findTransactions(gf, transList);
            q4Amt += MintUtils.computeTotal(matches);

            gf.setMonth(11);
            matches = MintUtils.findTransactions(gf, transList);
            q4Amt += MintUtils.computeTotal(matches);
            //
            gf.setAmount(GroupFilter.AmountLabel.Q4, q4Amt);
            q4Total += q4Amt;

            gf.setAmount(GroupFilter.AmountLabel.Total, q1Amt + q2Amt + q3Amt + q4Amt);
        }
        totalsMap.put(GroupFilter.AmountLabel.Q1, q1Total);
        totalsMap.put(GroupFilter.AmountLabel.Q2, q2Total);
        totalsMap.put(GroupFilter.AmountLabel.Q3, q3Total);
        totalsMap.put(GroupFilter.AmountLabel.Q4, q4Total);
        totalsMap.put(GroupFilter.AmountLabel.Total, q1Total + q2Total + q3Total + q4Total);

        return totalsMap;
    }


}
