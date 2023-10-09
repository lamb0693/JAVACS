package com.example.receiver;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
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
        @Part("customerTel") String customerTel,
        @Part("content") String content,
        @Part("message") String message,
        @Part MultipartBody.Part file
    );

    @FormUrlEncoded
    @POST("/api/board/list")
    public Call<List<ResponseBoardList>> listBoard(
        @Header("Authorization") String authToken,
        @Field("tel") String tel,
        @Field("noOfDisplay") int noOfDisplay
    );
    
}
