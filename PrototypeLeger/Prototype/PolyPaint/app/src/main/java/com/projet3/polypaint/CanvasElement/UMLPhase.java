package com.projet3.polypaint.CanvasElement;

import android.app.FragmentManager;
import android.graphics.Canvas;
import android.graphics.Path;

import com.projet3.polypaint.DrawingSession.ImageEditingDialogManager;

public class UMLPhase extends GenericTextShape {
    private final int PADDING = 18;
    protected final static int DEFAULT_WIDTH = 400;
    protected final static int DEFAULT_HEIGHT = 300;

    public UMLPhase(String id, int x, int y, int width, int height, PaintStyle style) {
        super(id, x, y, style);
        this.width = width;
        this.height = height;
    }
    private UMLPhase(String id, int x, int y, int width, int height, PaintStyle style, String contents) {
        super(id, x, y, style, contents);
        this.width = width;
        this.height = height;
    }

    @Override
    public void drawOnCanvas(Canvas canvas) {
        int w2 = width/2;
        int h2 = height/2;

        Path p = new Path();

        p.addRect(posX - w2, posY - h2, posX + w2, posY - h2 + FONT_SIZE + PADDING, Path.Direction.CW);

        canvas.drawPath(p, style.getBackgroundPaint());
        //canvas.drawPath(p, style.getBorderPaint());
        traceStyledLine(posX - w2, posY - h2, posX + w2, posY - h2, canvas);
        traceStyledLine(posX + w2, posY - h2, posX + w2, posY + h2, canvas);
        traceStyledLine(posX + w2, posY + h2, posX - w2, posY + h2, canvas);
        traceStyledLine(posX - w2, posY + h2, posX - w2, posY - h2, canvas);
        traceStyledLine(posX - w2, posY - h2 + FONT_SIZE + PADDING, posX + w2, posY - h2 + FONT_SIZE + PADDING, canvas);

        canvas.drawText(text, posX, posY - h2 + FONT_SIZE, style.getTextPaint());
    }

    public UMLPhase clone() {
        return new UMLPhase(id + "clone",this.posX + CLONE_OFFSET, this.posY + CLONE_OFFSET, width, height, this.style, text);
    }

    public void showEditingDialog(FragmentManager fragmentManager) {
        ImageEditingDialogManager.getInstance().showTextAndStyleDialog(fragmentManager, style, text);
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }
}
