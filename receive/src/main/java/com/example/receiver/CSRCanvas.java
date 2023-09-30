package com.example.receiver;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


import java.awt.*;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class CSRCanvas extends Canvas implements MouseMotionListener, MouseListener{

    ArrayList<Point> pointList = new ArrayList<>();
    ArrayList<ArrayList<Point>> lineList = null;
    private Socket socket;

    public CSRCanvas() {
        init();
    }

    public CSRCanvas(Socket socket){
        this.socket = socket;

        try {
            socket.on("canvas_data", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    byte[] receivedData = (byte[]) args[0];
                    ByteArrayInputStream bais = new ByteArrayInputStream(receivedData);
                    try{
                        ObjectInputStream ois = new ObjectInputStream(bais);
                        lineList = (ArrayList<ArrayList<Point>> )ois.readObject();
                    } catch (Exception e){
                        System.out.println("Casting error in CSRCanvas");
                    }
                    repaint();;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        init();
    }

    public CSRCanvas(ArrayList<ArrayList<Point>> lineList){
        this.lineList = lineList;
        init();
    }

    public CSRCanvas(String filePath){
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
        try{
            fileInputStream = new FileInputStream(filePath);
            objectInputStream = new ObjectInputStream(fileInputStream);

            // Read the object from the file
            
            lineList =   ( ArrayList<ArrayList<Point>>) objectInputStream.readObject();
            if(lineList == null) throw new RuntimeException("not a line data file");
        } catch(Exception e){
            System.out.println(e.getMessage());
        } finally {
            try{
                objectInputStream.close();
                fileInputStream.close();
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        
    }

    public void init(){
        setBackground(Color.BLUE);
        setSize(Cons.CANVAS_WIDTH, Cons.WINDOW_HEIGHT - Cons.UPPER_PANEL_HEIGHT);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);    
    }

    public void drawLine(ArrayList<Point> pointList, Graphics g){
        for(int i=0; i<pointList.size()-1; i++){
            g.drawLine((int)Math.round(pointList.get(i).getX()), 
                (int)Math.round(pointList.get(i).getY()),
                (int)Math.round(pointList.get(i+1).getX()),
                (int)Math.round( pointList.get(i+1).getY()) );
        }
    }

    public void paint(Graphics g) {
        Graphics2D g2;
        g2 = (Graphics2D) g;
        g2.setColor(Color.WHITE);
        Font font = new Font("Serif", Font.PLAIN, 13);
        g2.setFont(font);
        g2.drawString("텍스트 넣기", 10, 20);

        //drawLine(pointList, g2);
        if(lineList != null){
            for(ArrayList<Point> line : lineList){
                drawLine(line, g2);
            }
        }
    }

    public void removeLast() {
        // if(lineList.size() > 0) lineList.remove(lineList.size()-1);
        // socket.emit("canvas_data", lineList);
        // repaint();
    }

    public void clearAll() {
        // lineList.clear();
        // socket.emit("canvas_data", lineList);
        // repaint();
    }

    public void saveCurrentImage(){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("boardImage.csr");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            // Write the object to the file
            objectOutputStream.writeObject(lineList);

            objectOutputStream.close();
            fileOutputStream.close();

            clearAll();

            System.out.println("ArrayList saved to boardImage.csr");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub
        //System.out.println("dragged");
        // pointList.add(new Point(e.getX(), e.getY()));
        // repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        // System.out.println("clicked");
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //System.out.println("pressed");
        // TODO Auto-generated method stub
        // pointList.add(new Point(e.getX(), e.getY()));
        // repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //System.out.println("released");
        // TODO Auto-generated method stub
        // lineList.add(pointList);
        // pointList = new ArrayList<>();
        // repaint();
        // socket.emit("canvas_data", lineList);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
    }
    
    
}
