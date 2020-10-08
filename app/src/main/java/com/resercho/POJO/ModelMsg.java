package com.resercho.POJO;

public class ModelMsg {
    String id, msg, time, status,del, from, to;
    boolean sending;

    public ModelMsg() {
    }

    public ModelMsg(String id, String msg, String time, String status, String del) {
        this.sending =false;
        this.id = id;
        this.msg = msg;
        this.time = time;
        this.status = status;
        this.del = del;
    }

    public String getFrom() {
        return from;
    }

    public boolean isSending() {
        return sending;
    }

    public void setSending(boolean sending) {
        this.sending = sending;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDel() {
        return del;
    }

    public void setDel(String del) {
        this.del = del;
    }
}
