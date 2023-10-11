package com.example.receiver;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;

public class CounselList  extends JList<Object> implements MouseListener {
    private DefaultListModel<Object> model = null;
    Socket socket = null;
    WndFrame wndFrame = null;
    List<ResponseBoardList> list = new ArrayList<>();

    public CounselList(Socket socket, WndFrame wndFrame){
        this.socket = socket;
        this.wndFrame = wndFrame;

        // update 통합
        try {
            socket.on("board_updated", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try{

                    } catch (Exception e){
                        System.out.println("Casting error in CSRCanvas");
                    }
                    repaint();;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        // try {
        //     socket.on("chat_data", new Emitter.Listener() {
        //         @Override
        //         public void call(Object... args) {
        //             // 자바 프로그램에서 온 data는 이렇게 해야 되는데 node에서 온것은 이렇게 하면 error
        //             //byte[] receivedData = (byte[]) args[0];
        //             //ByteArrayInputStream bais = new ByteArrayInputStream(receivedData);
        //             try{
        //                 //ObjectInputStream ois = new ObjectInputStream(bais);
        //             //    String strReceived = new String(receivedData, "EUC_KR");
        //                 // node에서 온것은 바로 이렇게
        //                 String strReceived = (String) args[0];
        //                 addString("client", strReceived);
        //             } catch (Exception e){
        //                 System.out.println("Casting error in CSRCanvas");
        //             }
        //             repaint();;
        //         }
        //     });

        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
        
        this.setBackground(Color.LIGHT_GRAY);

        model = new DefaultListModel<Object>();
        this.setModel(model);

        this.setCellRenderer(new CustomCellRenderer());
        this.addMouseListener(this);

        model.addElement("01031795981");
        model.addElement("01068011307");
    }

    // 스프링 백엔드에서 데이터를 읽어옴
    public void readFromBoard(String tel){
        Builder builder = new Retrofit.Builder();
        Retrofit retrofit = builder.baseUrl("http://localhost:8080/").addConverterFactory(GsonConverterFactory.create()).build();
        INetworkService iNetworkService = retrofit.create(INetworkService.class);
        Call<List<ResponseBoardList>> apicall = iNetworkService.listBoard("Bearer:"+wndFrame.getAccessToken(), tel, 10);
        apicall.enqueue(new Callback<List<ResponseBoardList>>(){
            @Override
            public void onFailure(Call<List<ResponseBoardList>> arg0, Throwable arg1) {
                System.out.println("api call failure");
  
            }
            @Override
            public void onResponse(Call<List<ResponseBoardList>> arg0, Response<List<ResponseBoardList>> arg1) {
                System.out.println("api call success");
                if( arg1.isSuccessful() ) {
                    list = arg1.body();
                    clearList();
                    for(ResponseBoardList board : list){
                        //System.out.println(board.getName());
                        // data 저장
                        addString( board.getName() + " >> ", board.getMessage() + "   첨부 메시지 : >> " + board.getContent() + "   " + board.getStrUpdatedAt());
                    }
                    //System.out.println(list);
                    JOptionPane.showMessageDialog(wndFrame, "list downloaded");
                } else {
                    int response  = arg1.code();
                    JOptionPane.showMessageDialog(wndFrame, "resoponse not ok :" + response);
                }
            }
        });
    }

    public void addString(String owner, String message){
        model.addElement(owner + " : " +  message);
    }

    public void clearList(){
        model.clear();
    }

    // Custom cell renderer class
    private class CustomCellRenderer extends javax.swing.DefaultListCellRenderer{

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            //Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            JLabel renderer = new JLabel(value.toString());

            // Set the preferred size to adjust the cell height (adjust the height as needed)
            renderer.setPreferredSize(new Dimension(renderer.getWidth(), 30));

            if (isSelected) {
                renderer.setBackground(list.getSelectionBackground());
                renderer.setForeground(list.getSelectionForeground());
            } else {
                renderer.setBackground(list.getBackground());
                renderer.setForeground(list.getForeground());
            }

            // Customize the font size here (adjust the font size as needed)
            Font font = new Font("Serif", Font.PLAIN, 16); // Example: Font size 16
            renderer.setFont(font);

            renderer.setOpaque(true);

            return renderer;
        }

    }

    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {
        if(e.getClickCount()==2){
            int index = locationToIndex(e.getPoint());
            System.out.println("row in JList : " + index);
            CustomModalDialog customModalDialog = new CustomModalDialog(this, "lineList", "C:\\javaclass\\abuji\\CSNet\\demo\\boardImage.csr");
            customModalDialog.showDialog();
        };
    }

    @Override
    public void mousePressed(java.awt.event.MouseEvent e) {
    }

    @Override
    public void mouseReleased(java.awt.event.MouseEvent e) {
    }

    @Override
    public void mouseEntered(java.awt.event.MouseEvent e) {
    }

    @Override
    public void mouseExited(java.awt.event.MouseEvent e) {
    }    
}
