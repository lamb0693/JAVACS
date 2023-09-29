package com.example.receiver;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.awt.*;

public class CSRCanvas extends Canvas implements MouseMotionListener, MouseListener{

    ArrayList<Point> pointList = new ArrayList<>();
    ArrayList<ArrayList<Point>> lineList = new ArrayList<>();

    public CSRCanvas() {
        setBackground(Color.BLUE);
        this.setSize(800, 600);
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
        repaint();
    }

    public void clearAll() {
        lineList.clear();
        repaint();
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
