package com.eightbitpanda.lens.helper;


public class HistoryItem {


    String id, type, text, time;

    public HistoryItem() {

    }

    public HistoryItem(String type, String text, String time) {
        this.type = type;
        this.text = text;
        this.time = time;
    }

    public HistoryItem(String id, String type, String text, String time) {
        this.id = id;
        this.type = type;
        this.text = text;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
