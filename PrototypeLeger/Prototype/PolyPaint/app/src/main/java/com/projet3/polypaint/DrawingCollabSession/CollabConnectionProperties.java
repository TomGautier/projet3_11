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
    public static String CATEGORY_TAG = "category";
    public static String FILLING_COLOR_TAG = "fillingColor";
    public static String VERTICES_TAG = "vertices";
    public static String ID_SHAPE1_TAG = "idShape1";
    public static String ID_SHAPE2_TAG = "idShape2";
    public static String INDEX1_TAG = "index1";
    public static String INDEX2_TAG = "index2";
    public static String Q1_TAG = "q1";
    public static String Q2_TAG = "q2";


    private String connectionType;
    private int[][] vertices;
    private String idShape1;
    private String idShape2;
    private int index1;
    private int index2;
    private String q1;
    private String q2;
    private float thick;


    public CollabConnectionProperties(int[][] vertices, float thick, String type, String connectionType, String fillingColor, String borderColor
    ,String idShape1, String idShape2, int index1, int index2){
        super(type,fillingColor,borderColor);
        this.idShape1 = idShape1;
        this.idShape2 = idShape2;
        this.index1 = index1;
        this.index2 = index2;
        this.q1 = null;
        this.q2 = null;
        this.vertices = vertices;
        this.connectionType = connectionType;
        this.thick = thick;
    }


    public CollabConnectionProperties(JSONObject obj) throws JSONException {
        super(obj.getString(TYPE_TAG), obj.getString(FILLING_COLOR_TAG), obj.getString(BORDER_COLOR_TAG));
        connectionType = obj.getString(CATEGORY_TAG);
        JSONArray array = obj.getJSONArray(VERTICES_TAG);
        ArrayList<int[]> list = new ArrayList();
        int index = 0;
        for (int i = 0; i < array.length(); i++) {
            list.add(new int[] {(int) array.getJSONArray(index).get(0),(int)array.getJSONArray(index).get(1)});
            index++;
        }
        vertices = list.toArray(new int[list.size()][2]);
        idShape1 = obj.getString(ID_SHAPE1_TAG);
        idShape2 = obj.getString(ID_SHAPE2_TAG);
        index1 =  (int)obj.get(INDEX1_TAG);
        index2 = (int)obj.get(INDEX2_TAG);
        thick =  (int)obj.get(HEIGHT_TAG);
    }

    public String getConnectionType(){
        return connectionType;
    }
    public int[][] getVertices(){
        return vertices;
    }
    public String getIdShape1(){
        return idShape1;
    }
    public String getIdShape2(){
        return idShape2;
    }
    public int getIndex1(){
        return index1;
    }
    public int getIndex2(){
        return index2;
    }
    public float getThick(){
        return thick;
    }
    /*public String getBorderColor(){
        return borderColor;
    }*/

}

