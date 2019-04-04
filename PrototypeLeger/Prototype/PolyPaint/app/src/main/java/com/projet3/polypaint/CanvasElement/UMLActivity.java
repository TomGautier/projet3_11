package com.projet3.polypaint.CanvasElement;

import android.app.FragmentManager;
import android.graphics.Canvas;
import android.graphics.Path;

import com.projet3.polypaint.DrawingSession.ImageEditingDialogManager;

public class UMLActivity extends GenericShape {
    protected final static int DEFAULT_WIDTH = 90;
    protected final static int DEFAULT_HEIGHT = 60;

    public static final String TYPE = "UmlActivity";

    public UMLActivity(String id, int x, int y, int width, int height, PaintStyle style, float angle) {
        super(id, x, y, width, height, style, angle);
        //width = DEFAULT_WIDTH;
        //height = DEFAULT_HEIGHT;
    }


    public UMLActivity clone() {
        return new UMLActivity(id + "clone",this.posX + CLONE_OFFSET, this.posY + CLONE_OFFSET, width, height, this.style, this.angle);
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

        traceStyledLine(posX - w2, posY - h2, posX + w2 - h2, posY - h2, canvas);
        traceStyledLine(posX + w2 - h2, posY - h2, posX + w2, posY, canvas);
        traceStyledLine(posX + w2, posY, posX + w2 - h2, posY + h2, canvas);
        traceStyledLine(posX + w2 - h2, posY + h2, posX - w2, posY + h2, canvas);
        traceStyledLine(posX - w2, posY + h2, posX - w2, posY - h2, canvas);
    }

    @Override
    public Path getSelectionPath() {
        int w2 = width/2;
        int h2 = height/2;

        Path p = new Path();

        p.moveTo(posX - w2, posY - h2);
        p.lineTo(posX + w2 - h2, posY - h2);
        p.lineTo(posX + w2, posY);
        p.lineTo(posX + w2 - h2, posY + h2);
        p.lineTo(posX - w2, posY + h2);
        p.lineTo(posX - w2, posY - h2);

        return p;
    }

    public void showEditingDialog(FragmentManager fragmentManager) {
        ImageEditingDialogManager.getInstance().showStyleDialog(fragmentManager, style);
    }

    public String getType() { return TYPE; }
}
