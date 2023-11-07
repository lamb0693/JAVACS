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
    private static final int SAMPLE_RATE = 44100; // 44100;
    private static final int AUDIO_BUFFER_SIZE = 1024;
    private boolean bStreaming = true;
    TargetDataLine targetDataLine = null;
    private Socket socket;
    private String accessToken = null;

    private AudioInputStream audioInputStream = null;
    private File audioFile = null;
    private final String savedAudioFileName = "savedAudio.wav";
    private Uploader uploader = null;

    //private JPanel parent = null;
    private WndFrame wndFrame = null;

    // public AudioNetStreamer(Socket socket, String accessToken){
    //     this.socket = socket;
    //     this.accessToken = accessToken;
    //     this.audioFile = new File(savedAudioFileName);
    // }

    public AudioNetStreamer(Socket socket, Uploader uploader, WndFrame wndFrame){
        this.socket = socket;
        this.audioFile = new File(savedAudioFileName);
        this.uploader = uploader;
        this.wndFrame = wndFrame;
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
  
    }

    @Override
    public void run() {
        System.out.println("audio streaming thread started...");

        bStreaming = true;

        try{
            final AudioFormat audioFormat = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
            final DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat); // for microphone

            if(!AudioSystem.isLineSupported(info) ){    // 앞이 microphone, 뒤가 speaker
                System.err.println("Audio line not supported");
                JOptionPane.showMessageDialog(wndFrame, "음성 입력 장치가 지원되지 않습니다, 마이크를 확인하세요");
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
                    socket.emit("audio_data", wndFrame.txtCustomerTel.getText() , byteStream.toByteArray());
                }
            }
            byte[] audioData = byteStreamSaving.toByteArray();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
            audioInputStream = new AudioInputStream(byteArrayInputStream, audioFormat, audioData.length / audioFormat.getFrameSize());

            saveAudioToFile(audioFile);
            uploadSavedAudioFile();

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
