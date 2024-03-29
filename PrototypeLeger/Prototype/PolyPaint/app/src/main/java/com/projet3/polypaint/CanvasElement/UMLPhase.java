package com.projet3.polypaint.CanvasElement;

import android.app.FragmentManager;
import android.graphics.Canvas;
import android.graphics.Path;

import com.projet3.polypaint.DrawingSession.ImageEditingDialogManager;

import java.util.Date;

public class UMLPhase extends GenericTextShape {
    protected final static int DEFAULT_WIDTH = 451;
    protected final static int DEFAULT_HEIGHT = 350;

    public static final String TYPE = "Phase";

    public UMLPhase(String id, int x, int y, int width, int height, PaintStyle style, float angle) {
        super(id, x, y, width, height, style,angle);
        this.width = width;
    }
    public UMLPhase(String id, int x, int y, int width, int height, PaintStyle style, String contents, float angle) {
        super(id, x, y, width, height, style, contents,angle);
        this.width = width;
    }

    @Override
    public void drawOnCanvas(Canvas canvas) {
        int w2 = width/2;
        int h2 = height/2;

        Path p = new Path();

        p.addRect(posX - w2, posY - h2, posX + w2, posY - h2 + FONT_SIZE + 2*PADDING, Path.Direction.CW);

        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.rotate(angle,posX,posY);
        canvas.drawPath(p, style.getBackgroundPaint());
        //canvas.drawPath(p, style.getBorderPaint());
        traceStyledLine(posX - w2, posY - h2, posX + w2, posY - h2, canvas);
        traceStyledLine(posX + w2, posY - h2, posX + w2, posY + h2, canvas);
        traceStyledLine(posX + w2, posY + h2, posX - w2, posY + h2, canvas);
        traceStyledLine(posX - w2, posY + h2, posX - w2, posY - h2, canvas);
        traceStyledLine(posX - w2, posY - h2 + FONT_SIZE + 2*PADDING, posX + w2, posY - h2 + FONT_SIZE + 2*PADDING, canvas);

        canvas.drawText(text, posX, posY - h2 + FONT_SIZE + PADDING, style.getTextPaint());
        canvas.restore();
    }

    public UMLPhase clone() {
        return new UMLPhase(id +"_" + (new Date()).getTime(),this.posX + CLONE_OFFSET, this.posY + CLONE_OFFSET, width, height, this.style, text,angle);
    }

    public void showEditingDialog(FragmentManager fragmentManager) {
        ImageEditingDialogManager.getInstance().showTextAndStyleDialog(fragmentManager, style, text);
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getType() { return TYPE; }

}
