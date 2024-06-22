package com.ejt.money;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.Calendar;

public class TransRecord {
    private AccountEnum account;
    private Calendar date;
    private int amount;
    private String description;

    private String category;
    private String comment;
    private String tags;
    private boolean useFrom = false;
    private Boolean notDebit;
    private boolean amtNegative;
    private boolean debitNegativeAmt = false;

    public AccountEnum getAccount() {
        return account;
    }

    public void setAccount(AccountEnum account) {
        this.account = account;
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
        if (amount < 0) {
            amtNegative = true;
        } else {
            amtNegative = false;
        }
        this.amount = Math.abs(amount);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public boolean isUseFrom() {
        return useFrom;
    }

    public void setUseFrom(boolean useFrom) {
        this.useFrom = useFrom;
    }

    public Boolean getNotDebit() {
        return notDebit;
    }

    public void setNotDebit(Boolean notDebit) {
        this.notDebit = notDebit;
    }

    public boolean isAmtNegative() {
        return amtNegative;
    }

    public void setAmtNegative(boolean amtNegative) {
        this.amtNegative = amtNegative;
    }

    public boolean isDebitNegativeAmt() {
        return debitNegativeAmt;
    }

    public void setDebitNegativeAmt(boolean debitNegativeAmt) {
        this.debitNegativeAmt = debitNegativeAmt;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
