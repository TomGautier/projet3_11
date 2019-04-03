package com.projet3.polypaint.DrawingCollabSession;

import com.projet3.polypaint.CanvasElement.ConnectionForm;
import com.projet3.polypaint.CanvasElement.ConnectionFormVertex;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CollabConnectionProperties extends CollabShapeProperties  {

    private String type;
    private String fillingColor;
    //private String borderColor;
    private int[][] vertices;
    //private int height;
    //private int width;
   // private int rotation;


    public CollabConnectionProperties(int[][] vertices, String type, String fillingColor){
        this.vertices = vertices;
        this.type = type;
        this.fillingColor =fillingColor;
    }


    public CollabConnectionProperties(JSONObject obj){
        try {
            type = obj.getString(TYPE_TAG);
            fillingColor = obj.getString(FILLING_COLOR_TAG);
            JSONArray array = obj.getJSONArray("VERTICES");
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
    public int[][] getVertices(){
        return vertices;
    }
    /*public String getBorderColor(){
        return borderColor;
    }*/

}

