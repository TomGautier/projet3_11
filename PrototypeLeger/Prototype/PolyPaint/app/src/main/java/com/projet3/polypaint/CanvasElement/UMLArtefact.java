package com.projet3.polypaint.CanvasElement;

import android.app.FragmentManager;
import android.graphics.Canvas;
import android.graphics.Path;

import java.util.regex.Matcher;
import com.projet3.polypaint.DrawingSession.ImageEditingDialogManager;

public class UMLArtefact extends GenericShape {
    protected final static int DEFAULT_WIDTH = 60;
    protected final static int DEFAULT_HEIGHT = 80;
    public static final String TYPE = "UmlArtefact";
    public UMLArtefact(String id, int x, int y, int width, int height, PaintStyle style, float angle) {
        super(id,x, y, width, height, style, angle);
        //width = DEFAULT_WIDTH;
        //height = DEFAULT_HEIGHT;
    }


    public UMLArtefact clone() {
        return new UMLArtefact(id + "clone",this.posX + CLONE_OFFSET, this.posY + CLONE_OFFSET,width, height, this.style, angle);
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

        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.rotate(angle,posX,posY);
        canvas.drawPath(p, style.getBackgroundPaint());

        traceStyledLine(posX - w2, posY - h2, posX + w4, posY - h2, canvas);
        traceStyledLine(posX + w4, posY - h2, posX + w2, posY - h4, canvas);
        traceStyledLine(posX + w2, posY - h4, posX + w2, posY + h2, canvas);
        traceStyledLine(posX + w2, posY + h2, posX - w2, posY + h2, canvas);
        traceStyledLine(posX - w2, posY + h2, posX - w2, posY - h2, canvas);
        traceStyledLine(posX + w2, posY - h4, posX + w4, posY - h4, canvas);
        traceStyledLine(posX + w4, posY - h4, posX + w4, posY - h2, canvas);
        canvas.restore();
    }

    @Override
    public Path getSelectionPath() {
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

        return p;

    }

    public void showEditingDialog(FragmentManager fragmentManager) {
        ImageEditingDialogManager.getInstance().showStyleDialog(fragmentManager, style);
    }

    public String getType() { return TYPE; }
}
