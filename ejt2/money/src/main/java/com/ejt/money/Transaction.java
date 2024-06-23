package com.ejt.money;

import com.ejt.util.CalendarUtil;
import com.ejt.util.Util;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Transaction {

    public enum Type {
        INCOME("I"), EXPENSE("E"), TRANSFER("T");

        private final String label;

        private Type(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static Type determineTypeFromLabel(String label) throws Exception {
            if (INCOME.getLabel().equals(label)) {
                return INCOME;
            } else if (EXPENSE.getLabel().equals(label)) {
                return EXPENSE;
            } else if (TRANSFER.getLabel().equals(label)) {
                return TRANSFER;
            }
            throw new Exception("Invalid Transaction Type Label: " + label);
        }

        public static Type determineType(String transactionType, String category) {
            if ("Credit Card Payment".equals(category)) {
                return TRANSFER;
            }
            if ("Transfer for Cash Spending".equals(category)) {
                return TRANSFER;
            }
            if ("Cash".equals(category)) {
                return TRANSFER;
            }
            if ("Transfer".equals(category)) {
                return TRANSFER;
            }
            if ("credit".equals(transactionType)) {
                return INCOME;
            }
            return EXPENSE;
        }
    }

    public enum Tag {
        VACATION("VACATION"), ALTHEA_EXP("ALTHEA_EXP"),
        REIMBURSABLE("REIMBURSABLE"), MUSIC_EXP("MUSIC_EXP"),
        TAX_RELATED("TAX_RELATED"), DONATION("DONATION"), MUSIC_RELATED("MUSIC_RELATED");

        private final String value;

        private Tag(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Tag determineTag(String name) throws Exception {
            try {
                return Tag.valueOf(name);
            } catch (IllegalArgumentException e) {
                throw new Exception("Invalid Tag name: " + name);
            }
        }
    }

    private int id;
    private Calendar date;
    private int amount;
    private String category;
    private Type type;
    private String notes;
    private String account;
    private Set<Tag> tags = new HashSet<>();
    private String description;
    private String original;
    private String origType;

    public int getId() {
        return id;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public boolean hasTag(Tag tag) {
        return tags != null && tags.contains(tag);
    }

    public void addTags(String tagList) throws BadRecordException {
        Set<String> tagSet = Util.convertToStringSet(tagList);
        for (String tag : tagSet) {
            try {
                tags.add(Transaction.Tag.determineTag(tag));
            } catch (Exception e) {
                throw new BadRecordException("invalid tags " + tagList);
            }
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOriginal() {
        return original;
    }

    public String getOrigType() {
        return origType;
    }

    public boolean matchesQuarter(String qtr) {
        if (date == null) {
            return false;
        }
        int month = date.get(Calendar.MONTH);

        if ("Q1".equals(qtr)) {
            return month == 0 || month == 1 || month == 2;
        } else if ("Q2".equals(qtr)) {
            return month == 3 || month == 4 || month == 5;
        } else if ("Q3".equals(qtr)) {
            return month == 6 || month == 7 || month == 8;
        } else if ("Q4".equals(qtr)) {
            return month == 9 || month == 10 || month == 11;
        } else {
            throw new IllegalArgumentException("Bad input Quarter value: " + qtr);
        }
    }

    @Override
    public int hashCode() {
        return id;
    }


    public static Transaction createFromLines(List<String> lines, int id) throws Exception {
        if (lines == null || lines.size() < 2) {
            throw new Exception("create: expected at least 2 lines");
        }
        Transaction trans = new Transaction();
        trans.id = id;

        String date = lines.get(0).substring(0, "MM-dd-yyyy".length());
        try {
            trans.date = CalendarUtil.parse(date, "MM-dd-yyyy");
        } catch (IllegalArgumentException iae) {
            throw new Exception("create: expected valid date (MM-dd-yyyy) at start of line 1");
        }
        trans.type = Type.determineTypeFromLabel(lines.get(0).substring(13, 14));
        trans.amount = MintUtils.determineCents(lines.get(0).substring(14, 26));
        trans.description = lines.get(0).substring(29).trim();

        trans.category = lines.get(1).substring(0, 29).trim();
        trans.tags = new HashSet<>();

        if (lines.get(1).length() > 51) {
            trans.account = lines.get(1).substring(29, 51).trim();
            Set<String> tagSet = Util.convertToStringSet(lines.get(1).substring(51).trim());
            for (String tag : tagSet) {
                trans.tags.add(Tag.determineTag(tag));
            }
        } else {
            trans.account = lines.get(1).substring(29).trim();
        }

        if (lines.size() > 2) {
            trans.notes = lines.get(2).trim();
        }

        return trans;
    }

    public static Transaction create(List<String> lineTokens, int id) throws Exception {
        if (lineTokens == null || lineTokens.size() != 9) {
            throw new Exception("create: expected 9 tokens");
        }
        Transaction trans = new Transaction();
        trans.id = id;

        try {
            trans.date = CalendarUtil.parse(lineTokens.get(0), "MM/dd/yyyy");
        } catch (IllegalArgumentException iae) {
            throw new Exception("create: expected valid date (MM/dd/yyyy) in token 1");
        }
        trans.description = lineTokens.get(1);
        trans.original = lineTokens.get(2);
        trans.amount = MintUtils.determineCents(lineTokens.get(3));
        trans.category = lineTokens.get(5);
        trans.account = lineTokens.get(6);
        trans.notes = lineTokens.get(8);
        trans.type = Type.determineType(lineTokens.get(4), trans.category);
        trans.tags = MintUtils.determineTags(lineTokens.get(7));
        trans.origType = lineTokens.get(4);
        return trans;
    }

    public String toString() {
        return toString(false);
    }

    public String toString(boolean showOriginal) {
        StringBuilder sb = new StringBuilder();
        sb.append(CalendarUtil.toString(date, "MM-dd-yyyy"));
        Util.appendPadString(sb, type.getLabel(), 4, true);
        Util.appendPadString(sb, MintUtils.determineAmount(amount), 12, true);
        sb.append("   ");
        sb.append(description);
        sb.append("\n");
        if (showOriginal) {
            sb.append(original);
            sb.append("\n");
        }
        Util.appendPadString(sb, category, 29, false);
        if (tags.isEmpty()) {
            sb.append(account);
        } else {
            StringBuilder tagList = new StringBuilder();
            boolean firstOne = true;
            for (Tag tag : tags) {
                if (!firstOne) {
                    tagList.append(",");
                }
                firstOne = false;
                tagList.append(tag.name());
            }
            Util.appendPadString(sb, account, 22, false);
            sb.append(tagList);
        }
        sb.append("\n");
        if (StringUtils.isNotBlank(notes)) {
            sb.append(notes);
            sb.append("\n");
        }
        return sb.toString();
    }

}
