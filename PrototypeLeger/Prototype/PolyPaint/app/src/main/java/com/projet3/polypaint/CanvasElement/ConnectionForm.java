package com.projet3.polypaint.CanvasElement;

import android.app.FragmentManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import java.util.ArrayList;

public class ConnectionForm extends GenericShape {
    protected final static int DEFAULT_WIDTH = 100;
    protected final static int DEFAULT_HEIGHT = 60;
    protected final static float ARROW_DEFAULT_WIDTH_RATIO = 0.8f;
    protected final static float LINE_DEFAULT_WIDTH_RATIO = 0.2f;


    //private Paint borderPaint;
    //private Paint backgroundPaint;
    //private Paint textPaint;
    private String type;
    private ArrayList<Point> summits;

    public ConnectionForm(String id, int x, int y, int width, int height, PaintStyle style, String type) {
        super(id,x,y,width,height,style);
        this.summits = new ArrayList<>();
        this.type = type;
        //borderPaint = border;
        //backgroundPaint = background;
    }

    //public Paint getBorderPaint() { return borderPaint; }
    //Paint getBackgroundPaint() { return backgroundPaint; }
    //Paint getTextPaint() { return textPaint; }

    @Override
    public void drawOnCanvas(Canvas canvas) {
        int w2 = width/2;
        //int h2 = height/2;
       // int w3 = width/3;
        int h3 = height/3;
        float wLine = width * LINE_DEFAULT_WIDTH_RATIO;
        float wArrow = width * ARROW_DEFAULT_WIDTH_RATIO;
        int hLine = height/3;
        int hArrow = height/3;
        //float hLine = width * LINE_DEFAULT_WIDTH_RATIO;
        //float hArrow = width * ARROW_DEFAULT_WIDTH_RATIO;

        Path p = new Path();
        //trait
        //p.moveTo(posX,posY);
        p.addRect(posX - w2, posY + h3, posX + w2, posY - h3, Path.Direction.CW);
        //p.addRect(posX - wLine/2, posY + hLine/2, posX + wLine/2, posY - hLine/2, Path.Direction.CW);
        canvas.drawPath(p, style.getBackgroundPaint());
        canvas.drawPath(p, style.getBorderPaint());

        //p.moveTo(posX - wLine/2, posY);
       // p.moveTo(posX + wLine/2, posY + h3/2);
       /* p.moveTo(posX + wLine/2, posY);
        p.lineTo(posX + wLine/2, posY + 3/2*h3);
        p.lineTo(posX + wLine/2, posY - 3/2*h3);
        p.moveTo(posX + wLine/2, posY + 3/2*h3);
        p.lineTo(posX + wLine/2 + wArrow, posY - 3/2*h3);
        p.moveTo(posX + wLine/2 + wArrow, posY - 3/2*h3);
        p.lineTo(posX + wLine/2, posY - 3/2*h3);
        p.moveTo(posX + wLine/2, posY + h3);*/

       /* //fleche
        switch(type) {
            case "Agregation":
                break;
            case "Composition" :
                break;
            case "Inheritence":
                p.lineTo(posX , posY + h3);
                p.lineTo(posX + wArrow, posY);
                p.lineTo(posX, posY - (h3/2 + h3));
                p.lineTo(posX,posY + h3);
                break;

        }*/
        //summits.add(new Point((int)(posX - wLine/2), posY));
        //summits.add(new Point((int)(posX + wLine/2 + wArrow), posY));

    }


    @Override
    public GenericShape clone() {
        return null;
    }

    @Override
    public void showEditingDialog(FragmentManager fragmentManager) {

    }

    @Override
    public String getType() {
        return type;
    }
}
