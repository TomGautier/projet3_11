package com.projet3.polypaint.CanvasElement;

import android.graphics.Canvas;
import android.graphics.Path;

public class UMLArtefact extends GenericShape {
    private final int DEFAULT_WIDTH = 60;
    private final int DEFAULT_HEIGHT = 80;

    public UMLArtefact(int x, int y, PaintStyle style) {
        super(x, y, 0, 0, style);
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
    }
    public UMLArtefact clone() {
        return new UMLArtefact(this.posX + CLONE_OFFSET, this.posY + CLONE_OFFSET, this.style);
    }

    @Override
    public void drawOnCanvas(Canvas canvas) {
        int w2 = width/2;
        int h2 = height/2;
        int w4 = width/4;
        int h4 = height/4;

        Path p = new Path();

        p.moveTo(posX - w2, posY - h2);
        p.lineTo(posX + w4, posY - h2);
        p.lineTo(posX + w2, posY - h4);
        p.lineTo(posX + w2, posY + h2);
        p.lineTo(posX - w2, posY + h2);
        p.lineTo(posX - w2, posY - h2);

        canvas.drawPath(p, style.getBackgroundPaint());

        p.moveTo(posX + w2, posY - h4);
        p.lineTo(posX + w4, posY - h4);
        p.lineTo(posX + w4, posY - h2);

        canvas.drawPath(p, style.getBorderPaint());
    }

    public void showEditingDialog(DialogListener listener) {
        /* Do nothing, for now. */
        /* This will eventually do something like listener.showXYZDialog() */
    }
}
