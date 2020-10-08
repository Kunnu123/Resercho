package com.resercho.POJO;

import java.util.List;

public class ModelWork {
    String id, heading,status,audience,post_of,media_src,cover_img,type,filtertype,place,category,typework,time,username,profpic,userBio;
    boolean like, saved, denabled, senabled;

    List<String> pdflist;
    List<String> imglist;
    String author, doi,dop;

    long likes;
    long comments;
    long savedCount;
    long citeCount;

    public long getShareCount() {
        return shareCount;
    }

    public void setShareCount(long shareCount) {
        this.shareCount = shareCount;
    }

    long shareCount;

    public long getSavedCount() {
        return savedCount;
    }

    public void setSavedCount(long savedCount) {
        this.savedCount = savedCount;
    }

    public long getCiteCount() {
        return citeCount;
    }

    public void setCiteCount(long citeCount) {
        this.citeCount = citeCount;
    }

    public ModelWork() {
    }

    public ModelWork(String id, String heading, String status, String audience, String post_of, String media_src, String cover_img, String type, String filtertype, String place, String category, String typework, String time, String username, boolean like) {
        this.id = id;
        this.heading = heading;
        this.status = status;
        this.audience = audience;
        this.post_of = post_of;
        this.media_src = media_src;
        this.cover_img = cover_img;
        this.type = type;
        this.filtertype = filtertype;
        this.place = place;
        this.category = category;
        this.typework = typework;
        this.time = time;
        this.username = username;
        this.like = like;
    }

    public List<String> getPdflist() {
        return pdflist;
    }

    public void setPdflist(List<String> pdflist) {
        this.pdflist = pdflist;
    }

    public List<String> getImglist() {
        return imglist;
    }

    public void setImglist(List<String> imglist) {
        this.imglist = imglist;
    }

    public long getLikes() {
        return likes;
    }


    public void setLikes(long likes) {
        this.likes = likes;
    }

    public long getComments() {
        return comments;
    }

    public void setComments(long comments) {
        this.comments = comments;
    }

    public boolean isDenabled() {
        return denabled;
    }

    public void setDenabled(boolean denabled) {
        this.denabled = denabled;
    }

    public boolean isSenabled() {
        return senabled;
    }

    public void setSenabled(boolean senabled) {
        this.senabled = senabled;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getDop() {
        return dop;
    }

    public void setDop(String dop) {
        this.dop = dop;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public String getProfpic() {
        return profpic;
    }

    public void setProfpic(String profpic) {
        this.profpic = profpic;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getPost_of() {
        return post_of;
    }

    public void setPost_of(String post_of) {
        this.post_of = post_of;
    }

    public String getMedia_src() {
        return media_src;
    }

    public void setMedia_src(String media_src) {
        this.media_src = media_src;
    }

    public String getCover_img() {
        return cover_img;
    }

    public void setCover_img(String cover_img) {
        this.cover_img = cover_img;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFiltertype() {
        return filtertype;
    }

    public void setFiltertype(String filtertype) {
        this.filtertype = filtertype;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTypework() {
        return typework;
    }

    public void setTypework(String typework) {
        this.typework = typework;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
