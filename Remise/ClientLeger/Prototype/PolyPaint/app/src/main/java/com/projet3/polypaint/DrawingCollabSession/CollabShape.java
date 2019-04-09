package com.projet3.polypaint.DrawingCollabSession;
import com.projet3.polypaint.CanvasElement.ConnectionForm;
import com.projet3.polypaint.DrawingSession.ImageEditingFragment;

import android.app.FragmentManager;
import android.graphics.Paint;

import com.projet3.polypaint.CanvasElement.PaintStyle;
import com.projet3.polypaint.CanvasElement.TextBox;
import com.projet3.polypaint.CanvasElement.UMLActivity;
import com.projet3.polypaint.CanvasElement.UMLArtefact;
import com.projet3.polypaint.CanvasElement.UMLClass;
import com.projet3.polypaint.CanvasElement.UMLRole;
import com.projet3.polypaint.DrawingSession.ImageEditingDialogManager;

import org.json.JSONException;
import org.json.JSONObject;

public class CollabShape {
    public static String ID_TAG = "id";
    public static String IMAGE_ID_TAG = "imageId";
    public static String AUTHOR_TAG = "author";
    public static String PROPERTIES_TAG = "properties";

    private String id;
    private String imageId;
    private String author;
    private CollabShapeProperties properties;

    public CollabShape(String id_, String drawingSessionId_, String author_, CollabShapeProperties properties_){
        id = id_;
        imageId = drawingSessionId_;
        author = author_;
        properties = properties_;
    }
    public CollabShape(JSONObject obj){
        try {
            id = obj.getString(ID_TAG);
            imageId = obj.getString(IMAGE_ID_TAG);
            author = obj.getString(AUTHOR_TAG);
            if (obj.getJSONObject(PROPERTIES_TAG).getString(CollabShapeProperties.TYPE_TAG).equals(ImageEditingFragment.ShapeType.Arrow.toString()))
                properties = new CollabConnectionProperties(obj.getJSONObject(PROPERTIES_TAG));
            else
                properties = new CollabShapeProperties(obj.getJSONObject(PROPERTIES_TAG));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String getId(){
        return id;
    }
    public String getImageId(){
        return imageId;
    }
    public String getAuthor(){
        return author;
    }
    public CollabShapeProperties getProperties(){
        return properties;
    }

    public void showEditingDialog(FragmentManager fragmentManager) {
        PaintStyle style = properties.getStyle();

        switch(properties.getType()) {
            case "UmlClass":
                ImageEditingDialogManager.getInstance().showClassEditingDialog(fragmentManager, style,
                        properties.getLabel(), properties.getAttributesString(), properties.getMethodsString());
                break;
            case "Artefact":
                ImageEditingDialogManager.getInstance().showStyleDialog(fragmentManager, style);
                break;
            case "Activity":
                ImageEditingDialogManager.getInstance().showStyleDialog(fragmentManager, style);
                break;
            case "Role":
                ImageEditingDialogManager.getInstance().showStyleDialog(fragmentManager, style);
                break;
            case "Phase":
                ImageEditingDialogManager.getInstance().showTextAndStyleDialog(fragmentManager, style, properties.getLabel());
                break;
            case "Comment":
                ImageEditingDialogManager.getInstance().showTextAndStyleDialog(fragmentManager, style, properties.getLabel());
                break;
            case "Text":
                ImageEditingDialogManager.getInstance().showTextEditingDialog(fragmentManager, style, properties.getLabel());
                break;
        }
    }
}
