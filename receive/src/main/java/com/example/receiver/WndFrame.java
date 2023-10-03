package com.example.receiver;

import javax.swing.*;

import io.socket.client.IO;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class WndFrame extends JFrame {
    public Socket socket = null;
    CSRCanvas canvas = null;

    AudioNetStreamer audioStreamer = null;
    AudioNetReceiver audioNetReceiver = null;

    public WndFrame() {
        initSocket();

        setPreferredSize(new Dimension(Cons.WINDOW_WIDTH, Cons.WINDOW_HEIGHT));
        setMinimumSize(new Dimension(Cons.WINDOW_WIDTH, Cons.WINDOW_HEIGHT));
        setMaximumSize(new Dimension(Cons.WINDOW_WIDTH, Cons.WINDOW_HEIGHT));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("CSR");
        initWindow();
        setVisible(true); 

        //this.pack();
    }

    private void initSocket(){
        try {
            IO.Options options = new IO.Options();
            options.forceNew = true; // Create a new connection
            socket = IO.socket(Cons.SOCEKTIO_SERVER, options);
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void initWindow(){
        this.setLayout(new BorderLayout());

        /*
         *그림 그리기용 Canvas
         */ 
        canvas = new CSRCanvas(socket);
        canvas.setPreferredSize(new Dimension(Cons.CANVAS_WIDTH, Cons.WINDOW_HEIGHT-Cons.UPPER_PANEL_HEIGHT));
        canvas.setMinimumSize(new Dimension(Cons.CANVAS_WIDTH, Cons.WINDOW_HEIGHT-Cons.UPPER_PANEL_HEIGHT));
        canvas.setMaximumSize(new Dimension(Cons.CANVAS_WIDTH, Cons.WINDOW_HEIGHT-Cons.UPPER_PANEL_HEIGHT));
        this.add(canvas, BorderLayout.WEST);

        /*
         * 상담 결과 보여주는 List
         */
        CounselList counselList = new CounselList(socket);
        counselList.setPreferredSize(new Dimension(Cons.JLIST_WIDTH, Cons.WINDOW_HEIGHT - Cons.UPPER_PANEL_HEIGHT));
        counselList.setMinimumSize(new Dimension(Cons.JLIST_WIDTH, Cons.WINDOW_HEIGHT - Cons.UPPER_PANEL_HEIGHT));
        counselList.setMaximumSize(new Dimension(Cons.JLIST_WIDTH, Cons.WINDOW_HEIGHT - Cons.UPPER_PANEL_HEIGHT));
        this.add(counselList, BorderLayout.CENTER);

        /*
         * DB조회용 panel, EastPanel
         */
        JPanel panelEast = new JPanel();
        panelEast.setLayout(new BorderLayout());
        panelEast.setPreferredSize(new Dimension(Cons.RIGHT_PANEL_WIDTH, Cons.WINDOW_HEIGHT - Cons.UPPER_PANEL_HEIGHT));
        panelEast.setMinimumSize(new Dimension(Cons.RIGHT_PANEL_WIDTH, Cons.WINDOW_HEIGHT - Cons.UPPER_PANEL_HEIGHT));
        panelEast.setMaximumSize(new Dimension(Cons.RIGHT_PANEL_WIDTH, Cons.WINDOW_HEIGHT - Cons.UPPER_PANEL_HEIGHT));   

        /*
         * login 3개
         */
        // 전화번호
        
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BorderLayout());
        loginPanel.setBackground(Color.YELLOW);
        loginPanel.setPreferredSize(new Dimension(Cons.RIGHT_PANEL_WIDTH, Cons.LOGIN_PANEL_HEIGHT));
        loginPanel.setMinimumSize(new Dimension(Cons.RIGHT_PANEL_WIDTH, Cons.LOGIN_PANEL_HEIGHT));
        loginPanel.setMinimumSize(new Dimension(Cons.RIGHT_PANEL_WIDTH, Cons.LOGIN_PANEL_HEIGHT));
        JPanel namePanel = new JPanel();
        JLabel labelName = new JLabel("전화번호");
        //labelName.setSize(200,70);
        namePanel.add(labelName);
        JTextField txtName = new JTextField(20);
        namePanel.add(txtName);
        // password
        JPanel pwdPanel = new JPanel();
        JLabel labelPwd = new JLabel("비밀번호");
        //labelPwd.setSize(200,70);
        pwdPanel.add(labelPwd);
        JTextField txtPwd = new JTextField(20);
        pwdPanel.add(txtPwd);
        JButton btnLogin = new JButton("Log in");

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Builder builder = new Retrofit.Builder();
                Retrofit retrofit = builder.baseUrl("http://localhost:8080/").addConverterFactory(GsonConverterFactory.create()).build();
                INetworkService iNetworkService = retrofit.create(INetworkService.class);
                Map<String, String> loginParamMap = new HashMap<>();
                loginParamMap.put("tel", "01031795981");
                loginParamMap.put("password", "00000000");
                Call<ResponseToken> apicall = iNetworkService.login(loginParamMap);
                apicall.enqueue(new Callback<ResponseToken>(){
                    @Override
                    public void onFailure(Call<ResponseToken> arg0, Throwable arg1) {
                        System.out.println("api call failure");
                    }
                    @Override
                    public void onResponse(Call<ResponseToken> arg0, Response<ResponseToken> arg1) {
                        System.out.println("api call success");
                        if( arg1.isSuccessful() ) {
                            ResponseToken responseToken = arg1.body();
                            System.out.println(responseToken.accessToken);
                            System.out.println(responseToken.refreshToken);
                        } else {
                            int response  = arg1.code();
                            System.out.println("resoponse not ok :" + response);
                        }
                        
                    }
                });

            }
        });
        
        loginPanel.add(namePanel, BorderLayout.NORTH);
        loginPanel.add(pwdPanel, BorderLayout.CENTER);
        loginPanel.add(btnLogin, BorderLayout.SOUTH);

        panelEast.add(loginPanel, BorderLayout.NORTH);

        /*
        * Send Message Panel
        */ 
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBackground(Color.BLUE);
        chatPanel.setPreferredSize(new Dimension(Cons.RIGHT_PANEL_WIDTH, Cons.CHAT_PANEL_HEIGHT));
        chatPanel.setMinimumSize(new Dimension(Cons.RIGHT_PANEL_WIDTH, Cons.CHAT_PANEL_HEIGHT));
        chatPanel.setMinimumSize(new Dimension(Cons.RIGHT_PANEL_WIDTH, Cons.CHAT_PANEL_HEIGHT));
        
        JPanel labelPanel = new JPanel();
        JLabel labelSend = new JLabel("보낼메시지");
        labelPanel.add(labelSend);
        final JTextField txtSend = new JTextField(30);
        JButton btnSendMessage = new JButton("보내기");
        btnSendMessage.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(txtSend.getText() != null && !txtSend.getText().equals("")) socket.emit("chat_data", txtSend.getText().getBytes());
            }
        });
        
        chatPanel.add(labelPanel, BorderLayout.NORTH);
        chatPanel.add(txtSend, BorderLayout.CENTER);
        chatPanel.add(btnSendMessage, BorderLayout.SOUTH);

        panelEast.add(chatPanel, BorderLayout.CENTER);

        /*
         * 접속자 List
         */
        CustomorList customorList = new CustomorList();
        customorList.setPreferredSize(new Dimension(Cons.RIGHT_PANEL_WIDTH, Cons.CUSTOMER_LIST_HEIGHT));
        customorList.setMaximumSize(new Dimension(Cons.RIGHT_PANEL_WIDTH, Cons.CUSTOMER_LIST_HEIGHT));
        customorList.setMinimumSize(new Dimension(Cons.RIGHT_PANEL_WIDTH, Cons.CUSTOMER_LIST_HEIGHT));
        panelEast.add(customorList, BorderLayout.SOUTH);

        this.add(panelEast, BorderLayout.EAST);

        /*
        * upper Panel 설정
        */ 
        JPanel panelNorth = new JPanel();
        panelNorth.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelNorth.setSize(Cons.WINDOW_WIDTH,Cons.UPPER_PANEL_HEIGHT);
        this.add(panelNorth, BorderLayout.NORTH);

        //this.pack();

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
                    thisButton.setBackground(Color.RED);
                    audioStreamer = new AudioNetStreamer(socket);
                    Thread audioThread = new Thread(audioStreamer);
                    audioThread.start();
                    thisButton.setText("음성 전달 끄기");
                    thisButton.setEnabled(true);
                }else if(audioStreamer!=null && audioStreamer.getBStreaming()==true){
                    thisButton.setEnabled(false);
                    thisButton.setBackground(Color.LIGHT_GRAY);
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

        JButton btnSpeakerOn = new JButton("음성받기 켜기", null);
        panelNorth.add(btnSpeakerOn);
        btnSpeakerOn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton thisButton = (JButton)e.getSource();
                if(audioNetReceiver == null){
                    thisButton.setEnabled(false);
                    thisButton.setBackground(Color.RED);
                    audioNetReceiver = new AudioNetReceiver(socket);
                    Thread audioThread = new Thread(audioNetReceiver);
                    audioThread.start();
                    thisButton.setText("음성받기 끄기");
                    thisButton.setEnabled(true);
                }else if(audioNetReceiver!=null && audioNetReceiver.getBReceiving()==true){
                    thisButton.setEnabled(false);
                    thisButton.setBackground(Color.LIGHT_GRAY);
                    audioNetReceiver.stopReceiving();;
                    thisButton.setText("음성받기 켜기");
                    thisButton.setEnabled(true);
                    audioNetReceiver = null;
                } 
            }
        });

        //this.pack();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(socket == null) socket.close();

                System.exit(0);
            }
        });
 
    }

}
