package com.example.chandora.mynotes;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.chandora.mynotes.NotesContract.NotesEntry;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private LinearLayout emptyLayout;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        emptyLayout = (LinearLayout) findViewById(R.id.emptyLayout);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
//
//        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
//        recyclerView.setLayoutManager(manager);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Main2Activity.class);
                i.putExtra("TAG", 1);
                startActivity(i);
            }
        });


        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MainActivity.this, "Add new note", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        displayDatabaseInfo();
        registerForContextMenu(recyclerView);

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_menu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int position = -1;
        int idOfNote = 0;

        try {
            position = ((NotesAdapter) recyclerView.getAdapter()).getMPosition();
        } catch (Exception e) {
            return super.onContextItemSelected(item);
        }

        switch (item.getItemId()) {
            case R.id.delete_note:
                idOfNote = ((NotesAdapter) recyclerView.getAdapter()).getIdOfNote();
                deleteNote(idOfNote);
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.copy_content:
                String content = ((NotesAdapter) recyclerView.getAdapter()).getNoteData();
                ClipboardManager myClickboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData myClip = ClipData.newPlainText("text", content);
                myClickboard.setPrimaryClip(myClip);
                Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onContextItemSelected(item);
    }

    public void deleteNote(int data) {

        DbHelper dbHelper = new DbHelper(this);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = NotesEntry._ID + " = ? ";
        String[] selectionArgs = {String.valueOf(data)};

        db.delete(NotesEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
        displayDatabaseInfo();

    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();

    }


    private void displayDatabaseInfo() {

        DbHelper dbHelper = new DbHelper(this);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                NotesEntry._ID,
                NotesEntry.COLUMN_NAME,
                NotesEntry.CURRENT_TIME
        };

        String sortOrder = NotesEntry._ID + " DESC";

        Cursor cursor = db.query(
                NotesEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        ArrayList<Notes> noteList = new ArrayList<>();

        try {

            int noteColumn = cursor.getColumnIndex(NotesEntry.COLUMN_NAME);
            int timeColumn = cursor.getColumnIndex(NotesEntry.CURRENT_TIME);
            int idColumn = cursor.getColumnIndex(NotesEntry._ID);

            while (cursor.moveToNext()) {
                String currentNote = cursor.getString(noteColumn);
                String currentTime = cursor.getString(timeColumn);
                int currentId = cursor.getInt(idColumn);
                Notes notes = new Notes(currentId, currentNote, currentTime);
                noteList.add(notes);
            }
        } finally {
            cursor.close();
        }

        db.close();

        NotesAdapter adapter = new NotesAdapter(this, noteList);

        if (adapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            emptyLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}
