package com.example.note;

public class Note {

    private String title;
    private String content;
    private String userId;
    public Note(String title, String content, String userId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
    }
    public Note() {
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

