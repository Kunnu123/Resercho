package com.resercho.POJO;

public class ModelGroupMsg {
    String id,gid,msg,attime,uid;
    boolean sending;

    public boolean isSending() {
        return sending;
    }

    public void setSending(boolean sending) {
        this.sending = sending;
    }

    public ModelGroupMsg() {
    }

    public ModelGroupMsg(String id, String gid, String msg, String attime, String uid) {
        this.id = id;
        this.gid = gid;
        this.msg = msg;
        this.attime = attime;
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getAttime() {
        return attime;
    }

    public void setAttime(String attime) {
        this.attime = attime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
