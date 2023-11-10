package com.example.receiver;

import java.io.File;

import javax.swing.JOptionPane;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Uploader {
    Builder builder = null;
    Retrofit retrofit = null;
    INetworkService iNetworkService = null;
    private String accessToken = null;
    WndFrame wndFrame = null;
    public volatile boolean resultUpload = false;

    Uploader(WndFrame wndFrame){
        builder = new Retrofit.Builder();
        retrofit = builder.baseUrl(Cons.API_SERVER).addConverterFactory(ScalarsConverterFactory.create()).build();
        iNetworkService = retrofit.create(INetworkService.class);
        this.wndFrame = wndFrame;
        //***** */ JSON 용
        // GsonBuilder gsonBuilder = new GsonBuilder();
        // Gson gson = gsonBuilder.setLenient().create();
        //Retrofit retrofit = builder.baseUrl("http://localhost:8080/").addConverterFactory(GsonConverterFactory.create(gson)).build();
        /* plain-text용 gradle 추가 필요함 */
    }

    public void setAccessToken(String accessToken){
        this.accessToken = accessToken;
    }

    public boolean uploadFile(String strContentType, String strMessage, String strFileName){
        if(accessToken ==null || wndFrame.getCustomorTel().equals("선택하세요")){
            JOptionPane.showMessageDialog(wndFrame, "로그인을 하지 않았거나 고객이 선택되어 있지 않습니다");
            return false;
        }
        try{
            MultipartBody.Part filePart = null;
            if(strFileName != null){
                File upFile = new File(strFileName);
                RequestBody requestBodyFile = RequestBody.create(MediaType.parse("multipart/form-data"), upFile);
                filePart = MultipartBody.Part.createFormData("file", upFile.getName(), requestBodyFile);    
            }

            Call<String> apicall = iNetworkService.createBoard("Bearer:"+accessToken,/*/ "multipart/form-data",*/
                         wndFrame.getCustomorTel(), strContentType, strMessage, filePart);
            apicall.enqueue(new Callback<String>(){
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    System.out.println("uploadSuccess : " + response.body());
                    if( response.isSuccessful() ) {
                        JOptionPane.showMessageDialog(wndFrame, "upload success: " + response.body());
                        resultUpload = true;
                        // board update및 server로 보낸다==>room으로 보내는 것으로 고쳐야 update board가 되 돌아와서 update
                        // wndFrame.getCounselList().readFromBoard(wndFrame.getCustomorTel());
                        wndFrame.sendUpdateBoardMessageToChatServer();
                    } else {
                        JOptionPane.showMessageDialog(wndFrame, "resoponse not ok :" + response.code());
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    //JOptionPane.showMessageDialog(loginPanel, "uploadFail : " + t.getMessage());
                    if (t instanceof HttpException) {
                        HttpException httpException = (HttpException) t;
                        int responseCode = httpException.code();
                        // Now you have the response code
                        System.out.println("uploadFail : Response code " + responseCode);
                        JOptionPane.showMessageDialog(wndFrame, "Upload Fail : Response code" + responseCode);
                        resultUpload = false;
                    } else {
                        // Handle other types of failures (e.g., network issues)
                        System.out.println("uploadFail : " + t.getMessage());
                        JOptionPane.showMessageDialog(wndFrame, t.getMessage());
                        resultUpload = false;
                    }
                }
            });
        } catch(Exception e){
            System.err.println(strFileName + " : upload 에 실패 했습니다");
            JOptionPane.showMessageDialog(wndFrame, strFileName + " : upload 에 실패 했습니다");
            resultUpload = false;
        }
  
        if(resultUpload == true){
            wndFrame.counselList.readFromBoard(strContentType);
        }

        return resultUpload;
    }
    
}
