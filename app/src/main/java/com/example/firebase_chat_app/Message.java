package com.example.firebase_chat_app;

public class Message {
    String type;
    String message;
    String from;
    String imgUrl;

    public Message(String message, String from, String imgUrl, String type) {
        this.message = message;
        this.from = from;
        this.imgUrl = imgUrl;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Message() {
    }
}
