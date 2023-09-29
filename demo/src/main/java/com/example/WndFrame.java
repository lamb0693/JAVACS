package com.example;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class WndFrame extends JFrame {

    AudioNetStreamer audioStreamer = null;
    CSRCanvas canvas = null;
    
    public WndFrame() {
        setSize(Cons.WINDOW_WIDTH,Cons.WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("CSR");
        init();
        setVisible(true);
        
    }

    private void init(){

        this.setLayout(new BorderLayout());

        /*
         *그림 그리기용 Canvas
         */ 
        canvas = new CSRCanvas();
        this.add(canvas, BorderLayout.WEST);

        /*
         * 상담 결과 보여주는 List
         */
        CounselList counselList = new CounselList();
        this.add(counselList, BorderLayout.CENTER);

        /*
         * DB조회용 panel
         */
        JPanel panelEast = new JPanel();
        panelEast.setLayout(new GridLayout(6,1));   
        panelEast.setSize(Cons.RIGHT_PANEL_WIDTH, Cons.WINDOW_HEIGHT-Cons.UPPER_PANEL_HEIGHT);
        this.add(panelEast, BorderLayout.EAST);

        /*
         * login 3개
         */
        // 전화번호
        
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(3,1));
        loginPanel.setBackground(Color.YELLOW);
        loginPanel.setSize(Cons.RIGHT_PANEL_WIDTH, 200);
        JPanel namePanel = new JPanel();
        JLabel labelName = new JLabel("전화번호");
        labelName.setSize(200,100);
        namePanel.add(labelName);
        JTextField txtName = new JTextField(20);
        namePanel.add(txtName);
        // password
        JPanel pwdPanel = new JPanel();
        JLabel labelPwd = new JLabel("비밀번호");
        labelPwd.setSize(200,100);
        pwdPanel.add(labelPwd);
        JTextField txtPwd = new JTextField(20);
        pwdPanel.add(txtPwd);
        JButton btnLogin = new JButton("Log in");
        
        loginPanel.add(namePanel);
        loginPanel.add(pwdPanel);
        loginPanel.add(btnLogin);

        panelEast.add(loginPanel);


        /*
        * upper Panel 설정
        */ 
        JPanel panelNorth = new JPanel();
        panelNorth.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelNorth.setSize(Cons.WINDOW_WIDTH,Cons.UPPER_PANEL_HEIGHT);
        this.add(panelNorth, BorderLayout.NORTH);

        //******  upper Panel의 button들

        /** 
         * Button Start and stop streaming microphone
         */
        JButton btnStartStreaming = new JButton("음성 전달 켜기", null);
        panelNorth.add(btnStartStreaming);
        btnStartStreaming.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton thisButton = (JButton)e.getSource();
                if(audioStreamer == null){
                    thisButton.setEnabled(false);
                    audioStreamer = new AudioNetStreamer();
                    Thread audioThread = new Thread(audioStreamer);
                    audioThread.start();
                    thisButton.setText("음성 전달 끄기");
                    thisButton.setEnabled(true);
                }else if(audioStreamer!=null && audioStreamer.getBStreaming()==true){
                    thisButton.setEnabled(false);
                    audioStreamer.stopStreaming();
                    thisButton.setText("음성 전달 켜기");
                    thisButton.setEnabled(true);
                    audioStreamer = null;
                } 
            }
        });

        /*
         * 마지막 선 지우기
         */
        JButton btnDellast = new JButton("마지막 선 지우기", null);
        panelNorth.add(btnDellast);
        btnDellast.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.removeLast();
            }
        });

        /*
         * 전체 지우기
         */
        JButton btnClearAll = new JButton("모든 선 지우기", null);
        panelNorth.add(btnClearAll);

        btnClearAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.clearAll();;
            }
        });

        /*
         * 선 파일로 저장하기
         */
        JButton btnSaveImage = new JButton("그림 저장하기", null);
        panelNorth.add(btnSaveImage);
        btnSaveImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.saveCurrentImage();    
            }
        });


        //this.pack();

        /*
         * stop streaming button  -- of no use currently
         */
        // JButton btnStopStreaming = new JButton("Stop Streaming", null);
        // btnStopStreaming.setSize(1000, 100);
        // this.add(btnStopStreaming, BorderLayout.SOUTH);
        // btnStopStreaming.addActionListener(new ActionListener() {
        //     @Override
        //     public void actionPerformed(ActionEvent e) {
        //         // TODO Auto-generated method stub
        //         // audioStreamer.stopStreaming();
        //     }
        // });

    }

}
