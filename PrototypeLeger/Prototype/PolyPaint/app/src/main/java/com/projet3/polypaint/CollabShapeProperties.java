package com.projet3.polypaint;

public class CollabShapeProperties {
    public static String TYPE_TAG = "type";
    public static String FILLING_COLOR_TAG = "fillingColor";
    public static String BORDER_COLOR_TAG = "borderColor";
    public static String MIDDLE_POINTX_TAG = "middlePointCoordX";
    public static String MIDDLE_POINTY_TAG = "middlePointCoordY";
    public static String HEIGHT_TAG = "height";
    public static String WIDTH_TAG = "width";
    public static String ROTATION_TAG = "rotation";

    private String type;
    private String fillingColor;
    private String borderColor;
    private int middlePointCoordX;
    private int middlePointCoordY;
    private int height;
    private int width;
    private int rotation;

    public CollabShapeProperties(String type_, String fillingColor_, String borderColor_, int middlePointCoordX_,int middlePointCoordY_,
                                 int height_, int width_, int rotation_){
        type = type_;
        fillingColor = fillingColor_;
        borderColor = borderColor_;
        middlePointCoordX = middlePointCoordX_;
        middlePointCoordY = middlePointCoordY_;
        height = height_;
        width = width_;
        rotation = rotation_;
    }
    public String getType(){
        return type;
    }
    public String getFillingColor(){
        return fillingColor;
    }
    public String getBorderColor(){
        return borderColor;
    }
    public int getMiddlePointCoordX(){
        return middlePointCoordX;
    }

    public int getMiddlePointCoordY(){
        return middlePointCoordY;
    }
    public int getHeight(){
        return height;
    }
    public int getWidth(){
        return width;
    }
    public int getRotation(){
        return rotation;
    }
}

