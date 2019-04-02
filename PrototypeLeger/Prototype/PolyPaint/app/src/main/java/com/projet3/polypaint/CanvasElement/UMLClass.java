package com.projet3.polypaint.CanvasElement;

import android.app.FragmentManager;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;

public class UMLClass extends GenericShape {
    protected final static int DEFAULT_WIDTH = 180;
    protected final static int DEFAULT_HEIGHT = 150;

    public UMLClass(String id, int x, int y, int width, int height, PaintStyle style, float angle) {
        super(id, x, y, width, height, style,angle);
        //width = DEFAULT_WIDTH;
        //height = DEFAULT_HEIGHT;
    }



    public UMLClass clone() {
        return new UMLClass(id + "clone", this.posX + CLONE_OFFSET, this.posY + CLONE_OFFSET, width, height, this.style, angle);
    }

    @Override
    public void drawOnCanvas(Canvas canvas) {
        int w2 = width/2;
        int h2 = height/2;

        Path p = new Path();

        p.addRect(posX - w2, posY - h2, posX + w2, posY + h2, Path.Direction.CW);

        //for (AnchorPoint anchorPoint : anchorPoints)
          //  anchorPoint.drawOnCanvas(canvas);
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.rotate(angle,posX,posY);
        canvas.drawPath(p, style.getBackgroundPaint());
        canvas.drawPath(p, style.getBorderPaint());
        canvas.restore();
    }

    public void showEditingDialog(FragmentManager fragmentManager) {
        /* Do nothing for now*/
        // ImageEditingDialogManager.getInstance().showXYZDialog(fragmentManager);
    }
}
