package com.example.receiver;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONObject;

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
import java.lang.reflect.Type;
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

        socket.on("counsel_rooms_info", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    System.out.println(args[0]);

                    if (args[0] instanceof JSONArray) {
                        model.clear();

                        JSONArray jsonArray = (JSONArray) args[0];
                        
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String roomName = jsonObject.getString("roomName");

                            System.out.println(roomName);
                            model.addElement(roomName);
                        }
                    } else {
                        System.out.println("Unexpected data type in args[0]");
                    }

                } catch (Exception e) {
                    System.out.println("Error handling data");
                }    
            }
        });

 


        this.setBackground(Color.green);

        model = new DefaultListModel<String>();
        this.setModel(model);

        this.setCellRenderer(new CustomCellRenderer());
        this.addMouseListener(this);

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
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(wndFrame.getAccessToken() == null || wndFrame.getAccessToken().equals("")){
            JOptionPane.showMessageDialog(wndFrame, "login 먼저 하세요");
        }
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
