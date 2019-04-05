package com.projet3.polypaint.CanvasElement;

import android.app.FragmentManager;
import android.graphics.Canvas;
import android.graphics.Path;

import com.projet3.polypaint.DrawingSession.ImageEditingDialogManager;

public class Comment extends GenericTextShape {
    public static final String TYPE = "Comment";

    public Comment(String id, int x, int y, PaintStyle style) {
        super(id, x, y, style);
        height += 2*PADDING;
    }
    public Comment(String id, int x, int y, PaintStyle style, String contents) {
        super(id, x, y, style, contents);
        height += 2*PADDING;
    }

    @Override
    public void drawOnCanvas(Canvas canvas) {
        int w2 = width/2;
        int h2 = height/2;

        Path p = new Path();

        p.addRect(posX - w2, posY - h2, posX + w2, posY + h2, Path.Direction.CW);

        canvas.drawPath(p, style.getBackgroundPaint());
        //canvas.drawPath(p, style.getBorderPaint());
        traceStyledLine(posX - w2, posY - h2, posX + w2, posY - h2, canvas);
        traceStyledLine(posX + w2, posY - h2, posX + w2, posY + h2, canvas);
        traceStyledLine(posX + w2, posY + h2, posX - w2, posY + h2, canvas);
        traceStyledLine(posX - w2, posY + h2, posX - w2, posY - h2, canvas);

        canvas.drawText(text, posX, posY + FONT_SIZE/2, style.getTextPaint());
    }

    @Override
    public GenericShape clone() {
        return new Comment(id + "clone", this.posX + CLONE_OFFSET, this.posY + CLONE_OFFSET, this.style, text);
    }

    @Override
    public void showEditingDialog(FragmentManager fragmentManager) {
            ImageEditingDialogManager.getInstance().showTextAndStyleDialog(fragmentManager, style, text);
    }

    @Override
    protected void adjustWidthToText() {
        super.adjustWidthToText();
        width += 2*PADDING;
    }

    @Override
    public String getType() { return TYPE; }
}
