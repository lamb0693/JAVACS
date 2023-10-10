package com.example.receiver;

import io.socket.client.Socket;

import javax.sound.sampled.*;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class AudioNetStreamer implements Runnable{
    private static final int SAMPLE_RATE = 22000; // 44100;
    private static final int AUDIO_BUFFER_SIZE = 1024;
    private boolean bStreaming = true;
    TargetDataLine targetDataLine = null;
    private Socket socket;
    private String accessToken = null;

    private AudioInputStream audioInputStream = null;
    private File audioFile = null;
    private final String savedAudioFileName = "savedAudio.wav";
    private Uploader uploader = null;

    private JPanel parent = null;

    // public AudioNetStreamer(Socket socket, String accessToken){
    //     this.socket = socket;
    //     this.accessToken = accessToken;
    //     this.audioFile = new File(savedAudioFileName);
    // }

    public AudioNetStreamer(Socket socket, Uploader uploader, JPanel parent){
        this.socket = socket;
        this.audioFile = new File(savedAudioFileName);
        this.uploader = uploader;
        this.parent = parent;
    }

    public void stopStreaming(){
        bStreaming = false;
    }

    public boolean getBStreaming(){
        return bStreaming;
    }

    private void saveAudioToFile(File file) {
        try {
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);
        } catch (IOException e) {
            System.err.println("Error saving audio to file: " + e.getMessage());
        }
    }

    private void uploadSavedAudioFile(){
        uploader.uploadFile("AUDIO", "audio file", savedAudioFileName);

        //Builder builder = new Retrofit.Builder();
        //***** */ JSON 용
        // GsonBuilder gsonBuilder = new GsonBuilder();
        // Gson gson = gsonBuilder.setLenient().create();
        //Retrofit retrofit = builder.baseUrl("http://localhost:8080/").addConverterFactory(GsonConverterFactory.create(gson)).build();
        /* plain-text용 gradle 추가 필요함 */
        // Retrofit retrofit = builder.baseUrl("http://localhost:8080/").addConverterFactory(ScalarsConverterFactory.create()).build();
        
        // INetworkService iNetworkService = retrofit.create(INetworkService.class);

        // try{
        //     File upFile = new File(savedAudioFileName);

        //     RequestBody requestBodyFile = RequestBody.create(MediaType.parse("multipart/form-data"), upFile);
        //     MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", upFile.getName(), requestBodyFile);

        //     Call<String> apicall = iNetworkService.createBoard("Bearer:"+accessToken,/*/ "multipart/form-data",*/
        //                  "AUDIO", "audio file", filePart);
        //     apicall.enqueue(new Callback<String>(){
        //         @Override
        //         public void onResponse(Call<String> call, Response<String> response) {
        //             System.out.println("uploadSuccess : " + response.body());
        //             // 나중에 dialogue 띄우자
        //         }
        //         @Override
        //         public void onFailure(Call<String> call, Throwable t) {
        //             //JOptionPane.showMessageDialog(loginPanel, "uploadFail : " + t.getMessage());
        //             if (t instanceof HttpException) {
        //                 HttpException httpException = (HttpException) t;
        //                 int responseCode = httpException.code();
        //                 // Now you have the response code
        //                 System.out.println("uploadFail : Response code " + responseCode);
        //             } else {
        //                 // Handle other types of failures (e.g., network issues)
        //                 System.out.println("uploadFail : " + t.getMessage());
        //             }
        //         }
        //     });
        // } catch(Exception e){
        //     System.err.println("savedAudioFile 의 upload 에 실패 했습니다");
        // }
  
    }

    @Override
    public void run() {
        System.out.println("audio streaming thread started...");

        try{
            final AudioFormat audioFormat = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
            final DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat); // for microphone

            if(!AudioSystem.isLineSupported(info) ){    // 앞이 microphone, 뒤가 speaker
                System.err.println("Audio line not supported");
                JOptionPane.showMessageDialog(parent, "음성 입력 장치가 지원되지 않습니다, 마이크를 확인하세요");
                // StartStreamin 버튼 활성화 필요
                return ;
            } else {
                System.out.println("Audio line supported");
            }

            targetDataLine = (TargetDataLine)AudioSystem.getLine(info);  // for microphone

            targetDataLine.open(audioFormat);
            targetDataLine.start();


            byte[] buffer = new byte[AUDIO_BUFFER_SIZE];
            ByteArrayOutputStream byteStreamSaving = new ByteArrayOutputStream();
            while (bStreaming){
                int bytesRead = targetDataLine.read(buffer, 0, buffer.length);
                if(bytesRead>0){
                    //System.out.println(bytesRead);
                    //for(int i=0; i<buffer.length; i++) System.out.print(buffer[i]+",");

                    // Serialize audio data (e.g., convert to bytes)
                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                    byteStream.write(buffer, 0, bytesRead);
                    byteStreamSaving.write(buffer, 0, bytesRead);

                    // Send audio data to the Socket.IO server
                    socket.emit("audio_data", byteStream.toByteArray());
                }
            }
            byte[] audioData = byteStreamSaving.toByteArray();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
            audioInputStream = new AudioInputStream(byteArrayInputStream, audioFormat, audioData.length / audioFormat.getFrameSize());

            saveAudioToFile(audioFile);
            uploadSavedAudioFile();
            /*
            * socket으로 상담 게시판 update되었다고  message 보내야 함
            */ 

        } catch(Exception e){
            System.err.println(e.getMessage());
        } finally {
            // Close the targetDataLine after streaming is finished
            if (targetDataLine != null) {
                targetDataLine.stop();
                targetDataLine.close();
            }
        }
        System.out.println("audio streaming thread  stoppping ...");
    }
}
