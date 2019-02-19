package com.projet3.polypaint.CanvasElement;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class UMLClass extends GenericShape {
    private final int DEFAULT_WIDTH = 200;
    private final int DEFAULT_HEIGHT = 150;

    public UMLClass(int x, int y, PaintStyle style) {
        super(x, y, 0, 0, style);
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
    }
    public UMLClass clone() {
        return new UMLClass(this.posX + 30, this.posY + 30, this.style);
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
}
