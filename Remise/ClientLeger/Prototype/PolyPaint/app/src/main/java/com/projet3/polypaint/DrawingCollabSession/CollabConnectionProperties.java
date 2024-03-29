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
    public static String VERTICESX_TAG = "pointsX";
    public static String VERTICESY_TAG = "pointsY";
    public static String ID_SHAPE1_TAG = "idShape1";
    public static String ID_SHAPE2_TAG = "idShape2";
    public static String INDEX1_TAG = "index1";
    public static String INDEX2_TAG = "index2";
    public static String Q1_TAG = "q1";
    public static String Q2_TAG = "q2";


    private String connectionType;
    private Integer[] verticesX;
    private Integer[] verticesY;
    private String idShape1;
    private String idShape2;
    private int index1;
    private int index2;
    private String q1;
    private String q2;
    private float thick;


    public CollabConnectionProperties(Integer[] verticesX, Integer[] verticesY, float thick, String type, String connectionType, String fillingColor, String borderColor
    ,String idShape1, String idShape2, int index1, int index2, String q1, String q2){
        super(type,fillingColor,borderColor);
        this.idShape1 = idShape1;
        this.idShape2 = idShape2;
        this.index1 = index1;
        this.index2 = index2;
        this.q1 = q1;
        this.q2 = q2;
        this.verticesX = verticesX;
        this.verticesY = verticesY;
        this.connectionType = connectionType;
        this.thick = thick;
    }


    public CollabConnectionProperties(JSONObject obj) throws JSONException {
        super(obj.getString(TYPE_TAG), obj.getString(FILLING_COLOR_TAG), obj.getString(BORDER_COLOR_TAG));
        connectionType = obj.getString(CATEGORY_TAG);
        JSONArray array = obj.getJSONArray(VERTICESX_TAG);
        ArrayList<Integer> list = new ArrayList();
        for (int i = 0; i < array.length(); i++) {
            list.add(array.getInt(i));
        }
        JSONArray array2 = obj.getJSONArray(VERTICESY_TAG);
        ArrayList<Integer> list2 = new ArrayList();
        for (int j = 0; j < array2.length(); j++) {
            list2.add(array2.getInt(j));
        }
        verticesX = list.toArray(new Integer[list.size()]);
        verticesY = list2.toArray(new Integer[list2.size()]);
        idShape1 = obj.getString(ID_SHAPE1_TAG);
        idShape2 = obj.getString(ID_SHAPE2_TAG);
        index1 =  (int)obj.get(INDEX1_TAG);
        index2 = (int)obj.get(INDEX2_TAG);
        thick =  (int)obj.get(HEIGHT_TAG);
    }

    public String getConnectionType(){
        return connectionType;
    }
    public Integer[] getVerticesX(){
        return verticesX;
    }
    public Integer[] getVerticesY() { return verticesY;}
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
    public String getQ1(){
        return q1;
    }
    public String getQ2(){
        return q2;
    }
    /*public String getBorderColor(){
        return borderColor;
    }*/

}

