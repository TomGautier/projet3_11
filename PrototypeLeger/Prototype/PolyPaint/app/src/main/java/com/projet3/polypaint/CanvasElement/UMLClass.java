package com.projet3.polypaint.CanvasElement;

import android.app.FragmentManager;
import android.graphics.Canvas;
import android.graphics.Path;

public class UMLClass extends GenericShape {
    protected final static int DEFAULT_WIDTH = 180;
    protected final static int DEFAULT_HEIGHT = 150;

    public static final String TYPE = "UmlClass";

    public UMLClass(String id, int x, int y, int width, int height, PaintStyle style) {
        super(id, x, y, width, height, style);
        //width = DEFAULT_WIDTH;
        //height = DEFAULT_HEIGHT;
    }
    public UMLClass clone() {
        return new UMLClass(id + "clone", this.posX + CLONE_OFFSET, this.posY + CLONE_OFFSET, width, height, this.style);
    }

    @Override
    public void drawOnCanvas(Canvas canvas) {
        int w2 = width/2;
        int h2 = height/2;

        Path p = new Path();

        p.addRect(posX - w2, posY - h2, posX + w2, posY + h2, Path.Direction.CW);

        canvas.drawPath(p, style.getBackgroundPaint());
        canvas.drawPath(p, style.getBorderPaint());
    }

    public void showEditingDialog(FragmentManager fragmentManager) {
        /* Do nothing for now*/
        // ImageEditingDialogManager.getInstance().showXYZDialog(fragmentManager);
    }

    public String getType() { return TYPE; }
}
