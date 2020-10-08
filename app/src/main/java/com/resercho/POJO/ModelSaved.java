package com.resercho.POJO;

public class ModelSaved {
    String id,uid,pid;
    ModelWork work;

    public ModelSaved() {
    }

    public ModelSaved(String id, String uid, String pid, ModelWork work) {
        this.id = id;
        this.uid = uid;
        this.pid = pid;
        this.work = work;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public ModelWork getWork() {
        return work;
    }

    public void setWork(ModelWork work) {
        this.work = work;
    }
}
