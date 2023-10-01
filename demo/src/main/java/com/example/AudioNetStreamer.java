package com.example;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import javax.sound.sampled.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class AudioNetStreamer implements Runnable{
    private static final int SAMPLE_RATE = 22000; // 44100;
    private static final int AUDIO_BUFFER_SIZE = 1024;
    private volatile boolean bStreaming = true;
    TargetDataLine targetDataLine = null;
    private Socket socket;

    private AudioInputStream audioInputStream = null;
    private File audioFile = null;

    public AudioNetStreamer(Socket socket){
        this.socket = socket;
        this.audioFile = new File("savedAudio.wav");
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

    @Override
    public void run() {
        System.out.println("audio_net thread started...");

        try{
            final AudioFormat audioFormat = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
            final DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat); // for microphone

            if(!AudioSystem.isLineSupported(info) ){    // 앞이 microphone, 뒤가 speaker
                System.err.println("Audio line not supported");
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
