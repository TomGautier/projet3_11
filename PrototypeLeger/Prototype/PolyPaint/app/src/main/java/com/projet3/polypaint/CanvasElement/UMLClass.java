package com.projet3.polypaint.CanvasElement;

import android.app.FragmentManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.projet3.polypaint.DrawingSession.ImageEditingDialogManager;

import java.util.ArrayList;
import java.util.Arrays;

public class UMLClass extends GenericTextShape {
    protected final static int DEFAULT_WIDTH = 180;
    protected final static int DEFAULT_HEIGHT = 150;

    private ArrayList<String> attributes;
    private ArrayList<String> methods;

    private Paint leftAlignedText;

    public static final String TYPE = "UmlClass";

    public UMLClass(String id, int x, int y, int width, int height, PaintStyle style) {
        super(id, x, y, width, height, style);

        attributes = new ArrayList<>();
        methods = new ArrayList<>();

        initializePaint();
    }
    public UMLClass(String id, int x, int y, int width, int height, String name, String attributes, String methods, PaintStyle style) {
        super(id, x, y, width, height, style, name);

        this.attributes = new ArrayList<>();
        this.methods = new ArrayList<>();
        setAttributes(attributes);
        setMethods(methods);

        initializePaint();
    }

    private void initializePaint() {
        leftAlignedText = new Paint(style.getTextPaint());
        leftAlignedText.setTextAlign(Paint.Align.LEFT);
    }

    public UMLClass clone() {
        return new UMLClass(id + "clone", this.posX + CLONE_OFFSET, this.posY + CLONE_OFFSET,
                width, height, text, concatenateList(attributes), concatenateList(methods), this.style);
    }

    @Override
    public void drawOnCanvas(Canvas canvas) {
        int w2 = width/2;
        int h2 = height/2;

        Path p = new Path();

        p.addRect(posX - w2, posY - h2, posX + w2, posY + h2, Path.Direction.CW);

        canvas.drawPath(p, style.getBackgroundPaint());
        //canvas.drawPath(p, style.getBorderPaint());
        traceStyledLine(posX - w2, posY - h2, posX + w2, posY - h2, canvas);
        traceStyledLine(posX + w2, posY - h2, posX + w2, posY + h2, canvas);
        traceStyledLine(posX + w2, posY + h2, posX - w2, posY + h2, canvas);
        traceStyledLine(posX - w2, posY + h2, posX - w2, posY - h2, canvas);

        // Separators between class name, attributes and methods
        traceStyledLine(posX - w2, posY - h2 + FONT_SIZE + 2*PADDING, posX + w2, posY - h2 + FONT_SIZE + 2*PADDING, canvas);
        traceStyledLine(posX - w2, posY - h2 + (attributes.size() + 1) * FONT_SIZE + 4*PADDING,
                posX + w2, posY - h2 + (attributes.size() + 1) * FONT_SIZE + 4*PADDING, canvas);

        // Text
        canvas.drawText(text, posX, posY - h2 + FONT_SIZE + PADDING, style.getTextPaint());
        for (int i = 0; i < attributes.size(); i++) {
            canvas.drawText(attributes.get(i), posX - w2 + PADDING, posY - h2 + (2 + i) * FONT_SIZE + 3*PADDING, leftAlignedText);
        }
        for (int i = 0; i < methods.size(); i++) {
            canvas.drawText(methods.get(i), posX - w2 + PADDING, posY - h2 + (2 + i + attributes.size()) * FONT_SIZE + 5*PADDING, leftAlignedText);
        }
    }

    public void showEditingDialog(FragmentManager fragmentManager) {
        ImageEditingDialogManager.getInstance().showClassEditingDialog(fragmentManager, style, text,
                concatenateList(attributes), concatenateList(methods));
    }


    @Override
    protected void adjustWidthToText() {
        int currentWidth = width;
        super.adjustWidthToText();
        width += 2*PADDING;

        if (currentWidth > width ) width = currentWidth;
    }

    private void adjustHeightToText() {
        int textHeight = (attributes.size() + methods.size() + 1) * FONT_SIZE + 6*PADDING;
        if (textHeight > height) height = textHeight;
    }

    public void setAttributes(String attributes) {
        if (attributes.length() != 0) {
            String[] a = attributes.split("\n");
            this.attributes = new ArrayList<>();
            this.attributes.addAll(Arrays.asList(a));
            adjustHeightToText();
        }
        else this.attributes = new ArrayList<>();
    }
    public void setMethods(String methods) {
        if (methods.length() != 0) {
            String[] m = methods.split("\n");
            this.methods = new ArrayList<>();
            this.methods.addAll(Arrays.asList(m));
            adjustHeightToText();
        }
        else this.methods = new ArrayList<>();
    }

    @Override
    public void setStyle(PaintStyle style) {
        super.setStyle(style);
        initializePaint();
    }

    private String concatenateList(ArrayList<String> list) {
        if (list.isEmpty()) return "";
        else {
            String result = list.get(0);
            for (int i = 1; i < list.size(); i++)
                result += "\n" + list.get(i);

            return result;
        }
    }

    public String getType() { return TYPE; }
}
