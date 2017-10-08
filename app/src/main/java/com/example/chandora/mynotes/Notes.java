package com.example.chandora.mynotes;

/**
 * Created by kpchandora on 7/10/17.
 */

public class Notes {

    private int id;
    private String data;
    private String time;

    public Notes(int id, String data, String time) {
        this.id = id;
        this.data = data;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public String getTime() {
        return time;
    }
}
