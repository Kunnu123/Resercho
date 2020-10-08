package com.resercho.POJO;

public class ModelCitation {

    String id, doneby,owner,pid,timestamp;

    public ModelCitation() {
    }

    public ModelCitation(String id, String doneby, String owner, String pid, String timestamp) {
        this.id = id;
        this.doneby = doneby;
        this.owner = owner;
        this.pid = pid;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDoneby() {
        return doneby;
    }

    public void setDoneby(String doneby) {
        this.doneby = doneby;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
