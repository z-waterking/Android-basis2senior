package com.camp.bit.todolist.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.camp.bit.todolist.NoteOperator;
import com.camp.bit.todolist.R;
import com.camp.bit.todolist.beans.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2019/1/23.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public class NoteListAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private final NoteOperator operator;
    private final List<Note> notes = new ArrayList<>();

    public NoteListAdapter(NoteOperator operator) {
        this.operator = operator;
    }

    public void refresh(List<Note> newNotes) {
        notes.clear();
        if (newNotes != null) {
            notes.addAll(newNotes);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int pos) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(itemView, operator);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int pos) {
        holder.bind(notes.get(pos));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
}
