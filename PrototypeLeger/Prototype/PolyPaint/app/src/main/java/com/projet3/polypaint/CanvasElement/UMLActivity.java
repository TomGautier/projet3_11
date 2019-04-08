package com.projet3.polypaint.CanvasElement;

import android.app.FragmentManager;
import android.graphics.Canvas;
import android.graphics.Path;

import com.projet3.polypaint.DrawingSession.ImageEditingDialogManager;

import java.util.Date;

public class UMLActivity extends GenericShape {
    protected final static int DEFAULT_WIDTH = 147;
    protected final static int DEFAULT_HEIGHT = 79;

    public static final String TYPE = "Activity";

    public UMLActivity(String id, int x, int y, int width, int height, PaintStyle style, float angle) {
        super(id, x, y, width, height, style, angle);
        //width = DEFAULT_WIDTH;
        //height = DEFAULT_HEIGHT;
    }

    public UMLActivity clone() {
        return new UMLActivity(id +"_" + (new Date()).getTime(),this.posX + CLONE_OFFSET, this.posY + CLONE_OFFSET, width, height, this.style, this.angle);
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

        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.rotate(angle,posX,posY);
        canvas.drawPath(p, style.getBackgroundPaint());

        traceStyledLine(posX - w2, posY - h2, posX + w2 - h2, posY - h2, canvas);
        traceStyledLine(posX + w2 - h2, posY - h2, posX + w2, posY, canvas);
        traceStyledLine(posX + w2, posY, posX + w2 - h2, posY + h2, canvas);
        traceStyledLine(posX + w2 - h2, posY + h2, posX - w2, posY + h2, canvas);
        traceStyledLine(posX - w2, posY + h2, posX - w2, posY - h2, canvas);
        canvas.restore();
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
