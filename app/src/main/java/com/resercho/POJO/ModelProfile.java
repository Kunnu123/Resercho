package com.resercho.POJO;

public class ModelProfile {
    private String profUrl, uname, since, bio, education, website, cites, followers,posts;

    public ModelProfile() {
    }

    public ModelProfile(String profUrl, String uname, String since, String bio, String education, String website) {
        this.profUrl = profUrl;
        this.uname = uname;
        this.since = since;
        this.bio = bio;
        this.education = education;
        this.website = website;
    }


    public String getCites() {
        return cites;
    }

    public void setCites(String cites) {
        this.cites = cites;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getPosts() {
        return posts;
    }

    public void setPosts(String posts) {
        this.posts = posts;
    }

    public String getProfUrl() {
        return profUrl;
    }

    public void setProfUrl(String profUrl) {
        this.profUrl = profUrl;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getSince() {
        return since;
    }

    public void setSince(String since) {
        this.since = since;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
