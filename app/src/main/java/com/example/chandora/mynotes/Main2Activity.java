package com.example.chandora.mynotes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.speech.tts.TextToSpeech;
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
import java.util.Locale;

public class Main2Activity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private EditText notesEditText;
    private String noteData = null;
    private int TAG = 1;
    private int noteId = 0;

    private TextToSpeech mTTS;
    private int MY_DATA_CHECK_CODE = 0;

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

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

    }

    private void speakWords(String data){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTTS.speak(data, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            mTTS.speak(data, TextToSpeech.QUEUE_FLUSH, null);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                mTTS = new TextToSpeech(this, this);
            }
        } else {

            Intent installTTSIntent = new Intent();
            installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            startActivity(installTTSIntent);

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

            case R.id.text_to_speech:
                speakWords(notesEditText.getText().toString());
                break;
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

    @Override
    public void onInit(int initStatus) {
        if (initStatus == TextToSpeech.SUCCESS){
            if (mTTS.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE){
                mTTS.setLanguage(Locale.US);
            }
        }else if (initStatus == TextToSpeech.ERROR){
            Toast.makeText(this, "Sorry! Text To Speech is failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTTS.shutdown();
    }
}
