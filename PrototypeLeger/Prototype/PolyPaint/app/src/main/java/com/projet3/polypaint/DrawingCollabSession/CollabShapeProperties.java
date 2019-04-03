package com.projet3.polypaint.DrawingCollabSession;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.spec.RSAOtherPrimeInfo;

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
    public CollabShapeProperties() {}
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

    public CollabShapeProperties(JSONObject obj){
        middlePointCoord = new int[2];
        try {
            type = obj.getString(TYPE_TAG);
            fillingColor = obj.getString(FILLING_COLOR_TAG);
            borderColor = obj.getString(BORDER_COLOR_TAG);
            for (int i = 0; i < 2; i++)
                middlePointCoord[i] = (int)obj.getJSONArray(MIDDLE_POINT_TAG).get(i);
            height= Integer.parseInt(obj.getString(HEIGHT_TAG));
            width = Integer.parseInt(obj.getString(WIDTH_TAG));
            rotation =Integer.parseInt(obj.getString(ROTATION_TAG));
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
    public void setMiddlePointCoord(int x, int y){
        middlePointCoord[0] = x;
        middlePointCoord[1] = y;
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

