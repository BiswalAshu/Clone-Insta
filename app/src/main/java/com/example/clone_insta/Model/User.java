package com.example.clone_insta.Model;

public class User {
    private String name,uesrname,bio,email,imageurl,id;

    public User() {
    }

    public User(String name, String uesrName, String bio, String email, String imageurl, String id) {
        this.name = name;
        this.uesrname = uesrName;
        this.bio = bio;
        this.email = email;
        this.imageurl = imageurl;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUesrName() {
        return uesrname;
    }

    public void setUesrName(String uesrName) {
        this.uesrname = uesrName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
