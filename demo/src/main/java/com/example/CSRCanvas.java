package com.example;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import io.socket.client.Socket;

import java.awt.*;

public class CSRCanvas extends Canvas implements MouseMotionListener, MouseListener{

    ArrayList<Point> pointList = new ArrayList<>();
    ArrayList<ArrayList<Point>> lineList = new ArrayList<>();
    private Socket socket;

    public CSRCanvas() {
        init();
    }

    public CSRCanvas(Socket socket){
        this.socket = socket;
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

        init();
        
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

        drawLine(pointList, g2);
        for(ArrayList<Point> line : lineList){
            drawLine(line, g2);
        }
    }

    public void removeLast() {
        if(lineList.size() > 0) lineList.remove(lineList.size()-1);
        sendDataToCSR();
        repaint();
    }

    public void clearAll() {
        lineList.clear();
        sendDataToCSR();
        repaint();
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

    private void sendDataToCSR(){
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(lineList);
            socket.emit( "canvas_data", baos.toByteArray());
        } catch (Exception e){
            System.out.println("send error in CSRCanvas");
        }
        
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub
        //System.out.println("dragged");
        pointList.add(new Point(e.getX(), e.getY()));
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        System.out.println("clicked");
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //System.out.println("pressed");
        // TODO Auto-generated method stub
        pointList.add(new Point(e.getX(), e.getY()));
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //System.out.println("released");
        // TODO Auto-generated method stub
        lineList.add(pointList);
        pointList = new ArrayList<>();
        repaint();
        sendDataToCSR();
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
