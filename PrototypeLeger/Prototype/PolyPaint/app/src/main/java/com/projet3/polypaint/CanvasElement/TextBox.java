package com.projet3.polypaint.CanvasElement;

import android.app.FragmentManager;
import android.graphics.Canvas;

import com.projet3.polypaint.DrawingSession.ImageEditingDialogManager;

public class TextBox extends GenericTextShape {

    public static final String TYPE = "Text";

    public TextBox(String id, int x, int y, PaintStyle style) {
        super(id, x, y, style);
    }
    public TextBox(String id, int x, int y, PaintStyle style, String contents) {
        super(id, x, y, style, contents);
    }

    @Override
    public void drawOnCanvas(Canvas canvas) {
        canvas.drawText(text, posX, posY + height / 2, style.getTextPaint());
    }

    public TextBox clone() {
        return new TextBox(id + "clone",this.posX + CLONE_OFFSET, this.posY + CLONE_OFFSET, this.style, text);
    }

    public void showEditingDialog(FragmentManager fragmentManager) {
        ImageEditingDialogManager.getInstance().showTextEditingDialog(fragmentManager, style, text);
    }

    public String getType() { return TYPE; }
}
