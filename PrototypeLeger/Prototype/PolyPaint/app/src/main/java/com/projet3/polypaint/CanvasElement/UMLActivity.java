package com.projet3.polypaint.CanvasElement;

import android.app.FragmentManager;
import android.graphics.Canvas;
import android.graphics.Path;

public class UMLActivity extends GenericShape {
    private final int DEFAULT_WIDTH = 90;
    private final int DEFAULT_HEIGHT = 60;

    public UMLActivity(String id, int x, int y, PaintStyle style) {
        super(id, x, y, 0, 0, style);
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
    }
    public UMLActivity clone() {
        return new UMLActivity(id,this.posX + CLONE_OFFSET, this.posY + CLONE_OFFSET, this.style);
    }

    @Override
    public void drawOnCanvas(Canvas canvas) {
        int w2 = width/2;
        int h2 = height/2;

        Path p = new Path();

        p.moveTo(posX - w2, posY - h2);
        p.lineTo(posX + w2 - h2, posY - h2);
        p.lineTo(posX + w2, posY);
        p.lineTo(posX + w2 - h2, posY + h2);
        p.lineTo(posX - w2, posY + h2);
        p.lineTo(posX - w2, posY - h2);

        canvas.drawPath(p, style.getBackgroundPaint());
        canvas.drawPath(p, style.getBorderPaint());
    }

    public void showEditingDialog(FragmentManager fragmentManager) {
        /* Do nothing for now*/
        // ImageEditingDialogManager.getInstance().showXYZDialog(fragmentManager);
    }
}
