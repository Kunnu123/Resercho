package com.resercho.POJO;

public class ModelChat {
    String mid, from_id, to_id, username, profile,time,status,del_status, lastMsg;

    public ModelChat(String mid, String from_id, String to_id, String username, String profile, String time, String status, String del_status) {
        this.mid = mid;
        this.from_id = from_id;
        this.to_id = to_id;
        this.username = username;
        this.profile = profile;
        this.time = time;
        this.status = status;
        this.del_status = del_status;
    }

    public ModelChat() {
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
    }

    public String getTo_id() {
        return to_id;
    }

    public void setTo_id(String to_id) {
        this.to_id = to_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
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

    public String getDel_status() {
        return del_status;
    }

    public void setDel_status(String del_status) {
        this.del_status = del_status;
    }
}
