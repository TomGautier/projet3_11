package com.projet3.polypaint.CanvasElement;

import android.app.FragmentManager;
import android.graphics.Canvas;
import android.graphics.Path;

import com.projet3.polypaint.DrawingSession.ImageEditingDialogManager;

public class Comment extends GenericTextShape {
    public static final String TYPE = "Comment";
    protected static final int DEFAULT_WIDTH = 113;
    protected  static final int DEFAULT_HEIGHT = 56;


    public Comment(String id, int x, int y, int width, int height, PaintStyle style, float angle) {
        super(id, x, y,width, height,style, angle);
        this.height += 2*PADDING;
    }
    public Comment(String id, int x, int y,int width, int height, PaintStyle style, String contents, float angle) {
        super(id, x, y,width,height, style, contents, angle);
        this.height += 2*PADDING;
    }

    @Override
    public void drawOnCanvas(Canvas canvas) {
        int w2 = displayWidth/2;
        int h2 = displayHeight/2;

        Path p = new Path();

        p.addRect(posX - w2, posY - h2, posX + w2, posY + h2, Path.Direction.CW);
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.rotate(angle,posX,posY);
        canvas.drawPath(p, style.getBackgroundPaint());
        //canvas.drawPath(p, style.getBorderPaint());
        traceStyledLine(posX - w2, posY - h2, posX + w2, posY - h2, canvas);
        traceStyledLine(posX + w2, posY - h2, posX + w2, posY + h2, canvas);
        traceStyledLine(posX + w2, posY + h2, posX - w2, posY + h2, canvas);
        traceStyledLine(posX - w2, posY + h2, posX - w2, posY - h2, canvas);

        if (!isAnimating) canvas.drawText(text, posX, posY + FONT_SIZE/2, style.getTextPaint());
        canvas.restore();
        animate();
    }

    @Override
    public GenericShape clone() {
        return new Comment(id + "clone", this.posX + CLONE_OFFSET, this.posY + CLONE_OFFSET,width,height, this.style, text, angle);
    }

    @Override
    public void showEditingDialog(FragmentManager fragmentManager) {
            ImageEditingDialogManager.getInstance().showTextAndStyleDialog(fragmentManager, style, text);
    }

    @Override
    protected void adjustWidthToText() {
        super.adjustWidthToText();
        width += 2*PADDING;
    }

    @Override
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
