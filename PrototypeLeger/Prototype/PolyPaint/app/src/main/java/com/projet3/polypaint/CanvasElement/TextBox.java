package com.projet3.polypaint.CanvasElement;

import android.graphics.Canvas;

public class TextBox extends GenericShape {
    public static final int FONT_SIZE = 25;
    private String text = "TEST";

    public TextBox(int x, int y, PaintStyle style) {
        super(x, y, 0, FONT_SIZE, style);
        adjustWidthToText();
    }

    @Override
    public void drawOnCanvas(Canvas canvas) {
        canvas.drawText(text, posX, posY + height / 2, style.getTextPaint());
    }

    public TextBox clone() {
        return new TextBox(this.posX + CLONE_OFFSET, this.posY + CLONE_OFFSET, this.style);
    }

    private void adjustWidthToText() {
        // Font is monospace, all characters are same width
        float[] charWidth = new float[1];
        style.getTextPaint().getTextWidths(text, 0, 1, charWidth);
        width = (int)charWidth[0] * text.length();
    }
}