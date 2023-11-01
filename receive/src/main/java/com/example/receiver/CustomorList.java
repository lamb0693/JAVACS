package com.example.receiver;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

class RoomInfo {
    int roomSize; 
    String roomName; 

    public int getRoomSize() {
        return roomSize;
    }

    public void setRoomSize(int roomSize) {
        this.roomSize = roomSize;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}


public class CustomorList extends JList<String> implements MouseListener{

    private DefaultListModel<String> model = null;

    private WndFrame wndFrame;
    private Socket socket = null;

    public CustomorList(WndFrame wndFrame, Socket socket){
        this.wndFrame = wndFrame;
        this.socket = socket;



        try {
            socket.on("counsel_rooms_info", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    ArrayList<RoomInfo> roomList = new ArrayList<>();
                    try{
                        System.out.println(args[0]);

                        Gson gson = new Gson();
                        String jsonData = (String) args[0];
                        Type roomInfoListType = new TypeToken<ArrayList<RoomInfo>>() {}.getType();
                        roomList = gson.fromJson(jsonData, roomInfoListType);
                        System.out.println(roomList.toString());

                    } catch (Exception e){
                        System.out.println("Casting error in CustomorList");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


        this.setBackground(Color.green);

        model = new DefaultListModel<String>();
        this.setModel(model);

        this.setCellRenderer(new CustomCellRenderer());
        this.addMouseListener(this);

        model.addElement("01031795981");
        model.addElement("01068011307");
    }

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

    private void setItemsInCounselList(String tel){
        final CounselList counselList = wndFrame.getCounselList();
        counselList.readFromBoard(tel);
        // Builder builder = new Retrofit.Builder();
        // Retrofit retrofit = builder.baseUrl("http://localhost:8080/").addConverterFactory(GsonConverterFactory.create()).build();
        // INetworkService iNetworkService = retrofit.create(INetworkService.class);
        // Call<List<ResponseBoardList>> apicall = iNetworkService.listBoard("Bearer:"+wndFrame.getAccessToken(), tel, 10);
        // apicall.enqueue(new Callback<List<ResponseBoardList>>(){
        //     @Override
        //     public void onFailure(Call<List<ResponseBoardList>> arg0, Throwable arg1) {
        //         System.out.println("api call failure");
  
        //     }
        //     @Override
        //     public void onResponse(Call<List<ResponseBoardList>> arg0, Response<List<ResponseBoardList>> arg1) {
        //         System.out.println("api call success");
        //         if( arg1.isSuccessful() ) {
        //             List<ResponseBoardList> boardList = arg1.body();
        //             counselList.clearList();
        //             for(ResponseBoardList board : boardList){
        //                 //System.out.println(board.getName());
        //                 counselList.addString(board.getName(), board.getContent()+","+board.getMessage());
        //             }
        //             System.out.println(boardList);
        //             JOptionPane.showMessageDialog(wndFrame, "list downloaded");
        //         } else {
        //             int response  = arg1.code();
        //             JOptionPane.showMessageDialog(wndFrame, "resoponse not ok :" + response);
        //         }
        //     }
        // });

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getClickCount()==2){
            int index = locationToIndex(e.getPoint());
            System.out.println("row in JList : " + index);
            String customerTel = model.get(index);
            setItemsInCounselList(customerTel);
            wndFrame.setCustomorTel(customerTel);
        };
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
}
