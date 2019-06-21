package com.project.memowithnfc.vo;

import java.io.Serializable;

public class Memo implements Serializable {
    private int id;
    private String date;
    private String time;
    private String content;
    private int alarmSetting;
    private int category_id;
    private String category_name;

    public Memo() {}

    public Memo(String date, String time, String content, int alarmSetting, int category_id) {
        this.date = date;
        this.time = time;
        this.content = content;
        this.alarmSetting = alarmSetting;
        this.category_id = category_id;
    }

    public Memo(int id, String date, String time, String content, int alarmSetting, int category_id, String category_name) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.content = content;
        this.alarmSetting = alarmSetting;
        this.category_id = category_id;
        this.category_name = category_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getAlarmSetting() {
        return alarmSetting;
    }

    public void setAlarmSetting(int alarmSetting) {
        this.alarmSetting = alarmSetting;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() { return category_name; }

    public void setCategory_name(String category_name) {this.category_name = category_name;}
}
