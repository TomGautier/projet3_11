package com.projet3.polypaint.CanvasElement;

import android.app.FragmentManager;
import android.graphics.Canvas;
import android.graphics.Path;

import com.projet3.polypaint.DrawingSession.ImageEditingDialogManager;

public class TextBox extends GenericShape {
    public static final int FONT_SIZE = 25;
    public static final String TYPE = "text_box";
    private String text = "";

    public TextBox(String id, int x, int y, PaintStyle style, float angle) {
        super(id, x, y, 0, FONT_SIZE, style, angle);
        text = "";
        adjustWidthToText();
    }

    private TextBox(String id, int x, int y, PaintStyle style, String contents, float angle) {
        super(id, x, y, 0, FONT_SIZE, style, angle);
        text = contents;
        adjustWidthToText();
    }

    @Override
    public void setAnchorPoints() {
    }

    @Override
    public void drawOnCanvas(Canvas canvas) {
        canvas.drawText(text, posX, posY + height / 2, style.getTextPaint());
    }

    public TextBox clone() {
        return new TextBox(id + "clone",this.posX + CLONE_OFFSET, this.posY + CLONE_OFFSET, this.style, text, angle);
    }

    private void adjustWidthToText() {
        if (text.length() != 0) {
            // Font is monospace, all characters are same width
            float[] charWidth = new float[1];
            style.getTextPaint().getTextWidths(text, 0, 1, charWidth);
            width = (int) charWidth[0] * text.length();
        }
    }

    public void setText(String text) {
        this.text = text;
        adjustWidthToText();
    }
    public String getText() {
        return text;
    }

    public void showEditingDialog(FragmentManager fragmentManager) {
        ImageEditingDialogManager.getInstance().showTextEditingDialog(fragmentManager, text);
    }

    public String getType() { return TYPE; }
}
