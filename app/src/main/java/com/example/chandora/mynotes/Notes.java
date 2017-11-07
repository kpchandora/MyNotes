package com.example.chandora.mynotes;

/**
 * Created by kpchandora on 7/10/17.
 */

public class Notes {

    private int id;
    private String data;
    private String firebaseKey;

    public Notes(){

    }

    public Notes(int id, String data, String firebaseKey) {
        this.id = id;
        this.data = data;
        this.firebaseKey = firebaseKey;

    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public int getId() {
        return id;
    }

    public String getData() {
        return data;
    }


}
