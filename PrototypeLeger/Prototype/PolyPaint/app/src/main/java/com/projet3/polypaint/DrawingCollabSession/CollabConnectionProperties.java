package com.projet3.polypaint.DrawingCollabSession;

import com.projet3.polypaint.CanvasElement.ConnectionForm;
import com.projet3.polypaint.CanvasElement.ConnectionFormVertex;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


import com.projet3.polypaint.CanvasElement.ConnectionForm;
import com.projet3.polypaint.CanvasElement.ConnectionFormVertex;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CollabConnectionProperties extends CollabShapeProperties  {
    public static String CONNECTION_TYPE_TAG = "connectionType";
    public static String FILLING_COLOR_TAG = "fillingColor";
    public static String VERTICES_TAG = "vertices";
    private String connectionType;
    //private String borderColor;
    private int[][] vertices;
    //private int height;
    //private int width;
    // private int rotation;


    public CollabConnectionProperties(int[][] vertices, String type, String connectionType, String fillingColor){
        super(type,fillingColor);
        this.vertices = vertices;
        this.connectionType = connectionType;
    }


    public CollabConnectionProperties(JSONObject obj) throws JSONException {
        super(obj.getString(TYPE_TAG), obj.getString(FILLING_COLOR_TAG));
        connectionType = obj.getString(CONNECTION_TYPE_TAG);
        JSONArray array = obj.getJSONArray(VERTICES_TAG);
        ArrayList<int[]> list = new ArrayList();
        int index = 0;
        for (int i = 0; i < array.length(); i++) {
            list.add(new int[] {(int) array.getJSONArray(index).get(0),(int)array.getJSONArray(index).get(1)});
            index++;
        }
        vertices = list.toArray(new int[list.size()][2]);
    }

    public String getConnectionType(){
        return connectionType;
    }
    public int[][] getVertices(){
        return vertices;
    }
    /*public String getBorderColor(){
        return borderColor;
    }*/

}

