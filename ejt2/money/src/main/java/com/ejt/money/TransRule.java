package com.ejt.money;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class TransRule {
    private String id;
    private String label;
    private String category;
    private String[] matches;
    private String tags;
    private boolean debit = true;
    private AccountEnum account;
    private boolean needsComment = false;
    private boolean needsTag = false;
    private boolean needsFrom = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String[] getMatches() {
        return matches;
    }

    public void assignMatches(String matches) {
        this.matches = StringUtils.split(matches, ',');
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public boolean isDebit() {
        return debit && !"Transfer".equals(category);
    }

    public boolean isCredit() {
        return !debit && !"Transfer".equals(category);
    }

    public void setDebit(boolean debit) {
        this.debit = debit;
    }

    public AccountEnum getAccount() {
        return account;
    }

    public void setAccount(AccountEnum account) {
        this.account = account;
    }

    public boolean isNeedsComment() {
        return needsComment;
    }

    public void setNeedsComment(boolean needsComment) {
        this.needsComment = needsComment;
    }

    public boolean isNeedsTag() {
        return needsTag;
    }

    public void setNeedsTag(boolean needsTag) {
        this.needsTag = needsTag;
    }

    public boolean isNeedsFrom() {
        return needsFrom;
    }

    public void setNeedsFrom(boolean needsFrom) {
        this.needsFrom = needsFrom;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
