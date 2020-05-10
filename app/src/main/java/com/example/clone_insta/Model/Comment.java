package com.example.clone_insta.Model;

public class Comment {
    private  String id;
    private String comment;
    private String publishers;


    public Comment() {
    }

    public Comment(String id, String comment, String publishers) {
        this.id = id;
        this.comment = comment;
        this.publishers = publishers;
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

    public String getPublishers() {
        return publishers;
    }

    public void setPublishers(String publishers) {
        this.publishers = publishers;
    }
}
