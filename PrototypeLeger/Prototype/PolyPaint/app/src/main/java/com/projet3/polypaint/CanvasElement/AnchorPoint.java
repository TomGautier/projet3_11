package com.projet3.polypaint.CanvasElement;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class AnchorPoint {
    protected final int ANCHOR_POINT_RADIUS =  15;
    private Rect box;
    private Paint paint;
    private String direction;
    private GenericShape owner;
    private ConnectionFormVertex connectionVertex;
   // private int connectedVertexIndex;


    public AnchorPoint(String direction,GenericShape shape){
        this.direction = direction;
        this.owner = shape;
        initialize();
    }
    private void initialize(){
        connectionVertex = null;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4.5f);
        setBox();
    }
    public boolean isConnected(){
        return connectionVertex != null;
    }
    public void setConnection(ConnectionFormVertex vertex){
        this.connectionVertex = vertex;
    }
    public void relativeMove(int x, int y){
        setBox();
        if (connectionVertex != null)
            connectionVertex.relativeMove(x,y);
    }
    public void setBox(){
        switch (direction){
            case "left":
            box = new Rect(owner.posX - owner.width/2 - 2*ANCHOR_POINT_RADIUS,owner.posY - ANCHOR_POINT_RADIUS,
                    owner.posX - owner.width/2, owner.posY + ANCHOR_POINT_RADIUS );
            break;
            case "right":
            box = new Rect(owner.posX + owner.width/2,owner.posY - ANCHOR_POINT_RADIUS,
                    owner.posX + owner.width/2 + 2*ANCHOR_POINT_RADIUS, owner.posY + ANCHOR_POINT_RADIUS );
            break;
            case "top":
            box = new Rect(owner.posX - ANCHOR_POINT_RADIUS,owner.posY - owner.height/2 - 2*ANCHOR_POINT_RADIUS,
                    owner.posX + ANCHOR_POINT_RADIUS, owner.posY - owner.height/2 );
            break;
            case "bottom":
            box = new Rect(owner.posX - ANCHOR_POINT_RADIUS,owner.posY + owner.height/2,
                    owner.posX + ANCHOR_POINT_RADIUS, owner.posY + owner.height/2 + 2*ANCHOR_POINT_RADIUS );
        }
    }
    public boolean stillConnected(){
        return Rect.intersects(box,connectionVertex.getBox());
    }
    public boolean intersect(Rect vertexBox){
        return Rect.intersects(box,vertexBox);
    }
    public boolean contains(int x, int y){
        return box.contains(x,y);
    }
    public void drawOnCanvas(Canvas canvas){
        canvas.drawRect(box,paint);
    }
}
