package com.projet3.polypaint.DrawingCollabSession;

import org.json.JSONException;
import org.json.JSONObject;

public class CollabShape {
    public static String ID_TAG = "id";
    public static String DRAWING_SESSION_TAG = "drawingSessionId";
    public static String AUTHOR_TAG = "author";
    public static String PROPERTIES_TAG = "properties";

    private String id;
    private String drawingSessionId;
    private String author;
    private CollabShapeProperties properties;

    public CollabShape(String id_, String drawingSessionId_, String author_, CollabShapeProperties properties_){
        id = id_;
        drawingSessionId = drawingSessionId_;
        author = author_;
        properties = properties_;
    }
    public CollabShape(JSONObject obj){
        try {
            id = obj.getString(ID_TAG);
            drawingSessionId = obj.getString(DRAWING_SESSION_TAG);
            author = obj.getString(AUTHOR_TAG);
            properties = new CollabShapeProperties(obj.getJSONObject(PROPERTIES_TAG));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String getId(){
        return id;
    }
    public String getDrawingSessionId(){
        return drawingSessionId;
    }
    public String getAuthor(){
        return author;
    }
    public CollabShapeProperties getProperties(){
        return properties;
    }

}
