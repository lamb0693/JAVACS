package com.example.receiver;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface INetworkService {

    @POST("/auth/login")
    public Call<ResponseToken> login(
        @Body Map<String, String> param
    ); 

    @Multipart
    @POST("/api/board/create")
    public Call<String> createBoard(
        @Header("Authorization") String authToken,
        //@Header("Content-type") String contentType,
        @Part("content") String content,
        @Part("message") String message,
        @Part MultipartBody.Part file
    );
    
}
