package com.example.chandora.mynotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.chandora.mynotes.NotesContract.NotesEntry;


/**
 * Created by kpchandora on 6/10/17.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mynewnotes.db";
    private static final int DB_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_QUERY = "CREATE TABLE " + NotesEntry.TABLE_NAME +
                "( " + NotesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NotesEntry.COLUMN_NAME + " TEXT , " +
                NotesEntry.CURRENT_TIME + " TEXT);";

        db.execSQL(CREATE_TABLE_QUERY);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        String query = "DROP TABLE IF EXISTS " + NotesEntry.TABLE_NAME;

        db.execSQL(query);
        onCreate(db);

    }


}
