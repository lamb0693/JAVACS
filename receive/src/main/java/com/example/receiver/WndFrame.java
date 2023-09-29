package com.example.receiver;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WndFrame extends JFrame {

    //AudioNetStreamer audioStreamer = null;
    AudioNetReceiver audioNetReceiver = null;

    public WndFrame() {
        setSize(1000,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("CSR");
        init();
        setVisible(true);
        
    }

    private void init(){

        this.setLayout(new BorderLayout());

        JButton btnStartStreaming = new JButton("Start Receiving", null);
        btnStartStreaming.setSize(1000, 100);
        this.add(btnStartStreaming, BorderLayout.SOUTH);
        btnStartStreaming.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                System.out.println("clicked");
                audioNetReceiver = new AudioNetReceiver();
                Thread audioThread = new Thread(audioNetReceiver);
                audioThread.start();
            }
        });

        JButton btnStopStreaming = new JButton("Stop Receiving", null);
        btnStopStreaming.setSize(1000, 100);
        this.add(btnStopStreaming, BorderLayout.NORTH);
        btnStopStreaming.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                //audioNetReceiver.stopReceiving();
            }
        });

        final CSRCanvas canvas = new CSRCanvas();
        this.add(canvas, BorderLayout.CENTER);

        JButton btnDellast = new JButton("del last", null);
        btnDellast.setSize(100,600);
        this.add(btnDellast, BorderLayout.EAST);
        btnDellast.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                canvas.removeLast();
                //throw new UnsupportedOperationException("Unimplemented method 'actionPerformed'");
            }
        });

        JButton btnClearAll = new JButton("clear all", null);
        btnClearAll.setSize(100,600);
        this.add(btnClearAll, BorderLayout.WEST);

        btnClearAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                canvas.clearAll();;
                //throw new UnsupportedOperationException("Unimplemented method 'actionPerformed'");
            }
        });
   
    }

}
