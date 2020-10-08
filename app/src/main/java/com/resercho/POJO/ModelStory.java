package com.resercho.POJO;

public class ModelStory {
//    String id,ownerid,imageurl,timestamp,ownerName,ownerImageUrl;

    String id, image, time, fullname, profile;

    public ModelStory() {
    }

    public ModelStory(String id, String image, String time) {
        this.id = id;
        this.image = image;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
