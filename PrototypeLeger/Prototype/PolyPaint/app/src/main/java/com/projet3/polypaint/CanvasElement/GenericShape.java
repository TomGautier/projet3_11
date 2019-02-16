package com.projet3.polypaint.CanvasElement;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public abstract class GenericShape {

    private final int SELECTION_GAP = 4;

    protected int posX;
    protected int posY;
    protected int width;
    protected int height;
    protected PaintStyle style;

    public GenericShape(int x, int y, int width, int height, PaintStyle style) {
        this.posX = x;
        this.posY = y;
        this.width = width;
        this.height = height;
        this.style = style;
    }

    public abstract void drawOnCanvas(Canvas canvas);

    public void drawSelectionBox(Canvas canvas, Paint paint) {
        int w2 = width/2 + SELECTION_GAP;
        int h2 = height/2 + SELECTION_GAP;

        Path p = new Path();

        p.addRect(posX - w2, posY - h2, posX + w2, posY + h2, Path.Direction.CW);

        canvas.drawPath(p, paint);
    }

    public boolean isOverPoint(int x, int y) {
        int w2 = width/2;
        int h2 = height/2;

        return x > posX - w2 && x < posX + w2 &&
                y > posY - h2 && y < posY + h2;
    }
}
