package com.example.receiver;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.awt.*;

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
                    ArrayList<ArrayList<FloatPoint>> lineListF = null;

                    try {
                        // Create Gson instance
                        Gson gson = new Gson();

                        // Deserialize JSON data into your data structure

                        String jsonData = (String) args[0];
                        lineListF = gson.fromJson(
                                jsonData,
                                new TypeToken<ArrayList<ArrayList<FloatPoint>>>() {}.getType()
                        );
  

                        // Now you have your data in lineListF
                        for (ArrayList<FloatPoint> lineF : lineListF) {
                            for (FloatPoint pointF : lineF) {
                                System.out.println("x: " + pointF.x + ", y: " + pointF.y);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Handle any file I/O exceptions
                    }

                    if(lineListF == null){
                        System.err.println("read failed");
                    }

                    ArrayList<ArrayList<Point>> newLineList = new ArrayList<>();
                    for(ArrayList<FloatPoint> lineF : lineListF){
                        ArrayList<Point> newLine = new ArrayList<>();
                        for(FloatPoint pointF : lineF){
                            Point point = new Point((int)pointF.x, (int)pointF.y);
                            newLine.add(point);
                        }
                        newLineList.add(newLine);
                    }

                    lineList = newLineList;

                    repaint();



                    // byte[] receivedData = (byte[]) args[0];
                    // ByteArrayInputStream bais = new ByteArrayInputStream(receivedData);
                    // try{
                    //     ObjectInputStream ois = new ObjectInputStream(bais);
                    //     lineList = (ArrayList<ArrayList<Point>> )ois.readObject();
                    // } catch (Exception e){
                    //     System.out.println("Casting error in CSRCanvas");
                    // }
                    // repaint();;
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
        //*****안드로이드와 호환을 위해 json으로 바꿔 저장 된 것 읽기*/

        ArrayList<ArrayList<FloatPoint>> lineListF = null;

        try {
            // Create Gson instance
            Gson gson = new Gson();

            // Deserialize JSON data into your data structure

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                lineListF = gson.fromJson(
                    reader,
                    new TypeToken<ArrayList<ArrayList<FloatPoint>>>() {}.getType()
                );
            }

            // Now you have your data in lineListF
            for (ArrayList<FloatPoint> lineF : lineListF) {
                for (FloatPoint pointF : lineF) {
                    System.out.println("x: " + pointF.x + ", y: " + pointF.y);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle any file I/O exceptions
        }

        if(lineListF == null){
            System.err.println("read failed");
        }
        ArrayList<ArrayList<Point>> newLineList = new ArrayList<>();
        for(ArrayList<FloatPoint> lineF : lineListF){
            ArrayList<Point> newLine = new ArrayList<>();
            for(FloatPoint pointF : lineF){
                Point point = new Point((int)pointF.x, (int)pointF.y);
                newLine.add(point);
            }
            newLineList.add(newLine);
        }

        this.lineList = newLineList;


        // FileInputStream fileInputStream = null;
        // ObjectInputStream objectInputStream = null;
        // try{
        //     fileInputStream = new FileInputStream(filePath);
        //     objectInputStream = new ObjectInputStream(fileInputStream);

        //     // Read the object from the file
            
        //     lineList =   ( ArrayList<ArrayList<Point>>) objectInputStream.readObject();
        //     if(lineList == null) throw new RuntimeException("not a line data file");
        // } catch(Exception e){
        //     System.out.println(e.getMessage());
        // } finally {
        //     try{
        //         objectInputStream.close();
        //         fileInputStream.close();
        //     } catch (Exception e){
        //         System.out.println(e.getMessage());
        //     }
        // }
        
    }

    public void init(){
        setBackground(Color.BLUE);
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
        ArrayList<ArrayList<FloatPoint>> lineListF = new ArrayList<>();
        // convert lineList
        for(ArrayList<Point> line : lineList){
            ArrayList<FloatPoint> lineF = new ArrayList<>();
            for(Point point : line){
                FloatPoint pointF = new FloatPoint(point.x, point.y);
                lineF.add(pointF);
            }
            lineListF.add(lineF);
        }

        // Create Gson instance
        Gson gson = new Gson();

        // Convert lineListF to JSON
        String json = gson.toJson(lineListF);

        // Save the JSON to a file
        try (FileWriter writer = new FileWriter("boardImage.json")) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //*****안드로이드와 호환을 위해 json으로 바꿔 저장 */
        // try {
        //     FileOutputStream fileOutputStream = new FileOutputStream("boardImage.csr");
        //     ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

        //     // Write the object to the file
        //     objectOutputStream.writeObject(lineList);

        //     objectOutputStream.close();
        //     fileOutputStream.close();

        //     clearAll();

        //     System.out.println("ArrayList saved to boardImage.csr");
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
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
