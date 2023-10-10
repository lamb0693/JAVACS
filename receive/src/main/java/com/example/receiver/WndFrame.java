package com.example.receiver;

import javax.swing.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class WndFrame extends JFrame {
    public Socket socket = null;
    CSRCanvas canvas = null;
    CounselList counselList = null;

    AudioNetStreamer audioStreamer = null;
    AudioNetReceiver audioNetReceiver = null;

    private Uploader uploader = null;

    // login후 setting 되는 변수
    private String accessToken = null;
    private String refreshToken = null;

    // customor select 후 설정 CustomrList에서 설정
    //private String customerTel = null;
    
    JLabel txtCustomerTel = null;
    JLabel txtLoginState = null;

    JButton btnStartStreaming = null;

    public WndFrame() {
        initSocket();
        uploader = new Uploader(this);

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
            //options.path="/socket.io";
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
        counselList = new CounselList(socket, this);
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
        
        final JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BorderLayout());
        loginPanel.setBackground(Color.YELLOW);
        loginPanel.setPreferredSize(new Dimension(Cons.RIGHT_PANEL_WIDTH, Cons.LOGIN_PANEL_HEIGHT));
        loginPanel.setMinimumSize(new Dimension(Cons.RIGHT_PANEL_WIDTH, Cons.LOGIN_PANEL_HEIGHT));
        loginPanel.setMinimumSize(new Dimension(Cons.RIGHT_PANEL_WIDTH, Cons.LOGIN_PANEL_HEIGHT));
        JPanel namePanel = new JPanel();
        JLabel labelName = new JLabel("전화번호");
        //labelName.setSize(200,70);
        namePanel.add(labelName);
        final JTextField txtName = new JTextField(20);
        namePanel.add(txtName);
        // password
        JPanel pwdPanel = new JPanel();
        JLabel labelPwd = new JLabel("비밀번호");
        //labelPwd.setSize(200,70);
        pwdPanel.add(labelPwd);
        final JPasswordField txtPwd = new JPasswordField(20);
        pwdPanel.add(txtPwd);
        final JButton btnLogin = new JButton("Log in");
        final JButton btnLogout = new JButton("Log out");

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnLogin.setEnabled(false); // 바로 disable 시켜야 기다리는 시간에  못 누름
                Builder builder = new Retrofit.Builder();
                Retrofit retrofit = builder.baseUrl("http://localhost:8080/").addConverterFactory(GsonConverterFactory.create()).build();
                INetworkService iNetworkService = retrofit.create(INetworkService.class);
                Map<String, String> loginParamMap = new HashMap<>();
                loginParamMap.put("tel", txtName.getText());
                loginParamMap.put("password", txtPwd.getText());
                Call<ResponseToken> apicall = iNetworkService.login(loginParamMap);
                apicall.enqueue(new Callback<ResponseToken>(){
                    @Override
                    public void onFailure(Call<ResponseToken> arg0, Throwable arg1) {
                        System.out.println("api call failure");
                        btnLogin.setEnabled(true);
                    }
                    @Override
                    public void onResponse(Call<ResponseToken> arg0, Response<ResponseToken> arg1) {
                        System.out.println("api call success");
                        if( arg1.isSuccessful() ) {
                            ResponseToken responseToken = arg1.body();
                            accessToken = responseToken.accessToken;
                            refreshToken = responseToken.refreshToken;
                            //*** */ uploader의 accessToken을 set
                            uploader.setAccessToken(accessToken);
                            //*** */
                            JOptionPane.showMessageDialog(loginPanel, "Login success : " + accessToken);
                            txtName.setText("");
                            txtPwd.setText("");
                            btnLogout.setEnabled(true);
                            // 나중에 바꾸자
                            txtLoginState.setText("CSR0001");
                        } else {
                            int response  = arg1.code();
                            JOptionPane.showMessageDialog(loginPanel, "resoponse not ok :" + response);
                            btnLogin.setEnabled(true);
                            txtLoginState.setText("Logout state");
                        }
                    }
                });

            }
        });

        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnLogout.setEnabled(false);
                accessToken = null;
                refreshToken = null;
                btnLogin.setEnabled(true);
            }
        });

        JPanel loginOutPanel = new JPanel();
        loginOutPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        loginOutPanel.add(btnLogin);
        loginOutPanel.add(btnLogout);
        
        loginPanel.add(namePanel, BorderLayout.NORTH);
        loginPanel.add(pwdPanel, BorderLayout.CENTER);
        loginPanel.add(loginOutPanel, BorderLayout.SOUTH);

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
                if(txtSend.getText() != null && !txtSend.getText().equals("")){
                    uploader.uploadFile("TEXT", txtSend.getText(), null);
                    txtSend.setText("");
                    // message만 보내고 server로 upload 후 다시 상담 내역 download
                    socket.emit("chat_data");
                } 
            }
        });
        
        chatPanel.add(labelPanel, BorderLayout.NORTH);
        chatPanel.add(txtSend, BorderLayout.CENTER);
        chatPanel.add(btnSendMessage, BorderLayout.SOUTH);

        panelEast.add(chatPanel, BorderLayout.CENTER);

        /*
         * 접속자 List
         */
        CustomorList customorList = new CustomorList(this, socket);
        customorList.setPreferredSize(new Dimension(Cons.RIGHT_PANEL_WIDTH, Cons.CUSTOMER_LIST_HEIGHT));
        customorList.setMaximumSize(new Dimension(Cons.RIGHT_PANEL_WIDTH, Cons.CUSTOMER_LIST_HEIGHT));
        customorList.setMinimumSize(new Dimension(Cons.RIGHT_PANEL_WIDTH, Cons.CUSTOMER_LIST_HEIGHT));
        panelEast.add(customorList, BorderLayout.SOUTH);

        this.add(panelEast, BorderLayout.EAST);

        /*
        * upper Panel 설정
        */ 
        final JPanel panelNorth = new JPanel();
        panelNorth.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelNorth.setSize(Cons.WINDOW_WIDTH,Cons.UPPER_PANEL_HEIGHT);
        this.add(panelNorth, BorderLayout.NORTH);

        //this.pack();

        //******  upper Panel의 button들
        JLabel lbLoginState = new JLabel("상담원:");
        panelNorth.add(lbLoginState);
        txtLoginState = new JLabel("로그인 하세요");
        panelNorth.add(txtLoginState);
        JLabel lbCustomerTel = new JLabel("고객 전화번호:");
        panelNorth.add(lbCustomerTel);
        txtCustomerTel = new JLabel("선택하세요");
        panelNorth.add(txtCustomerTel);

        /** 
         * Button Start and stop streaming microphone
         */
        btnStartStreaming = new JButton("음성 전달 켜기", null);
        panelNorth.add(btnStartStreaming);
        btnStartStreaming.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(accessToken==null || getCustomorTel().equals("선택하세요")){
                    JOptionPane.showMessageDialog(panelNorth, "login 하고 고객을 선택하세요");  
                    return;  
                }   
                JButton thisButton = (JButton)e.getSource();
                if(audioStreamer == null){
                    thisButton.setEnabled(false);
                    thisButton.setBackground(Color.RED);
                    audioStreamer = new AudioNetStreamer(socket, uploader, panelNorth);
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

        final JButton btnUpload = new JButton("upload");
        panelNorth.add(btnUpload);
        btnUpload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnUpload.setEnabled(false); // 바로 disable 시켜야 기다리는 시간에  못 누름
                Builder builder = new Retrofit.Builder();
                //***** */ JSON 용
                // GsonBuilder gsonBuilder = new GsonBuilder();
                // Gson gson = gsonBuilder.setLenient().create();
                //Retrofit retrofit = builder.baseUrl("http://localhost:8080/").addConverterFactory(GsonConverterFactory.create(gson)).build();
                /* plain-text용 gradle 추가 필요함 */
                Retrofit retrofit = builder.baseUrl("http://localhost:8080/").addConverterFactory(ScalarsConverterFactory.create()).build();
                
                INetworkService iNetworkService = retrofit.create(INetworkService.class);
 
                File upFile = new File("e:\\sql.txt");
                RequestBody requestBodyFile = RequestBody.create(MediaType.parse("multipart/form-data"), upFile);
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", upFile.getName(), requestBodyFile);

                Call<String> apicall = iNetworkService.createBoard("Bearer:"+accessToken,/*/ "multipart/form-data",*/"01031795981", "TEXT", "Hello Java", filePart);
                apicall.enqueue(new Callback<String>(){
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        JOptionPane.showMessageDialog(loginPanel, "uploadSuccess : " + response.body());
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        //JOptionPane.showMessageDialog(loginPanel, "uploadFail : " + t.getMessage());
                        if (t instanceof HttpException) {
                            HttpException httpException = (HttpException) t;
                            int responseCode = httpException.code();
                            // Now you have the response code
                            JOptionPane.showMessageDialog(loginPanel, "uploadFail : Response code " + responseCode);
                        } else {
                            // Handle other types of failures (e.g., network issues)
                            JOptionPane.showMessageDialog(loginPanel, "uploadFail : " + t.getMessage());
                        }
                    }
                });
                btnUpload.setEnabled(true);
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

    public String getAccessToken(){
        return this.accessToken;
    }

    public CounselList getCounselList(){
        return this.counselList;
    }

    public Uploader getUploader(){
        return this.uploader;
    }

    public String getCustomorTel(){
        return this.txtCustomerTel.getText();
    }

    public void setCustomorTel(String tel){
        txtCustomerTel.setText(tel);
    }

    public void setLoginState(String tel){
        txtLoginState.setText(tel);
    }

    public JButton getButtonStartStreaming(){
        return this.btnStartStreaming;
    }
}
