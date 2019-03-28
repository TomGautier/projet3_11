package com.projet3.polypaint.CanvasElement;

import android.graphics.Paint;
import android.graphics.Typeface;

public class PaintStyle {

    private final float DEFAULT_STROKE_WIDTH = 2f;

    private Paint borderPaint;
    private Paint backgroundPaint;
    private Paint textPaint;

    public PaintStyle(Paint border, Paint background, Paint text) {
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
    }

    public Paint getBorderPaint() { return borderPaint; }
    Paint getBackgroundPaint() { return backgroundPaint; }
    Paint getTextPaint() { return textPaint; }

}
