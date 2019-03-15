package com.projet3.polypaint.DrawingCollabSession;

public class CollabShapeProperties {
    public static String TYPE_TAG = "type";
    public static String FILLING_COLOR_TAG = "fillingColor";
    public static String BORDER_COLOR_TAG = "borderColor";
    public static String MIDDLE_POINT_TAG = "middlePointCoord";
    public static String HEIGHT_TAG = "height";
    public static String WIDTH_TAG = "width";
    public static String ROTATION_TAG = "rotation";

    private String type;
    private String fillingColor;
    private String borderColor;
    private int[] middlePointCoord;
    private int height;
    private int width;
    private int rotation;

    public CollabShapeProperties(String type_, String fillingColor_, String borderColor_, int[] middlePointCoord_,
                                 int height_, int width_, int rotation_){
        type = type_;
        fillingColor = fillingColor_;
        borderColor = borderColor_;
        middlePointCoord = middlePointCoord_;

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
    public int[] getMiddlePointCoord(){
        return middlePointCoord;
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

