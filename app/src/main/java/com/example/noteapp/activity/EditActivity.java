package com.example.noteapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.noteapp.api.Note;
import com.example.noteapp.R;
import com.example.noteapp.api.ApiClient;
import com.example.noteapp.api.ApiInterface;
import com.thebluealliance.spectrum.SpectrumPalette;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditActivity extends AppCompatActivity {

    private EditText titleEt,noteEt;
    ProgressDialog progressDialog;

    ApiInterface apiInterface;
    SpectrumPalette palette;
    int color;

    int id;
    String title;
    String note;
    Menu actionMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        titleEt = findViewById(R.id.title);
        noteEt = findViewById(R.id.note);
        palette = findViewById(R.id.palette);

        palette.setOnColorSelectedListener(
                clr -> color = clr
        );

        // 아무것도 안 했을 때 색상
        palette.setSelectedColor(getResources().getColor(R.color.white));
        color = getResources().getColor(R.color.white);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("저장중입니다…");

        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);
        title = intent.getStringExtra("title");
        note = intent.getStringExtra("note");
        color = intent.getIntExtra("color",0);

        setDataFromIntent();

    }

    private void setDataFromIntent() {

        if(id != 0){
            titleEt.setText(title);
            noteEt.setText(note);
            palette.setSelectedColor(color);
            readMode();
        }else {
            palette.setSelectedColor(getResources().getColor(R.color.white));
            color = getResources().getColor(R.color.white);
            editMode();
        }
    }

    private void editMode() {
        titleEt.setFocusableInTouchMode(true);
        noteEt.setFocusableInTouchMode(true);
        palette.setEnabled(true);
    }

    private void readMode() {

        //포커스 갖지 않게. ex) editText 를 눌러도 키보드가 작용하지 않는다.
        titleEt.setFocusableInTouchMode(false);
        noteEt.setFocusableInTouchMode(false);
        titleEt.setFocusable(false);
        noteEt.setFocusable(false);
        palette.setEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_editor,menu);
        actionMenu = menu;

        if(id != 0){
            actionMenu.findItem(R.id.edit).setVisible(true);
            actionMenu.findItem(R.id.delete).setVisible(true);
            actionMenu.findItem(R.id.save).setVisible(false);
            actionMenu.findItem(R.id.update).setVisible(false);
        }else{
            actionMenu.findItem(R.id.edit).setVisible(false);
            actionMenu.findItem(R.id.delete).setVisible(false);
            actionMenu.findItem(R.id.save).setVisible(true);
            actionMenu.findItem(R.id.update).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        String title = titleEt.getText().toString().trim();
        String note = noteEt.getText().toString().trim();
        int color = this.color;


        switch (item.getItemId()){
            case R.id.save:
                // 노트 저장!


                if(title.isEmpty() || note.isEmpty()){
                    Toast.makeText(this, "빈 곳을 채워주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    saveNote(title,note,color);
                }

                return true;

            case R.id.edit:

                editMode();
                actionMenu.findItem(R.id.edit).setVisible(false);
                actionMenu.findItem(R.id.delete).setVisible(false);
                actionMenu.findItem(R.id.save).setVisible(false);
                actionMenu.findItem(R.id.update).setVisible(true);

                return true;

            case R.id.update:

                //Update
                if(title.isEmpty() || note.isEmpty()){
                    Toast.makeText(this, "빈 곳을 채워주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    updateNode(id,title,note,color);
                }

                return true;


            case R.id.delete:

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("확인!");
                alertDialog.setMessage("삭제하시겠어요?");
                alertDialog.setPositiveButton("네",((dialog, which) -> {
                    dialog.dismiss();
                    deleteNote(id);
                }));
                alertDialog.setNegativeButton("취소",(((dialog, which) -> dialog.dismiss())));

                alertDialog.show();

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void deleteNote(int id) {

        progressDialog.show();
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<Note> call = apiInterface.deleteNote(id);
        call.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(@NonNull Call<Note> call, @NonNull Response<Note> response) {

                progressDialog.dismiss();

                if(response.isSuccessful() && response.body() != null){

                    Boolean success = response.body().getSuccess();
                    if(success){
                        Toast.makeText(EditActivity.this,
                                response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        finish(); // 메인 액티비티로 돌아감
                    }else{
                        Toast.makeText(EditActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(EditActivity.this, "실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Note> call,@NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(EditActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveNote(final String title, final String note, final int color) {

        progressDialog.show();

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<Note> call = apiInterface.saveNote(title,note,color);
        call.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(@NonNull Call<Note> call, @NonNull Response<Note> response) {
                progressDialog.dismiss();

                if(response.isSuccessful() && response.body() != null){

                    Boolean success = response.body().getSuccess();
                    if(success){
                        Toast.makeText(EditActivity.this,
                                response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        finish(); // 메인 액티비티로 돌아감
                    }else{
                        Toast.makeText(EditActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(EditActivity.this, "실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Note> call,@NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(EditActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateNode(int id, String title, String note, int color){
        progressDialog.show();

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<Note> call = apiInterface.updateNote(id,title,note,color);
        call.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(@NonNull Call<Note> call, @NonNull Response<Note> response) {
                progressDialog.dismiss();

                if(response.isSuccessful() && response.body() != null){

                    Boolean success = response.body().getSuccess();
                    if(success){
                        Toast.makeText(EditActivity.this,
                                response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        finish(); // 메인 액티비티로 돌아감
                    }else{
                        Toast.makeText(EditActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(EditActivity.this, "실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Note> call,@NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(EditActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}