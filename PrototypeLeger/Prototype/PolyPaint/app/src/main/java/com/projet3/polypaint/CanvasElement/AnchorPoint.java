package com.projet3.polypaint.CanvasElement;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class AnchorPoint {
    protected final int ANCHOR_POINT_RADIUS =  15;
    private Rect box;
    private Paint paint;
    private boolean isConnected;
    private String direction;
    private GenericShape shape;

    public AnchorPoint(String direction, GenericShape shape){
        this.direction = direction;
        this.shape = shape;
        initialize();
    }
    private void initialize(){
        isConnected = false;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4.5f);
        setBox();
    }
    public boolean isConnected(){
        return isConnected;
    }
    public void setConnectedState(boolean state){
        isConnected = state;
    }
    public void setBox(){
        switch (direction){
            case "left":
            box = new Rect(shape.posX - shape.width/2 - ANCHOR_POINT_RADIUS,shape.posY - ANCHOR_POINT_RADIUS,
                    shape.posX - shape.width/2, shape.posY + ANCHOR_POINT_RADIUS );
            break;
            case "right":
            box = new Rect(shape.posX + shape.width/2,shape.posY - ANCHOR_POINT_RADIUS,
                    shape.posX + shape.width/2 + ANCHOR_POINT_RADIUS, shape.posY + ANCHOR_POINT_RADIUS );
            break;
            case "top":
            box = new Rect(shape.posX - ANCHOR_POINT_RADIUS,shape.posY - shape.height/2 - ANCHOR_POINT_RADIUS,
                    shape.posX + ANCHOR_POINT_RADIUS, shape.posY - shape.height/2 );
            break;
            case "bottom":
            box = new Rect(shape.posX - ANCHOR_POINT_RADIUS,shape.posY + shape.height/2,
                    shape.posX + ANCHOR_POINT_RADIUS, shape.posY + shape.height/2 + ANCHOR_POINT_RADIUS );
        }
    }
    public boolean contains(int x, int y){
        return box.contains(x,y);
    }
    public void drawOnCanvas(Canvas canvas){
        canvas.drawRect(box,paint);
    }
}
