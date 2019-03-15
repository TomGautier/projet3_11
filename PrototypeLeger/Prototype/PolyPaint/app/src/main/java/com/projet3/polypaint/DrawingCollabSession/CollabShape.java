package com.projet3.polypaint.DrawingCollabSession;

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
