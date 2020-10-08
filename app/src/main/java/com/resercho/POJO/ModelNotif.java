package com.resercho.POJO;

public class ModelNotif {
    String id, profUrl,profName, title,sendId,type,src,time, checked,opened;

    public ModelNotif() {
    }

    public ModelNotif(String id, String profUrl, String sendId, String type, String src, String time, String checked, String opened) {
        this.id = id;
        this.profUrl = profUrl;
        this.sendId = sendId;
        this.type = type;
        this.src = src;
        this.time = time;
        this.checked = checked;
        this.opened = opened;
    }

    public String getProfName() {
        return profName;
    }

    public void setProfName(String profName) {
        this.profName = profName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfUrl() {
        return profUrl;
    }

    public void setProfUrl(String profUrl) {
        this.profUrl = profUrl;
    }

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String sendId) {
        this.sendId = sendId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public String getOpened() {
        return opened;
    }

    public void setOpened(String opened) {
        this.opened = opened;
    }
}
