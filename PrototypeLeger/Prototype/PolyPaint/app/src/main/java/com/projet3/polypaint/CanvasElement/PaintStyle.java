package com.projet3.polypaint.CanvasElement;

import android.graphics.Paint;

public class PaintStyle {
    public enum StrokeType{full, dotted, dashed}

    private Paint borderPaint;
    private Paint backgroundPaint;
    private Paint textPaint;
    private StrokeType strokeType;

    public PaintStyle(Paint border, Paint background, Paint text) {
        borderPaint = border;
        backgroundPaint = background;
        textPaint = text;
        strokeType = StrokeType.full;
    }

    public PaintStyle(PaintStyle src) {
        borderPaint = src.borderPaint;
        backgroundPaint = src.backgroundPaint;
        textPaint = src.textPaint;
        strokeType = src.strokeType;
    }

    public Paint getBorderPaint() { return borderPaint; }
    public Paint getBackgroundPaint() { return backgroundPaint; }
    public Paint getTextPaint() { return textPaint; }
    public StrokeType getStrokeType() { return strokeType; }

    public void setStrokeType(StrokeType type) { strokeType = type; }

}
