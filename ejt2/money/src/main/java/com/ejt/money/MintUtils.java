package com.ejt.money;

import com.ejt.util.PropertyManager;
import com.ejt.util.Util;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class MintUtils {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MintUtils.class);

    private MintUtils() {
    }

    public static int determineCents(String amount) throws Exception {
        if (amount == null) {
            throw new Exception("amount is null");
        }
        StringBuffer sb = new StringBuffer();
        int leng = amount.length();
        for (int i = 0; i < leng; i++) {
            String str = amount.substring(i, i + 1);
            if (StringUtils.isBlank(str) || ".".equals(str) || ",".equals(str)) {
                continue;
            }
            sb.append(str);
        }
        try {
            return Integer.parseInt(sb.toString());
        } catch (NumberFormatException nfe) {
            throw new Exception("amount is invalid: " + amount);
        }
    }

    public static String formatAmount(int cents) {
        String amt = String.valueOf(cents);
        int leng = amt.length();
        if (leng < 3) {
            return "0";
        }
        return amt.substring(0, leng - 2);
    }


    public static String determineAmount(int cents) {
        String amt = String.valueOf(cents);
        int leng = amt.length();
        if (leng == 1) {
            return "0.0" + amt;
        }
        if (leng == 2) {
            return "0." + amt;
        }
        return amt.substring(0, leng - 2) + "." + amt.substring(leng - 2);
    }

    public static Set<Transaction.Tag> determineTags(String tagsString) throws Exception {
        if (tagsString == null) {
            throw new Exception("tags is null");
        }
        Set<Transaction.Tag> tags = new HashSet<>();
        int startIndex = 0;

        while (StringUtils.isNotBlank(tagsString.substring(startIndex))) {
            Transaction.Tag matchingTag = null;
            for (Transaction.Tag tag : Transaction.Tag.values()) {
                if (tagsString.substring(startIndex).startsWith(tag.getValue())) {
                    matchingTag = tag;
                    break;
                }
            }
            if (matchingTag == null) {
                throw new Exception("tags is invalid: " + tagsString);
            }
            tags.add(matchingTag);
            startIndex += matchingTag.getValue().length();
            if (startIndex >= tagsString.length()) {
                break;
            }
            if (!tagsString.substring(startIndex).startsWith(" ")) {
                throw new Exception("tags is invalid: " + tagsString);
            }
            startIndex++;
            if (startIndex >= tagsString.length()) {
                break;
            }
        }
        return tags;
    }

    public static List<String> getPropertyValues(String prefix) {
        List<String> propValues = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            String value = PropertyManager.getString(prefix + i, null);
            if (value != null) {
                propValues.add(value);
            }
        }
        return propValues;
    }

    public static List<Transaction> findTransactions(TransactionFilter transFilter, List<Transaction> transList) {
        List<Transaction> matches = new ArrayList<>();
        GroupFilter gf = new GroupFilter();
        gf.setType(transFilter.getType());
        gf.setMonth(transFilter.getMonth());

        for (Transaction t : transList) {
            if (transactionMatches(t, gf, transFilter)) {
                matches.add(t);
            }
        }
        return matches;
    }

    public static List<Transaction> findTransactions(GroupFilter groupFilter, List<Transaction> transList) {
        List<Transaction> matches = new ArrayList<>();

        trans_loop:
        for (Transaction t : transList) {
            for (TransactionFilter tf : groupFilter.getFilters()) {
                if (transactionMatches(t, groupFilter, tf)) {
                    matches.add(t);
                    continue trans_loop;
                }
            }
        }
        return matches;
    }

    public static boolean doesTransactionMatch(Transaction trans, GroupFilter groupFilter) {
        for (TransactionFilter tf : groupFilter.getFilters()) {
            if (transactionMatches(trans, groupFilter, tf)) {
                return true;
            }
        }
        return false;
    }

    private static boolean transactionMatches(Transaction trans, GroupFilter groupFilter, TransactionFilter transFilter) {

        if (groupFilter.getType() != null) {
            if (trans.getType() != groupFilter.getType()) {
                return false;
            }
        }
        if (transFilter.getCategory() != null) {
            if (!Util.isEqual(transFilter.getCategory(), trans.getCategory())) {
                return false;
            }
        }
        if (transFilter.getDescription() != null) {
            if (!Util.isEqual(transFilter.getDescription(), trans.getDescription())) {
                return false;
            }
        }
        if (transFilter.getNoTags() != null) {
            if (trans.getTags().isEmpty() ||
                    (trans.getTags().size() == 1 && trans.getTags().contains(Transaction.Tag.TAX_RELATED))) {
                // no tags
            } else {
                return false;
            }
        }
        if (transFilter.getTag() != null) {
            if (!trans.getTags().contains(transFilter.getTag())) {
                return false;
            }
        }
        if (transFilter.getNotTag1() != null) {
            if (trans.getTags().contains(transFilter.getNotTag1())) {
                return false;
            }
        }
        if (transFilter.getNotTag2() != null) {
            if (trans.getTags().contains(transFilter.getNotTag2())) {
                return false;
            }
        }
        if (groupFilter.getMonth() != null) {
            if (trans.getDate() == null) {
                return false;
            }
            int month = trans.getDate().get(Calendar.MONTH);
            if (month != groupFilter.getMonth().intValue()) {
                return false;
            }
        }
        return true;
    }


    public static int computeTotal(Collection<Transaction> transList) {
        int total = 0;
        for (Transaction t : transList) {
            total += t.getAmount();
        }
        return total;
    }

    //	income.group.1.name=Eric W2
    //	income.group.1.filter.1=CAT:Paycheck#DES:Daugherty


    public static GroupFilter createGroupFilter(String propName, Transaction.Type type) throws Exception {
        GroupFilter groupFilter = new GroupFilter();
        groupFilter.setType(type);

        groupFilter.setGroupName(PropertyManager.getString(propName + ".name", null));
        if (groupFilter.getGroupName() == null) {
            return null;
        }
        List<String> filterList = getPropertyValues(propName + ".filter.");
        if (filterList.isEmpty()) {
            return groupFilter;
        }

        for (String filter : filterList) {
            TransactionFilter tf = new TransactionFilter();
            groupFilter.getFilters().add(tf);

            StringTokenizer st = new StringTokenizer(filter, "#");
            while (st.hasMoreTokens()) {
                String token = st.nextToken();

                if (token.startsWith("CAT:")) {
                    tf.setCategory(token.substring("CAT:".length()));

                } else if (token.startsWith("DES:")) {
                    tf.setDescription(token.substring("DES:".length()));

                } else if (token.startsWith("TAG:")) {
                    String tag = token.substring("TAG:".length());
                    if ("0".equals(tag)) {
                        tf.setNoTags(true);
                    } else {
                        tf.setTag(Transaction.Tag.determineTag(tag));
                    }
                } else {
                    throw new Exception("filter is invalid: " + filter);
                }
            }
        }
        return groupFilter;
    }

    public static int computeMonthly(int total, int numMonths) {
        return Math.round(((float) total) / ((float) numMonths));
    }

    public static TransRule createTransRule(String id) {
        String propName = "trans.rule." + id;

        String match = getPropValue(propName + ".match");
        if (match == null) {
            return null;
        }
        TransRule transRule = new TransRule();
        transRule.assignMatches(getPropValue(propName + ".match"));
        transRule.setId(id);

        transRule.setLabel(getPropValue(propName + ".label"));
        transRule.setCategory(getPropValue(propName + ".cat"));
        transRule.setTags(getPropValue(propName + ".tags"));
        transRule.setDebit(Util.getBoolean(getPropValue(propName + ".debit"), true));
        transRule.setNeedsComment(Util.getBoolean(getPropValue(propName + ".needsComment"), false));

        String account = getPropValue(propName + ".account");
        if (account != null) {
            try {
                transRule.setAccount(AccountEnum.determineAccount(account));
            } catch (Exception e) {
                throw new RuntimeException("Bad Account value " + account + " for trans.rule." + id);
            }
        }
        return transRule;
    }


    public static String getPropValue(String propName) {
        return StringUtils.trimToNull(PropertyManager.getString(propName, null));
    }
}
