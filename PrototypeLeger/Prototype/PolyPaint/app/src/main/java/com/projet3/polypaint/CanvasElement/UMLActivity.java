package com.projet3.polypaint.CanvasElement;

import android.graphics.Canvas;
import android.graphics.Paint;

public class UMLActivity extends GenericShape {
    private final int DEFAULT_WIDTH = 90;
    private final int DEFAULT_HEIGHT = 60;

    public UMLActivity(int x, int y, Paint paint) {
        super(x, y, 0, 0, paint);
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
    }

    @Override
    public void drawOnCanvas(Canvas canvas) {
        int w2 = width/2;
        int h2 = height/2;
        canvas.drawLines(new float[]{posX - w2,      posY - h2, posX + w2 - h2, posY - h2,
                                     posX + w2 - h2, posY - h2, posX + w2,      posY,
                                     posX + w2,      posY,      posX + w2 - h2, posY + h2,
                                     posX + w2 - h2, posY + h2, posX - w2,      posY + h2,
                                     posX - w2,      posY + h2, posX - w2,      posY - h2},
                         paint);
    }
}
