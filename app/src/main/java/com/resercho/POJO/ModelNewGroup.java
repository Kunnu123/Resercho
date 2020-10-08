package com.resercho.POJO;

public class ModelNewGroup {
    private String name, about, friends, catid, image;
    boolean privateGroup;

    // After creation
    int gid;
    boolean university;

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public boolean isUniversity() {
        return university;
    }

    public void setUniversity(boolean university) {
        this.university = university;
    }

    public ModelNewGroup() {
    }

    public ModelNewGroup(String name, String about, String friends, String catid, String image) {
        this.name = name;
        this.about = about;
        this.friends = friends;
        this.catid = catid;
        this.image = image;
    }

    public boolean isPrivateGroup() {
        return privateGroup;
    }

    public void setPrivateGroup(boolean privateGroup) {
        this.privateGroup = privateGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        this.friends = friends;
    }

    public String getCatid() {
        return catid;
    }

    public void setCatid(String catid) {
        this.catid = catid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
