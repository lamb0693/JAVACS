package com.example.receiver;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class AudioNetReceiver implements Runnable{
    private static final int SAMPLE_RATE = 22000; //44100;
    public boolean bReceiving = true;
    SourceDataLine sourceDataLine = null;
    private Socket socket;

    public AudioNetReceiver(){
        try {
            IO.Options options = new IO.Options();
            options.forceNew = true; // Create a new connection
            socket = IO.socket("http://localhost:3000", options);
            
            socket.connect();

            socket.on("audio_data", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                        byte[] audioData = (byte[]) args[0];
                        try {
                            // Play received audio data
                            //System.out.println(audioData);
                            //for(byte x : audioData) System.out.print(","+x);
                            sourceDataLine.write(audioData, 0, audioData.length);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
            });

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("audio_net thread started...");
   
        try{
            final AudioFormat audioFormat = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
            final DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat); // for speaker

            if(!AudioSystem.isLineSupported(info) ){    // 앞이 microphone, 뒤가 speaker
                System.err.println("Audio line not supported");
                return ;
            } else {
                System.out.println("Audio line supported");
            }

            sourceDataLine = (SourceDataLine)AudioSystem.getLine(info);  // for microphone

            sourceDataLine.open(audioFormat);
            sourceDataLine.start();

            while(true){

            }
        } catch(Exception e){
            System.err.println(e.getMessage());
        } 
        
    }    
}
