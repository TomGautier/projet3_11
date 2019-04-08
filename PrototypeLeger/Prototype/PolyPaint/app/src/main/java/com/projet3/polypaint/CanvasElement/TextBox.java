package com.projet3.polypaint.CanvasElement;

import android.app.FragmentManager;
import android.graphics.Canvas;
import android.graphics.Path;

import com.projet3.polypaint.DrawingSession.ImageEditingDialogManager;

public class TextBox extends GenericTextShape {

    public static final String TYPE = "Text";
    protected static final int DEFAULT_WIDTH = 50;
    protected  static final int DEFAULT_HEIGHT = 50;

    public TextBox(String id, int x, int y, int width, int height, PaintStyle style, float angle) {
        super(id, x, y, width, height,style,angle);
        isAnimating = false;
    }
    public TextBox(String id, int x, int y, int width, int height, PaintStyle style, String contents, float angle) {
        super(id, x, y, width, height, style, contents, angle);
        isAnimating = false;
    }

    @Override
    public void drawOnCanvas(Canvas canvas) {
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.rotate(angle,posX,posY);
        canvas.drawText(text, posX, posY + height / 2, style.getTextPaint());
        canvas.restore();
    }

    public TextBox clone() {
        return new TextBox(id + "clone",this.posX + CLONE_OFFSET, this.posY + CLONE_OFFSET, width, height, this.style, text, angle);
    }

    public void showEditingDialog(FragmentManager fragmentManager) {
        ImageEditingDialogManager.getInstance().showTextEditingDialog(fragmentManager, style, text);
    }

    public String getType() { return TYPE; }

    @Override
    public void setAnchorPoints() {
        //no anchor points for connectionForms
    }
    @Override
    public void drawAnchorPoints(Canvas canvas){
        //no anchor points
    }
    @Override
    public void rotateAnchorPoints(){
        //no anchor points
    }
}
