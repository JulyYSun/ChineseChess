package com.ys.client;

import java.awt.*;

public class ChessMan {
    //棋子类所有变量必须为私有变量
    private Color color;  //颜色
    private String name;  //名称
    private int x;  //位置坐标
    private int y;
    private boolean focus=false;  //是否选中

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isFocus() {
        return focus;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }


    public ChessMan(){

    }

    public ChessMan(Color color, String name, int x, int y) {
        this.color = color;
        this.name = name;
        this.x = x;
        this.y = y;
        this.focus=false;
    }
}
