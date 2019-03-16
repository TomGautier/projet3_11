package com.projet3.polypaint.CanvasElement;

import android.graphics.Paint;

public class PaintStyle {
    enum StrokeType{full, dotted, dashed}

    private Paint borderPaint;
    private Paint backgroundPaint;
    private Paint textPaint;
    private StrokeType strokeType;

    public PaintStyle(Paint border, Paint background, Paint text) {
        borderPaint = border;
        backgroundPaint = background;
        textPaint = text;
        strokeType = StrokeType.dashed;
    }

    Paint getBorderPaint() { return borderPaint; }
    Paint getBackgroundPaint() { return backgroundPaint; }
    Paint getTextPaint() { return textPaint; }
    StrokeType getStrokeType() { return strokeType; }

    public void setStrokeType(StrokeType type) { strokeType = type; }

}
