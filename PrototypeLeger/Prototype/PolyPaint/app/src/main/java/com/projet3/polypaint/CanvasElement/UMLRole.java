package com.projet3.polypaint.CanvasElement;

import android.app.FragmentManager;
import android.graphics.Canvas;
import android.graphics.Path;

public class UMLRole extends GenericShape {
    protected final static int DEFAULT_WIDTH = 60;
    protected final static int DEFAULT_HEIGHT = 90;

    public UMLRole(String id,int x, int y, int width, int height, PaintStyle style) {
        super(id, x, y, width,height, style);
        //width = DEFAULT_WIDTH;
        //height = DEFAULT_HEIGHT;
    }

    @Override
    public void setAnchorPoints() {

    }

    public UMLRole clone() {
        return new UMLRole(id + "clone",this.posX + CLONE_OFFSET, this.posY + CLONE_OFFSET, width, height, this.style);
    }

    @Override
    public void drawOnCanvas(Canvas canvas) {
        int w2 = width/2;
        int h2 = height/2;
        int w4 = width/4;
        int h4 = height/4;

        Path p = new Path();

        p.moveTo(posX - w4, posY);
        p.lineTo(posX + w4, posY);
        p.lineTo(posX + w2, posY + h2);
        p.lineTo(posX - w2, posY + h2);
        p.lineTo(posX - w4, posY);
        p.addCircle(posX, posY - h4, h4, Path.Direction.CW);

        canvas.drawPath(p, style.getBackgroundPaint());
        canvas.drawPath(p, style.getBorderPaint());
    }

    public void showEditingDialog(FragmentManager fragmentManager) {
        /* Do nothing for now*/
        // ImageEditingDialogManager.getInstance().showXYZDialog(fragmentManager);
    }
}
