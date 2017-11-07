package com.example.chandora.mynotes;

import android.provider.BaseColumns;

/**
 * Created by kpchandora on 6/10/17.
 */

public final class NotesContract {

    private NotesContract(){

    }

    public static final class NotesEntry implements BaseColumns{

        public static final String TABLE_NAME = "notes_new_table";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_NAME = "note";

        public static final String FIREBASE_KEY = "firebase_key";

    }

}
