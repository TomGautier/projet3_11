package com.projet3.polypaint.CanvasElement;

import android.graphics.Paint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShapeJsonConverter {

    public static GenericShape createShapeFromJson(JSONObject json) {
        GenericShape shape = null;
        try {
            JSONObject properties = json.getJSONObject("properties");

            PaintStyle style = getPaintStyleFromProperties(properties);

            /*switch (properties.getString("type")) {
                case UMLActivity.TYPE :
                    shape = new UMLActivity(properties.getJSONArray("middlePointCoord").getInt(0),
                                            properties.getJSONArray("middlePointCoord").getInt(1), style);
                    break;
                case UMLArtefact .TYPE :
                    shape = new UMLArtefact(properties.getJSONArray("middlePointCoord").getInt(0),
                                            properties.getJSONArray("middlePointCoord").getInt(1), style);
                    break;
                case UMLClass.TYPE :
                    shape = new UMLClass(properties.getJSONArray("middlePointCoord").getInt(0),
                                         properties.getJSONArray("middlePointCoord").getInt(1), style);
                    break;
                case UMLRole.TYPE :
                    shape = new UMLRole(properties.getJSONArray("middlePointCoord").getInt(0),
                                        properties.getJSONArray("middlePointCoord").getInt(1), style);
                    break;
                case TextBox.TYPE :
                    shape = new TextBox(properties.getJSONArray("middlePointCoord").getInt(0),
                                        properties.getJSONArray("middlePointCoord").getInt(1), style);
                    break;
                default :
                    break;
            }*/

            shape.setSize(properties.getInt("width"), properties.getInt("height"));
        } catch (JSONException e) { e.printStackTrace(); }

        return shape;
    }

    // TODO: get remaining values
    public static JSONObject createJsonFromShape(GenericShape shape) {
        JSONObject json = new JSONObject();
        JSONObject properties = new JSONObject();

        /*try {
            properties.put("type", shape.getType());
            properties.put("fillingColor", shape.getBackgroundColorString());
            properties.put("borderColor", shape.getBorderColorString());
            JSONArray coords = new JSONArray();
            coords.put(shape.getBoundingBox().centerX());
            coords.put(shape.getBoundingBox().centerY());
            properties.put("middlePointCoord", coords);
            properties.put("height", shape.getBoundingBox().width());
            properties.put("width", shape.getBoundingBox().height());
            properties.put("rotation", 0);

            json.put("id", UserManager.currentInstance.getUserUsername() + generateId());
            json.put("drawingSessionId", "");   // TBD
            json.put("author", UserManager.currentInstance.getUserUsername());
            json.put("properties", properties);
        } catch (JSONException e) { e.printStackTrace(); }*/

        return json;
    }

    private static PaintStyle getPaintStyleFromProperties(JSONObject json) {
        try {
            // Border paint
            int borderColor = colorFromString(json.getString("borderColor"));
            Paint borderPaint = new Paint();
            borderPaint.setColor(borderColor);

            // Background paint
            int backgroundColor = colorFromString(json.getString("fillingColor"));
            Paint backgroundPaint = new Paint();
            backgroundPaint.setColor(backgroundColor);

            // Text paint
            Paint textPaint = new Paint();
            textPaint.setColor(borderColor);

            return new PaintStyle(borderPaint, backgroundPaint, textPaint);
        } catch (JSONException e) { e.printStackTrace(); }

        return null;
    }

    private static int colorFromString(String source) {
        return Integer.decode("0x" + source);
    }
    private static String generateId() {
        // TODO : Generate random id, procedure TBD with Tom & Olivier
        return "_testId";
    }
}
