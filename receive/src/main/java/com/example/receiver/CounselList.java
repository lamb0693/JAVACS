package com.example.receiver;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class CounselList  extends JList<Object> implements MouseListener {
    private DefaultListModel<Object> model = null;
    Socket socket;

    public CounselList(Socket socket){
        this.socket = socket;

        try {
            socket.on("chat_data", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    byte[] receivedData = (byte[]) args[0];
//                    ByteArrayInputStream bais = new ByteArrayInputStream(receivedData);
                    try{
                        //ObjectInputStream ois = new ObjectInputStream(bais);
                        String strReceived = new String(receivedData.toString());
                        addString("client", strReceived);
                    } catch (Exception e){
                        System.out.println("Casting error in CSRCanvas");
                    }
                    repaint();;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        this.setBackground(Color.LIGHT_GRAY);

        model = new DefaultListModel<Object>();
        this.setModel(model);

        this.setCellRenderer(new CustomCellRenderer());
        this.addMouseListener(this);

        model.addElement("Hello");
        model.addElement("한글 사랑");
    }

    public void addString(String owner, String message){
        model.addElement(owner + " : " +  message);
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
