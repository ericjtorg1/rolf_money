package com.ejt.money;

public class TransactionFilter {

    private Transaction.Type type;
    private String category;
    private Transaction.Tag tag;
    private Transaction.Tag notTag1, notTag2;
    private String description;
    private String groupName;
    // 0=Jan ... 11=Dec
    private Integer month;
    private Boolean noTags;

    public Transaction.Type getType() {
        return type;
    }

    public void setType(Transaction.Type type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String catgory) {
        this.category = catgory;
    }

    public Transaction.Tag getTag() {
        return tag;
    }

    public void setTag(Transaction.Tag tag) {
        this.tag = tag;
    }

    public Transaction.Tag getNotTag1() {
        return notTag1;
    }

    public void setNotTag1(Transaction.Tag notTag1) {
        this.notTag1 = notTag1;
    }

    public Transaction.Tag getNotTag2() {
        return notTag2;
    }

    public void setNotTag2(Transaction.Tag notTag2) {
        this.notTag2 = notTag2;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Boolean getNoTags() {
        return noTags;
    }

    public void setNoTags(Boolean noTags) {
        this.noTags = noTags;
    }
}
