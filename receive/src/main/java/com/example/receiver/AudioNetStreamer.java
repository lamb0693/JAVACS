package com.example.receiver;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;

public class AudioNetStreamer implements Runnable{
    private static final int SAMPLE_RATE = 44100;
    private static final int AUDIO_BUFFER_SIZE = 1024;
    public boolean bStreaming = true;
    TargetDataLine targetDataLine = null;
    private Socket socket;

    public AudioNetStreamer(){
        try {
            IO.Options options = new IO.Options();
            options.forceNew = true; // Create a new connection
            socket = IO.socket("http://localhost:3000", options);
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void stopStreaming(){
        bStreaming = false;
        socket.disconnect();
    }

    @Override
    public void run() {
        System.out.println("audio_net thread started...");
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'run'");
        try{
            final AudioFormat audioFormat = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
            final DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat); // for microphone

            if(!AudioSystem.isLineSupported(info) ){    // 앞이 microphone, 뒤가 speaker
                System.err.println("Audio line not supported");
                return ;
            }

            targetDataLine = (TargetDataLine)AudioSystem.getLine(info);  // for microphone

            targetDataLine.open(audioFormat);
            targetDataLine.start();

            byte[] buffer = new byte[AUDIO_BUFFER_SIZE];
            while (bStreaming){
                int bytesRead = targetDataLine.read(buffer, 0, buffer.length);
                if(bytesRead>0){
                    System.out.println(bytesRead);
                    //for(int i=0; i<buffer.length; i++) System.out.print(buffer[i]+",");

                    // Serialize audio data (e.g., convert to bytes)
                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                    byteStream.write(buffer, 0, bytesRead);

                    // Send audio data to the Socket.IO server
                    socket.emit("audio_data", byteStream.toByteArray());
                }

            }
        } catch(Exception e){
            System.err.println(e.getMessage());
        } finally {
            // Close the targetDataLine after streaming is finished
            if (targetDataLine != null) {
                targetDataLine.stop();
                targetDataLine.close();
            }
        }
    }
}
