package com.example;

public class FloatPoint{
    float x;
    float y;
    
    public FloatPoint() {
    }

    public FloatPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "FloatPoint [x=" + x + ", y=" + y + "]";
    }
 
}