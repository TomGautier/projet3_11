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
    public static String MIDDLE_POINT_TAG = "middlePointCoord";
    public static String HEIGHT_TAG = "height";
    public static String WIDTH_TAG = "width";
    public static String ROTATION_TAG = "rotation";
    public static String BORDER_TYPE_TAG = "borderType";
    public static String LABEL_TAG = "label";
    public static String METHODS_TAG = "methods";
    public static String ATTRIBUTES_TAG = "attributes";

    protected String type;
    protected String fillingColor;
    private String borderColor;
    private PaintStyle.StrokeType borderType = PaintStyle.StrokeType.full;
    private int[] middlePointCoord;
    private int height;
    private int width;
    private int rotation;
    private ArrayList<String> attributes = new ArrayList<>();
    private ArrayList<String> methods = new ArrayList<>();
    private String label="";

    public CollabShapeProperties(String type_, String fillingColor_, String borderColor_) { // pour les formes de connexion
        type = type_;
        fillingColor = formatColorString(fillingColor_);
        borderColor = formatColorString(borderColor_);
    }
    public CollabShapeProperties(String type_, String fillingColor_, String borderColor_, ArrayList<String> attributes, ArrayList<String> methods,
                                 String label, PaintStyle.StrokeType borderType, int[] middlePointCoord_,
                                 int height_, int width_, int rotation_){
        type = type_;
        fillingColor = formatColorString(fillingColor_);
        borderColor = formatColorString(borderColor_);
        this.attributes = attributes;
        this.methods = methods;
        this.label = label;
        this.borderType = borderType;
        middlePointCoord = middlePointCoord_;
        height = height_;
        width = width_;
        rotation = rotation_;
    }

    public CollabShapeProperties(JSONObject obj){
        middlePointCoord = new int[2];
        try {
            type = obj.getString(TYPE_TAG);
            fillingColor = formatColorString(obj.getString(FILLING_COLOR_TAG));
            borderColor = formatColorString(obj.getString(BORDER_COLOR_TAG));
            for (int i = 0; i < 2; i++)
                middlePointCoord[i] = (int)obj.getJSONArray(MIDDLE_POINT_TAG).get(i);
            height= Integer.parseInt(obj.getString(HEIGHT_TAG));
            width = Integer.parseInt(obj.getString(WIDTH_TAG));
            rotation =Integer.parseInt(obj.getString(ROTATION_TAG));

            if (obj.has(BORDER_TYPE_TAG) && !obj.getString(BORDER_TYPE_TAG).isEmpty())
                borderType = PaintStyle.StrokeType.valueOf(obj.getString(BORDER_TYPE_TAG).toLowerCase());

            if (obj.has(LABEL_TAG))
                label = obj.getString(LABEL_TAG);
            if (obj.has(ATTRIBUTES_TAG)) {
                attributes = new ArrayList<>();
                JSONArray attr = obj.getJSONArray(ATTRIBUTES_TAG);
                for (int i = 0; i < attr.length(); i++)
                    attributes.add(attr.getString(i));
            }
            if (obj.has(METHODS_TAG)) {
                methods = new ArrayList<>();
                JSONArray met = obj.getJSONArray(METHODS_TAG);
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
    public String[] getAttributes(){
        return attributes.toArray(new String[attributes.size()]);
    }
    public String[] getMethods(){

        return methods.toArray(new String[methods.size()]);
    }
    public String getLabel(){
        return label;
    }
    public PaintStyle.StrokeType getBorderType(){
        return borderType;
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

    public void setLabel(String text) {
        this.label = text;
    }
    public void setTextFields(String text, String attributes, String methods) {
        setLabel(text);
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
        borderColor = formatColorString(Integer.toHexString(style.getBorderPaint().getColor()));
        fillingColor = formatColorString(Integer.toHexString(style.getBackgroundPaint().getColor()));
        borderType = style.getStrokeType();
    }
    public PaintStyle getStyle() {
        Paint backgroundPaint = new Paint();
        Paint borderPaint = new Paint();

        backgroundPaint.setColor(formatColorInt(fillingColor));
        borderPaint.setColor(formatColorInt(borderColor));

        return new PaintStyle(borderPaint, backgroundPaint, new Paint(borderPaint), borderType);
    }

    private int formatColorInt(String color) {
        int cutNb = 0;
        if (color.length() == 8) cutNb = 2;
        else if (color.length() == 9) cutNb = 3;
        return Integer.decode("#" + color.substring(cutNb)) + 0xff000000;
    }

    private String formatColorString(String color) {
        System.out.println("Input color : " + color);
        if (color.length() == 9) return color;
        else if (color.length() == 8) return "#" + color;
        else {
            System.out.println("Invalid color string detected : " + color);
            return color;
        }
    }

}

