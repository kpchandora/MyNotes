package com.example.chandora.mynotes;

import java.util.ArrayList;

/**
 * Created by kpchandora on 4/11/17.
 */

public class UserNotes {

    private String userNoteId;
    private String userNote;

    public UserNotes(){

    }

    public UserNotes(String userNote, String userNoteId) {
        this.userNoteId = userNoteId;
        this.userNote = userNote;
    }


    public String getUserNoteId() {
        return userNoteId;
    }

    public String getUserNote() {
        return userNote;
    }

}
