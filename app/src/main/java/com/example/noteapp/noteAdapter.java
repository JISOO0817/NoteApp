package com.example.noteapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.api.Note;

import org.w3c.dom.Text;

import java.util.List;

public class noteAdapter extends RecyclerView.Adapter<noteAdapter.MyViewHolder>{

    private Context context;
    private List<Note> notes;
    private ItemClickListenner itemClickListenner;

    public noteAdapter(Context context, List<Note> notes, ItemClickListenner itemClickListenner) {
        this.context = context;
        this.notes = notes;
        this.itemClickListenner = itemClickListenner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_item,parent,false);
        return new MyViewHolder(view,itemClickListenner);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Note note = notes.get(position);

        holder.titleTv.setText(note.getTitle());
        holder.noteTv.setText(note.getNote());
        holder.dateTv.setText(note.getDate());
        holder.cardView.setCardBackgroundColor(note.getColor());
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        TextView titleTv,noteTv,dateTv;
        ItemClickListenner itemClickListenner;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView, ItemClickListenner itemClickListenner) {
            super(itemView);

            titleTv = itemView.findViewById(R.id.titleTv);
            noteTv = itemView.findViewById(R.id.noteTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            cardView = itemView.findViewById(R.id.cardView);

            this.itemClickListenner = itemClickListenner;
            cardView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            itemClickListenner.onItemClick(v,getAdapterPosition());
        }
    }
    public interface ItemClickListenner{
        void onItemClick(View view, int position);
    }
}
