package com.projet3.polypaint.CanvasElement;

import android.graphics.Paint;

public class PaintStyle {

    private Paint borderPaint;
    private Paint backgroundPaint;

    public PaintStyle(Paint border, Paint background) {
        borderPaint = border;
        backgroundPaint = background;
    }

    Paint getBorderPaint() { return borderPaint; }
    Paint getBackgroundPaint() { return backgroundPaint; }

}
