package com.example.noteapp.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.Toast;

import com.example.noteapp.R;
import com.example.noteapp.activity.EditActivity;
import com.example.noteapp.api.ApiClient;
import com.example.noteapp.api.ApiInterface;
import com.example.noteapp.api.Note;
import com.example.noteapp.noteAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int INTENT_ADD = 100;
    private static final int INTENT_EDIT = 200;

    FloatingActionButton fab;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipe;

    ApiInterface apiInterface;
    List<Note> note;
    noteAdapter adapter;
    noteAdapter.ItemClickListenner itemClickListenner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.add);
        recyclerView = findViewById(R.id.recycler_view);

        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        swipe = findViewById(R.id.swipe_refresh);
        swipe.setOnRefreshListener(
                () -> showNote()
        );

        fab.setOnClickListener(view -> {
            startActivityForResult(new Intent(this, EditActivity.class),INTENT_ADD);
        });

        itemClickListenner = ((view,position) -> {
            int id = note.get(position).getId();
            String title = note.get(position).getTitle();
            String notes = note.get(position).getNote();
            int color = note.get(position).getColor();

            Intent intent = new Intent(this,EditActivity.class);
            intent.putExtra("id",id);
            intent.putExtra("title",title);
            intent.putExtra("note",notes);
            intent.putExtra("color",color);
            startActivityForResult(intent,INTENT_EDIT);


        });

        showNote();

    }

    private void showNote() {

        //당겼을 때 새로고침 생김
        swipe.setRefreshing(true);
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<Note>> call = apiInterface.getNotes();
        call.enqueue(new Callback<List<Note>>() {
            @Override
            public void onResponse(Call<List<Note>> call, Response<List<Note>> response) {
                if(response.isSuccessful() && response.body() != null){

                    onGetResult(response.body());
                    swipe.setRefreshing(false);  //새로고침 사라짐

                }
            }

            @Override
            public void onFailure(Call<List<Note>> call, Throwable t) {
                swipe.setRefreshing(false);
            }
        });
    }

    public void onGetResult(List<Note>notes){
        adapter = new noteAdapter(getApplicationContext(),notes,itemClickListenner);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        note = notes;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == INTENT_ADD && resultCode==RESULT_OK){
            showNote();
        }else if(requestCode == INTENT_EDIT && resultCode==RESULT_OK){
            showNote();
        }
    }
}