package com.projet3.polypaint.CanvasElement;

import android.graphics.Paint;
import android.graphics.Typeface;

public class PaintStyle {
    public enum StrokeType{Full, Solid, Dotted, Dashed, Dash}

    private final float DEFAULT_STROKE_WIDTH = 2f;

    private Paint borderPaint;
    private Paint backgroundPaint;
    private Paint textPaint;
    private StrokeType strokeType;

    public PaintStyle(Paint border, Paint background, Paint text, StrokeType stroke) {
        // Border paint
        borderPaint = border;
        borderPaint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeCap(Paint.Cap.ROUND);
        borderPaint.setAntiAlias(true);

        // Background paint
        backgroundPaint = background;
        borderPaint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
        backgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        // Text paint
        textPaint = text;
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setTextSize(TextBox.FONT_SIZE);
        textPaint.setTypeface(Typeface.MONOSPACE);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);

        strokeType = stroke;
    }

    public PaintStyle(PaintStyle src) {
        borderPaint = new Paint(src.borderPaint);
        backgroundPaint = new Paint(src.backgroundPaint);
        textPaint = new Paint(src.textPaint);
        strokeType = src.strokeType;
    }

    public Paint getBorderPaint() { return borderPaint; }
    public Paint getBackgroundPaint() { return backgroundPaint; }
    public Paint getTextPaint() { return textPaint; }
    public StrokeType getStrokeType() { return strokeType; }

    public void setStrokeType(StrokeType type) { strokeType = type; }
    public void setBorderColor(int color) {
        borderPaint.setColor(color);
        textPaint.setColor(color);
    }
    public void setBackgroundColor(int color) {
        backgroundPaint.setColor(color);
    }
}
