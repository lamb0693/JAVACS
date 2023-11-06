package com.example.receiver;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class AudioNetReceiver implements Runnable{
    private static final int SAMPLE_RATE = 44100; //44100;
    public volatile boolean bReceiving = true;
    SourceDataLine sourceDataLine = null;

    public AudioNetReceiver(Socket socket){

        try {
            socket.on("audio_data", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                        byte[] audioData = (byte[]) args[0];
                        System.out.println(audioData);
                        try {
                            if(sourceDataLine!=null) sourceDataLine.write(audioData, 0, audioData.length);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopReceiving(){
        bReceiving=false;
    }

    public boolean getBReceiving(){
        return bReceiving;
    }

    @Override
    public void run() {
        System.out.println("audio_net thread started...");
        bReceiving = true;
   
        try{
            final AudioFormat audioFormat = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
            final DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat); // for speaker

            if(!AudioSystem.isLineSupported(info) ){  //speaker
                System.err.println("Audio line not supported");
                return ;
            } else {
                System.out.println("Audio line supported");
            }

            sourceDataLine = (SourceDataLine)AudioSystem.getLine(info);  // for speaker

            sourceDataLine.open(audioFormat);
            sourceDataLine.start();

            while(bReceiving){

            }

            sourceDataLine.close();
            System.out.println("audio receiving thread terminating.....");
        } catch(Exception e){
            System.err.println(e.getMessage());
        } 
        
    }    
}
