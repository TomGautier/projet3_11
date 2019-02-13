package com.projet3.polypaint.CanvasElement;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.res.ResourcesCompat;

import com.projet3.polypaint.R;

public abstract class GenericShape {
    protected int posX;
    protected int posY;
    protected int width;
    protected int height;
    protected Paint paint;

    public GenericShape(int x, int y, int width, int height, Paint paint) {
        this.posX = x;
        this.posY = y;
        this.width = width;
        this.height = height;
        this.paint = paint;
    }

    public abstract void drawOnCanvas(Canvas canvas);
}
