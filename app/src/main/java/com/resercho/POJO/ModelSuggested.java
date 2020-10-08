package com.resercho.POJO;

public class ModelSuggested {
    String id, username, profUrl, fullname;
    boolean sentRequest;

    public ModelSuggested() {
    }

    public ModelSuggested(String id, String username, String profUrl, String fullname, boolean sentRequest) {
        this.id = id;
        this.username = username;
        this.profUrl = profUrl;
        this.fullname = fullname;
        this.sentRequest = sentRequest;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public boolean isSentRequest() {
        return sentRequest;
    }

    public void setSentRequest(boolean sentRequest) {
        this.sentRequest = sentRequest;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
