package com.projet3.polypaint.CanvasElement;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

public abstract class GenericShape {

    private final int SELECTION_GAP = 4;
    private final int EDIT_BUTTON_SIZE = 25;
    protected final int CLONE_OFFSET = 30;

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

    public abstract GenericShape clone();

    public void drawSelectionBox(Canvas canvas, Paint paint) {
        int w2 = width/2 + SELECTION_GAP;
        int h2 = height/2 + SELECTION_GAP;

        Path p = new Path();

        p.addRect(posX - w2, posY - h2, posX + w2, posY + h2, Path.Direction.CW);

        canvas.drawPath(p, paint);

        drawEditButton(canvas);
    }
    private void drawEditButton(Canvas canvas) {
        Paint paint = new Paint();
        Path p = new Path();

        p.addRect(new RectF(getEditButton()), Path.Direction.CW);

        canvas.drawPath(p, paint);
    }

    public Rect getBoundingBox() {
        int w2 = width/2;
        int h2 = height/2;

        return new Rect(posX - w2, posY - h2, posX + w2, posY + h2);
    }
    public Rect getEditButton() {
        int w2 = width/2;
        int h2 = height/2;

        return new Rect(posX + w2, posY - h2 - EDIT_BUTTON_SIZE, posX + w2 + EDIT_BUTTON_SIZE, posY - h2);
    }

    public void relativeMove(int x, int y) {
        posX += x;
        posY += y;
    }

    public abstract void showEditingDialog(DialogListener listener);
}
