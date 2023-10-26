package com.example.receiver;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class CustomModalDialog {
    Window wnd = null;
    JDialog dialog = null;
    String text = null;
    ArrayList<ArrayList<Point>> lineList = null;
    CSRCanvas canvas = null;
    Uploader uploader = null;


    public CustomModalDialog(Window wnd){
        this.wnd = wnd;
        dialog = new JDialog(this.wnd, "Modal Dialog");
    }

    public CustomModalDialog(Window wnd, String text){
        this.wnd = wnd;
        dialog = new JDialog(this.wnd, "Modal Dialog");
        this.text= text;
    }

    // CSRCanvas uploader 
    // 빈 canvas로 시작
    public CustomModalDialog(Uploader uploader ){
        this.uploader = uploader;
        canvas = new CSRCanvas();
        canvas.init();
    }

    public CustomModalDialog(CounselList counselList, String text, ArrayList<ArrayList<Point>> lineList){
        this.wnd = (Window)wnd;
        dialog = new JDialog(this.wnd, "Modal Dialog");

        this.text= text;
        this.lineList = lineList;
        canvas = new CSRCanvas(lineList);
    }

    public CustomModalDialog(CounselList counselList, String text, String filePath){
        this.wnd = (Window)wnd;
        dialog = new JDialog(this.wnd, "Modal Dialog");

        this.text= text;
        canvas = new CSRCanvas(filePath);
        canvas.init();
    }

    public CustomModalDialog(CounselList counselList) {
        this.wnd = (Window)wnd;
    }

    public void showDialog() {
        dialog.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);

        JLabel label = null;
        if(text!=null) label = new JLabel(text);
        JButton closeButton = new JButton("Close");

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose(); // Close the dialog
            }
        });

        dialog.setLayout(new BorderLayout());
        if (label!=null)dialog.add(label, BorderLayout.NORTH);
        dialog.add(canvas, BorderLayout.CENTER);
        dialog.add(closeButton, BorderLayout.SOUTH);

        dialog.setSize(500, 900);
        dialog.setLocationRelativeTo(wnd); // Center on the parent frame
        dialog.setVisible(true); // Display the modal dialog
    }    
}
