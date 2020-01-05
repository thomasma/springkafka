package com.aver.model;

public class Message {
    private String text;

    public Message() {
    }

    public Message(String message) {
        this.text = message;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Message [message=" + text + "]";
    }

}