package com.projet3.polypaint.DrawingCollabSession;

import android.graphics.Paint;

import com.projet3.polypaint.CanvasElement.PaintStyle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.spec.RSAOtherPrimeInfo;
import java.util.ArrayList;
import java.util.Arrays;

public class CollabShapeProperties {
    public static String TYPE_TAG = "type";
    public static String FILLING_COLOR_TAG = "fillingColor";
    public static String BORDER_COLOR_TAG = "borderColor";
    public static String STROKE_TAG = "strokeType";
    public static String MIDDLE_POINT_TAG = "middlePointCoord";
    public static String HEIGHT_TAG = "height";
    public static String WIDTH_TAG = "width";
    public static String ROTATION_TAG = "rotation";
    public static String LABEL_TAG = "label";
    public static String ATTRIBUTE_TAG = "attributes";
    public static String METHOD_TAG = "methods";

    private String type;
    private String fillingColor;
    private String borderColor;
    private PaintStyle.StrokeType strokeType = PaintStyle.StrokeType.full;
    private int[] middlePointCoord;
    private int height;
    private int width;
    private int rotation;

    private String text = "";
    private ArrayList<String> attributes = new ArrayList<>();
    private ArrayList<String> methods = new ArrayList<>();

    public CollabShapeProperties(String type_, String fillingColor_, String borderColor_, int[] middlePointCoord_,
                                 int height_, int width_, int rotation_){
        type = type_;
        fillingColor = fillingColor_;
        borderColor = borderColor_;
        strokeType = PaintStyle.StrokeType.full; // TODO: Add stroke type to passed parameters?
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
            strokeType = PaintStyle.StrokeType.valueOf(obj.getString(STROKE_TAG));
            for (int i = 0; i < 2; i++)
                middlePointCoord[i] = (int)obj.getJSONArray(MIDDLE_POINT_TAG).get(i);
            height= Integer.parseInt(obj.getString(HEIGHT_TAG));
            width = Integer.parseInt(obj.getString(WIDTH_TAG));
            rotation =Integer.parseInt(obj.getString(ROTATION_TAG));

            if (obj.has(LABEL_TAG))
                text = obj.getString(LABEL_TAG);
            if (obj.has(ATTRIBUTE_TAG)) {
                attributes = new ArrayList<>();
                JSONArray attr = obj.getJSONArray(ATTRIBUTE_TAG);
                for (int i = 0; i < attr.length(); i++)
                    attributes.add(attr.getString(i));
            }
            if (obj.has(METHOD_TAG)) {
                methods = new ArrayList<>();
                JSONArray met = obj.getJSONArray(METHOD_TAG);
                for (int i = 0; i < met.length(); i++)
                    methods.add(met.getString(i));
            }
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
    public PaintStyle.StrokeType getStrokeType() {
        return strokeType;
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

    public String getText() {
        return text;
    }
    public String getAttributesString() {
        if (attributes == null) return "";

        String attr = "";
        for (String a : attributes)
            attr += a + '\n';

        return attr.trim();
    }
    public String getMethodsString() {
        if (methods == null) return "";

        String met = "";
        for (String m : methods)
            met += m + '\n';

        return met.trim();
    }

    public void setText(String text) {
        this.text = text;
    }
    public void setTextFields(String text, String attributes, String methods) {
        setText(text);
        setAttributes(attributes);
        setMethods(methods);
    }

    private void setAttributes(String attributes) {
        String[] a = attributes.split("\n");
        this.attributes = new ArrayList<>();
        this.attributes.addAll(Arrays.asList(a));
    }
    private void setMethods(String methods) {
        String[] m = methods.split("\n");
        this.methods = new ArrayList<>();
        this.methods.addAll(Arrays.asList(m));
    }

    public void setStyle(PaintStyle style) {
        borderColor = Integer.toHexString(style.getBorderPaint().getColor() & 0xffffff);
        fillingColor = Integer.toHexString(style.getBackgroundPaint().getColor() & 0xffffff);
        strokeType = style.getStrokeType();
    }
    public PaintStyle getStyle() {
        Paint backgroundPaint = new Paint();
        Paint borderPaint = new Paint();
//        System.out.println("0x" + fillingColor.substring(1));
        System.out.println("---------------------------------__" + fillingColor + " -- " + borderColor);
        try {
            backgroundPaint.setColor(Integer.decode(fillingColor) + 0xff000000);
            borderPaint.setColor(Integer.decode(borderColor) + 0xff000000);
        } catch (NumberFormatException nfe) {
            backgroundPaint.setColor(Integer.decode("#" + fillingColor) + 0xff000000);
            borderPaint.setColor(Integer.decode("#" + borderColor) + 0xff000000);
        }
        return new PaintStyle(borderPaint, backgroundPaint, new Paint(borderPaint), strokeType);
    }

    public JSONArray getAttributesJson() {
        return new JSONArray(attributes);
    }
    public JSONArray getMethodsJson() {
        return new JSONArray(methods);
    }
}

