package com.example.chandora.mynotes;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.chandora.mynotes.NotesContract.NotesEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private LinearLayout emptyLayout;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener stateListener;
    private DatabaseReference mFirebaseRef;
    private FirebaseDatabase mFirebaseDatabase;

    ArrayList<Notes> noteList;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        emptyLayout = (LinearLayout) findViewById(R.id.emptyLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        noteList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
//
//        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
//        recyclerView.setLayoutManager(manager);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
//        mFirebaseDatabase.setPersistenceEnabled(true);


        if (mAuth.getCurrentUser() != null) {
            mFirebaseRef = mFirebaseDatabase.getReference().child("Notes").child(mAuth.getCurrentUser().getUid());
        }

        stateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MainActivity.this, SignInActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }
            }
        };

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

//        int position = -1;
        int idOfNote = 0;
        String firebaseId;
//        try {
//            position = ((NotesAdapter) recyclerView.getAdapter()).getMPosition();
//        } catch (Exception e) {
//            return super.onContextItemSelected(item);
//        }

        switch (item.getItemId()) {
            case R.id.delete_note:
                idOfNote = ((NotesAdapter) recyclerView.getAdapter()).getIdOfNote();
                firebaseId = ((NotesAdapter) recyclerView.getAdapter()).getFirebaseId();
                deleteNote(idOfNote, firebaseId);
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

    public void deleteNote(int data, String fId) {

        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        mFirebaseRef.child(fId).removeValue();

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
        mAuth.addAuthStateListener(stateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayDatabaseInfo();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void displayDatabaseInfo() {

        DbHelper dbHelper = new DbHelper(this);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                NotesEntry._ID,
                NotesEntry.COLUMN_NAME,
                NotesEntry.FIREBASE_KEY
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


        try {

            int noteColumn = cursor.getColumnIndex(NotesEntry.COLUMN_NAME);
            int idColumn = cursor.getColumnIndex(NotesEntry._ID);
            int firebaseIdColumn = cursor.getColumnIndex(NotesEntry.FIREBASE_KEY);

            noteList.clear();

            while (cursor.moveToNext()) {
                String currentNote = cursor.getString(noteColumn);
                String firebaseKey = cursor.getString(firebaseIdColumn);
                int currentId = cursor.getInt(idColumn);
                Notes notes = new Notes(currentId, currentNote, firebaseKey);
                noteList.add(notes);

            }
        } finally {
            cursor.close();
        }

        db.close();

        if (mAuth.getCurrentUser() != null) {

            mFirebaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    if (noteList.isEmpty()) {

                        Log.d("TAG", "In add value");
                        for (DataSnapshot firebaseData : dataSnapshot.getChildren()) {
                            Notes notes = firebaseData.getValue(Notes.class);
                            noteList.add(notes);
                            if (notes != null) {
                                String noteData = notes.getData();
                                String fireKey = notes.getFirebaseKey();
                                insertNote(fireKey, noteData);
                                Log.d("Data", noteData);
                            }

                        }
                        Collections.reverse(noteList);
                        displayDatabaseInfo();

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        Log.d("ListL", noteList.toString());
        NotesAdapter adapter = new NotesAdapter(getApplicationContext(), noteList);

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
    private void insertNote(String fireKey, String notesData){

        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NotesEntry.FIREBASE_KEY, fireKey);
        values.put(NotesEntry.COLUMN_NAME, notesData);

        db.insertWithOnConflict(NotesEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);


    }

}
