package com.example.chandora.mynotes;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Main2Activity extends AppCompatActivity {

    private EditText notesEditText;
    private String noteData = null;
    private int TAG = 1;
    private int noteId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        notesEditText = (EditText) findViewById(R.id.notesEditText);

        setTitle("");

        Bundle data = getIntent().getExtras();

        if (data != null) {
            noteData = data.getString("NOTE");
            TAG = data.getInt("TAG");
            noteId = data.getInt("ID");

        }

        if (TAG == 0) {
            notesEditText.setText(noteData);
        }

    }

    private void insertNote() {

        String notesString = notesEditText.getText().toString().trim();

        DbHelper mDbHelper = new DbHelper(this);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NotesContract.NotesEntry.COLUMN_NAME, notesString);
        values.put(NotesContract.NotesEntry.CURRENT_TIME, getCurrentTime());

        if (!TextUtils.isEmpty(notesString)) {

            db.insertWithOnConflict(NotesContract.NotesEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        } else {
            Toast.makeText(this, "Empty note", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateNote() {

        String notesString = notesEditText.getText().toString().trim();

        DbHelper mDbHelper = new DbHelper(this);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NotesContract.NotesEntry.COLUMN_NAME, notesString);
        values.put(NotesContract.NotesEntry.CURRENT_TIME, getCurrentTime());

        String selection = NotesContract.NotesEntry._ID + " = ?";

        if (!TextUtils.isEmpty(notesString)) {
            db.update(NotesContract.NotesEntry.TABLE_NAME, values, selection, new String[]{String.valueOf(noteId)});
        }

    }

    private String getCurrentTime() {

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("MMM dd,yyyy  hh:mm:ss a");
        String date = format.format(currentTime);
        return date;

    }

    private void deleteNote() {

        String selection = NotesContract.NotesEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(noteId)};
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(NotesContract.NotesEntry.TABLE_NAME, selection, selectionArgs);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.editor_menu, menu);

        if (TAG == 1) {
            menu.removeItem(R.id.action_delete);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save:
                if (TAG == 0) {
                    updateNote();
                } else {
                    insertNote();
                }
                finish();
                return true;

            case R.id.action_delete:
                deleteNote();
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (TAG == 0) {
            updateNote();
        } else {
            insertNote();
        }
        finish();
    }
}
