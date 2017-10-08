package com.example.chandora.mynotes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by kpchandora on 7/10/17.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyHolder> {

    private Context context;
    private ArrayList<Notes> notesList;
    private int position;
    private int idOfNote;
    private String stringNote;
    private int rowIndex = -1;

    public NotesAdapter(Context context, ArrayList<Notes> notes) {
        this.context = context;
        notesList = notes;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View mView = inflater.inflate(R.layout.list_layout, parent, false);

        return new MyHolder(mView, notesList, context);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {

        Notes notes = notesList.get(position);
        String singleNote = notes.getData();
        holder.notesTextView.setText(singleNote);


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setPosition(holder.getAdapterPosition());
                idOfNote = notesList.get(getMPosition()).getId();
                stringNote = notesList.get(getMPosition()).getData();

                return false;
            }
        });


    }

    @Override
    public void onViewRecycled(MyHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    public String getNoteData(){
        return stringNote;
    }

    public int getIdOfNote() {
        return idOfNote;
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public int getMPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnContextClickListener {

        TextView notesTextView;
        Context ctx;
        ArrayList<Notes> notes;
        View itemView;
        CardView cardView;

        @RequiresApi(api = Build.VERSION_CODES.M)
        public MyHolder(View itemView, ArrayList<Notes> notes, Context ctx) {
            super(itemView);
            this.itemView = itemView;
            this.ctx = ctx;
            this.notes = notes;
            cardView = (CardView)itemView.findViewById(R.id.cardView);
            notesTextView = (TextView) itemView.findViewById(R.id.listText);
            itemView.setOnClickListener(this);
            itemView.setOnContextClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Notes note = notes.get(position);
            int noteId = note.getId();
            String noteData = note.getData();
            String time = note.getTime();

            Intent i = new Intent(ctx, Main2Activity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("ID", noteId);
            i.putExtra("NOTE", noteData);
            i.putExtra("TIME", time);
            i.putExtra("TAG", 0);
            ctx.startActivity(i);

        }


        @Override
        public boolean onContextClick(View view) {
            return true;
        }
    }
}
