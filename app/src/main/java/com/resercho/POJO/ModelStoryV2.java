package com.resercho.POJO;

public class ModelStoryV2 {
    String added_by,count,username,profUrl;

    public ModelStoryV2() {
    }

    public ModelStoryV2(String added_by, String count, String username, String profUrl) {
        this.added_by = added_by;
        this.count = count;
        this.username = username;
        this.profUrl = profUrl;
    }

    public String getAdded_by() {
        return added_by;
    }

    public void setAdded_by(String added_by) {
        this.added_by = added_by;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfUrl() {
        return profUrl;
    }

    public void setProfUrl(String profUrl) {
        this.profUrl = profUrl;
    }
}
