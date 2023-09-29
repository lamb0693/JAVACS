package com.example;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class AudioStreamer implements Runnable{
    private static final int SAMPLE_RATE = 44100;
    private static final int AUDIO_BUFFER_SIZE = 1024;
    public boolean bStreaming = true;
    TargetDataLine targetDataLine = null;
    SourceDataLine sourceDataLine = null;

    public AudioStreamer(){
 //       init();
    }

    private void init(){
   
    }

    public void stopStreaming(){
        targetDataLine.close();
        sourceDataLine.close();
        bStreaming = false;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'run'");
        try{
            System.out.println("audio thread runs ....");
            final AudioFormat audioFormat = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
            final DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat); // for microphone
            final DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, audioFormat); // for speaker

            if(!AudioSystem.isLineSupported(info) || !AudioSystem.isLineSupported(sourceInfo)){    // 앞이 microphone, 뒤가 speaker
                System.err.println("Audio line not supported");
                return ;
            }

            targetDataLine = (TargetDataLine)AudioSystem.getLine(info);  // for microphone
            sourceDataLine = (SourceDataLine)AudioSystem.getLine(sourceInfo); // for speaker

            targetDataLine.open(audioFormat);
            sourceDataLine.open(audioFormat);
            targetDataLine.start();
            sourceDataLine.start();

            byte[] buffer = new byte[AUDIO_BUFFER_SIZE];
            while (bStreaming){
                int bytesRead = targetDataLine.read(buffer, 0, buffer.length);
                if(bytesRead>0){
                    sourceDataLine.write(buffer, 0, bytesRead); // Write to the speaker
                }
                //System.out.println(bytesRead);
                //for(int i=0; i<buffer.length; i++) System.out.print(buffer[i]+",");
            }
        } catch(Exception e){
            System.err.println(e.getMessage());
        }
    }
    
}
