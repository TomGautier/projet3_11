package com.projet3.polypaint.CanvasElement;

import android.app.FragmentManager;
import android.graphics.Canvas;
import android.graphics.Path;

import com.projet3.polypaint.DrawingSession.ImageEditingDialogManager;

public class UMLClass extends GenericTextShape {
    protected final static int DEFAULT_WIDTH = 180;
    protected final static int DEFAULT_HEIGHT = 150;

    private String attributes = "";
    private String methods = "";

    public UMLClass(String id, int x, int y, int width, int height, PaintStyle style) {
        super(id, x, y, style);

        if (width > this.width) this.width = width;
        this.height = height;
    }
    public UMLClass(String id, int x, int y, int width, int height, String name, String attributes, String methods, PaintStyle style) {
        super(id, x, y, style, name);

        this.attributes = attributes;
        this.methods = methods;

        if (width > this.width) this.width = width;
        this.height = height;
    }
    public UMLClass clone() {
        return new UMLClass(id + "clone", this.posX + CLONE_OFFSET, this.posY + CLONE_OFFSET, width, height, text, attributes, methods, this.style);
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

        canvas.drawText(text, posX, posY - h2 + FONT_SIZE, style.getTextPaint());
    }

    public void showEditingDialog(FragmentManager fragmentManager) {
        ImageEditingDialogManager.getInstance().showClassEditingDialog(fragmentManager, style, text);
    }


    @Override
    protected void adjustWidthToText() {
        int currentWidth = width;
        super.adjustWidthToText();
        width += PADDING;

        if (currentWidth > width ) width = currentWidth;
    }

    public void setAttributes(String attributes) { this.attributes = attributes; }
    public void setMethods(String methods) { this.methods = methods; }
}
