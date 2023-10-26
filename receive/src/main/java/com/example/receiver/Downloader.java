package com.example.receiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Downloader {
    Builder builder = null;
    Retrofit retrofit = null;
    INetworkService iNetworkService = null;
    private String accessToken = null;
    WndFrame wndFrame = null;
    public volatile boolean resultDownload = false;
    private String strSaveFilePath = null;
    private String contentType = null;
    DownloadCallback downloadCallback;

    Downloader(WndFrame wndFrame, DownloadCallback downloadCallback){
        Gson gson = new GsonBuilder().setLenient().create();

        builder = new Retrofit.Builder();
        retrofit = builder.baseUrl("http://localhost:8080/")
            .addConverterFactory(ScalarsConverterFactory.create()) // For raw responses
            .addConverterFactory(GsonConverterFactory.create(gson)) // For JSON conversion, if needed
            .build();
        iNetworkService = retrofit.create(INetworkService.class);
        this.wndFrame = wndFrame;

        this.downloadCallback = downloadCallback;
    }

    public void setAccessToken(String accessToken){
        this.accessToken = accessToken;
    }

    public void setSaveFilePath(String fileName){
        strSaveFilePath = "./" + fileName;
    }

    public void setContentType(String type){
        contentType = type;
    }

    public boolean downloadFile(Long id){

        if(strSaveFilePath == null){
            JOptionPane.showMessageDialog(wndFrame, "저장할 파일 이름을 지정하지 않았어요");
            return false;    
        }

        if(this.accessToken ==null){
            JOptionPane.showMessageDialog(wndFrame, "로그인을 하지 않았습니다");
            return false;
        }

        try{
            Call<ResponseBody> apicall = iNetworkService.download("Bearer:"+accessToken, id);
            apicall.enqueue(new Callback<ResponseBody>(){
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println("downloadSuccess : " + response.body());
                    if( response.isSuccessful() ) {
                        //JOptionPane.showMessageDialog(wndFrame, "download success: " + response.body());
                         try {
                            ResponseBody responseBody = response.body();
                            // Specify the file path where you want to save the downloaded file
                            File file = new File(strSaveFilePath);

                            InputStream inputStream = responseBody.byteStream();
                            OutputStream outputStream = new FileOutputStream(file);

                            byte[] buffer = new byte[4096];
                            int bytesRead;

                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }

                            outputStream.flush();
                            outputStream.close();
                            inputStream.close();

                            // 성공하고 난 뒤의 callback 실행
                            downloadCallback.onDownloadComplete(true, contentType);
                        } catch (IOException e) {
                            e.printStackTrace();
                            resultDownload = false;
                        }
                        //나중에 download 한 것으로 Borad Image 를 dialog로 올린다 혹은 audio로 연결
                        resultDownload = true;
                    } else {
                        JOptionPane.showMessageDialog(wndFrame, "resoponse not ok :" + response.code());
                        resultDownload = false;
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (t instanceof HttpException) {
                        HttpException httpException = (HttpException) t;
                        int responseCode = httpException.code();
                        System.out.println("uploadFail : Response code " + responseCode);
                        JOptionPane.showMessageDialog(wndFrame, "Upload Fail : Response code" + responseCode);
                        resultDownload = false;
                    } else {
                        System.out.println("uploadFail : " + t.getMessage());
                        JOptionPane.showMessageDialog(wndFrame, t.getMessage());
                        resultDownload = false;
                    }
                }
            });
        } catch(Exception e){
            System.err.println(e.getMessage());
            return false;
        }

        return resultDownload;
    }    
}
