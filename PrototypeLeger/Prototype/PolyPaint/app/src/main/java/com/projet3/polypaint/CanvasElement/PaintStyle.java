package com.projet3.polypaint.CanvasElement;

import android.graphics.Paint;

public class PaintStyle {

    private Paint borderPaint;
    private Paint backgroundPaint;
    private Paint textPaint;

    public PaintStyle(Paint border, Paint background, Paint text) {
        borderPaint = border;
        backgroundPaint = background;
        textPaint = text;
    }

    Paint getBorderPaint() { return borderPaint; }
    Paint getBackgroundPaint() { return backgroundPaint; }
    Paint getTextPaint() { return textPaint; }

}
