package com.example.receiver;


import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class AudioPlayer {
    String filePath = null;

    AudioPlayer(String filename){
        this.filePath = "./" + filename;
    }
    
    public void playAudio(){
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
            final Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            
            // Add a listener to handle the playback completion
            clip.addLineListener(new LineListener() {
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                }
            });
            
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
