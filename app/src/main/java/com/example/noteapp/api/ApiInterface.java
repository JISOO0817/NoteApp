package com.example.noteapp.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded // key=value&key=value 와 같은 형태로 데이터를 전달하는 것 ,@Field 와 같이 쓴다.
    @POST("save.php")
    Call<Note> saveNote(
      @Field("title") String title,
      @Field("note") String note,
      @Field("color") int color
    );

    @GET("notes.php")
    Call<List<Note>> getNotes();

    @FormUrlEncoded
    @POST("updateNote.php")
    Call<Note> updateNote(
            @Field("id") int id,
            @Field("title") String title,
            @Field("note") String note,
            @Field("color") int color
    );
}
