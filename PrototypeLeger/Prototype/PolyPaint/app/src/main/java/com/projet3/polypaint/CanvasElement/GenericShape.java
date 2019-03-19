package com.projet3.polypaint.CanvasElement;

import android.app.FragmentManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.content.res.ResourcesCompat;

import com.projet3.polypaint.R;

public abstract class GenericShape {

    private final int SELECTION_GAP = 4;
    private final int EDIT_BUTTON_SIZE = 30;
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

        // Background
        paint.setColor(Color.LTGRAY);
        p.addRect(new RectF(getEditButton()), Path.Direction.CW);
        canvas.drawPath(p, paint);
        p.reset();

        // Pencil
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.5f);
        paint.setAntiAlias(true);
        int left = getEditButton().left;
        int bottom = getEditButton().bottom;

        p.moveTo(left, bottom);
        p.lineTo(left, bottom - EDIT_BUTTON_SIZE/3);
        p.lineTo(left + 2*EDIT_BUTTON_SIZE/3, bottom - EDIT_BUTTON_SIZE);
        p.lineTo(left + EDIT_BUTTON_SIZE, bottom - 2*EDIT_BUTTON_SIZE/3);
        p.lineTo(left + EDIT_BUTTON_SIZE/3, bottom);
        p.lineTo(left, bottom);
        p.moveTo(left + EDIT_BUTTON_SIZE/3, bottom);
        p.lineTo(left, bottom - EDIT_BUTTON_SIZE/3);

        canvas.drawPath(p, paint);
    }

    public void relativeMove(int x, int y) {
        posX += x;
        posY += y;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public abstract void showEditingDialog(FragmentManager fragmentManager);

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
    public String getBorderColorString() {
        return Integer.toHexString(style.getBorderPaint().getColor()).substring(2);
    }
    public String getBackgroundColorString() {
        return Integer.toHexString(style.getBackgroundPaint().getColor()).substring(2);
    }
    public abstract String getType();
}
