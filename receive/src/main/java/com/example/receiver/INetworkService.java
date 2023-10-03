package com.example.receiver;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface INetworkService {

    @POST("/auth/login")
    public Call<ResponseToken> login(
        @Body Map<String, String> param
    ); 
    
}
