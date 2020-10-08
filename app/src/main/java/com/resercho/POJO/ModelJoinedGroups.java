package com.resercho.POJO;

public class ModelJoinedGroups {
    String id,uid,gid,status,request_by, timestamp,name,image,about;

    public ModelJoinedGroups() {
    }

    public ModelJoinedGroups(String id, String uid, String gid, String status, String request_by, String timestamp, String name, String image, String about) {
        this.id = id;
        this.uid = uid;
        this.gid = gid;
        this.status = status;
        this.request_by = request_by;
        this.timestamp = timestamp;
        this.name = name;
        this.image = image;
        this.about = about;
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

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequest_by() {
        return request_by;
    }

    public void setRequest_by(String request_by) {
        this.request_by = request_by;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public static class ModelComment {
        String id, comment, comname, comid,time,pid, comprof;

        public ModelComment() {
        }

        public ModelComment(String id, String comment, String comname, String comid, String time, String pid, String comprof) {
            this.id = id;
            this.comment = comment;
            this.comname = comname;
            this.comid = comid;
            this.time = time;
            this.pid = pid;
            this.comprof = comprof;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getComname() {
            return comname;
        }

        public void setComname(String comname) {
            this.comname = comname;
        }

        public String getComid() {
            return comid;
        }

        public void setComid(String comid) {
            this.comid = comid;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getComprof() {
            return comprof;
        }

        public void setComprof(String comprof) {
            this.comprof = comprof;
        }
    }
}
