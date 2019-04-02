package com.projet3.polypaint.CanvasElement;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

public class AnchorPoint {
    protected final int ANCHOR_POINT_RADIUS =  15;
   // private Rect box;
    private Paint paint;
    private String direction;
    private GenericShape owner;
    private ConnectionFormVertex connectionVertex;
    private Point centerPoint;
    private Path path;
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
        setPath();
    }
    public boolean isConnected(){
        return connectionVertex != null;
    }
    public void setConnection(ConnectionFormVertex vertex){
        this.connectionVertex = vertex;
    }
    public void relativeMove(int x, int y){
        setPath();
        //if (isConnected())
          //  connectionVertex.relativeMove(x,y);
    }
    public void rotate(){
        //path = new Path();
        Matrix matrix = new Matrix();
        matrix.setRotate(owner.angle,owner.posX,owner.posY);
        float[] pts = new float[2];
        pts[0] = centerPoint.x;
        pts[1] = centerPoint.y;
        matrix.mapPoints(pts);
        centerPoint = new Point((int)pts[0],(int)pts[1]);
        path.addCircle(centerPoint.x,centerPoint.y,ANCHOR_POINT_RADIUS, Path.Direction.CW);
        if (isConnected())
            connectionVertex.setPoint(new Point(centerPoint.x, centerPoint.y));
    }
    public void setPath(){
        path = new Path();
        switch (direction){
            case "left":
            /*box = new Rect(owner.posX - owner.width/2 - 2*ANCHOR_POINT_RADIUS,owner.posY - ANCHOR_POINT_RADIUS,
                    owner.posX - owner.width/2, owner.posY + ANCHOR_POINT_RADIUS );*/
            centerPoint = new Point(owner.posX - owner.width/2 - ANCHOR_POINT_RADIUS, owner.posY);
            break;
            case "right":
            /*box = new Rect(owner.posX + owner.width/2,owner.posY - ANCHOR_POINT_RADIUS,
                    owner.posX + owner.width/2 + 2*ANCHOR_POINT_RADIUS, owner.posY + ANCHOR_POINT_RADIUS );*/
            centerPoint = new Point(owner.posX + owner.width/2 + ANCHOR_POINT_RADIUS, owner.posY);
            break;
            case "top":
            /*box = new Rect(owner.posX - ANCHOR_POINT_RADIUS,owner.posY - owner.height/2 - 2*ANCHOR_POINT_RADIUS,
                    owner.posX + ANCHOR_POINT_RADIUS, owner.posY - owner.height/2 );*/
            centerPoint = new Point(owner.posX, owner.posY - owner.height/2 - ANCHOR_POINT_RADIUS);
            break;
            case "bottom":
            /*box = new Rect(owner.posX - ANCHOR_POINT_RADIUS,owner.posY + owner.height/2,
                    owner.posX + ANCHOR_POINT_RADIUS, owner.posY + owner.height/2 + 2*ANCHOR_POINT_RADIUS );*/
            centerPoint = new Point(owner.posX, owner.posY + owner.height/2 + ANCHOR_POINT_RADIUS);
            break;
        }
        rotate();
        //path.addCircle(centerPoint.x,centerPoint.y,ANCHOR_POINT_RADIUS, Path.Direction.CW);
    }


    public boolean stillConnected(){
        return connectionVertex.getBox().contains(centerPoint.x,centerPoint.y);
        //return connectionVertex.getBox().contains(centerPoint.x,centerPoint.y);
        //return Rect.intersects(box,connectionVertex.getBox());
    }
    public boolean intersect(Rect vertexBox){
        //Path temp = new Path();
        //temp.op(path,vertexPath, Path.Op.INTERSECT);
        //return connectionVertex.getBox().contains(centerPoint.x,centerPoint.y);
        //return !temp.isEmpty();
        return vertexBox.contains(centerPoint.x,centerPoint.y);
       // return Rect.intersects(box,vertexBox);
    }
    public boolean contains(int x, int y){
        Path temp = new Path();
        temp.addCircle(x,y,1, Path.Direction.CW);
        temp.op(path, Path.Op.INTERSECT);
        return !temp.isEmpty();
        //return box.contains(x,y);
    }
    public void drawOnCanvas(Canvas canvas){
        //canvas.drawRect(box,paint);
        canvas.drawPath(path,paint);
    }
}
