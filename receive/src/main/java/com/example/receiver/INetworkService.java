package com.example.receiver;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;

public interface INetworkService {

    @POST("/auth/login")
    public Call<ResponseToken> login(
        @Body Map<String, String> param
    ); 

    @FormUrlEncoded
    @POST("/api/board/create")
    public Call<String> createBoard(
        @Header("Authorization") String authToken,
        // @Body Map<String, Object> param
        @Field("content") String content,
        @Field("message") String message,
        @Field("file") Multipart file
    );
    
}
