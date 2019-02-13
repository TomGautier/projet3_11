package com.projet3.polypaint.CanvasElement;

import android.graphics.Canvas;
import android.graphics.Paint;

public class UMLArtefact extends GenericShape {
    private final int DEFAULT_WIDTH = 60;
    private final int DEFAULT_HEIGHT = 80;

    public UMLArtefact(int x, int y, Paint paint) {
        super(x, y, 0, 0, paint);
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
    }

    @Override
    public void drawOnCanvas(Canvas canvas) {
        int w2 = width/2;
        int h2 = height/2;
        int w4 = width/4;
        int h4 = height/4;

        canvas.drawLines(new float[]{posX - w2, posY - h2, posX + w4, posY - h2,
                                     posX + w4, posY - h2, posX + w2, posY - h4,
                                     posX + w4, posY - h2, posX + w4, posY - h4,
                                     posX + w4, posY - h4, posX + w2, posY - h4,
                                     posX + w2, posY - h4, posX + w2, posY + h2,
                                     posX + w2, posY + h2, posX - w2, posY + h2,
                                     posX - w2, posY + h2, posX - w2, posY - h2},
                         paint);
    }
}
