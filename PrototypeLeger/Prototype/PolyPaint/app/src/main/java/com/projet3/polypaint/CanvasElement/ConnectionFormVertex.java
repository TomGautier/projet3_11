package com.projet3.polypaint.CanvasElement;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

public class ConnectionFormVertex {
    protected final int ANCHOR_POINT_RADIUS =  20;
    private Point point;
    private ConnectionFormVertex next;
    private Path path;
    private Rect box;

    public ConnectionFormVertex(Point point, ConnectionFormVertex next){
        this.point = point;
        this.next = next;
        setBox();
    }
    public Rect getBox(){
        return box;
    }
    public void setBox(){
        box = new Rect(point.x - ANCHOR_POINT_RADIUS - 5, point.y - ANCHOR_POINT_RADIUS - 5,
                point.x + ANCHOR_POINT_RADIUS + 5, point.y + ANCHOR_POINT_RADIUS + 5);
        //path = new Path();
        //path.addCircle(point.x,point.y,ANCHOR_POINT_RADIUS, Path.Direction.CW);
    }
    public boolean contains(int x, int y){
        return box.contains(x,y);
        //return box.contains(x,y);
    }
    public void relativeMove(int x, int y){
        point.x += x;
        point.y += y;
        setBox();
        //box.offset(x,y);
        /*if (next != null){
            next.x += x;
            next.y += y;
        }*/
    }
    public final int x(){
        return point.x;
    }
    public final int y(){
        return point.y;
    }
    public final Point getPoint(){
        return point;
    }
    public void setPoint(Point point){
        this.point = point;
        setBox();
    }

    public final ConnectionFormVertex getNext(){
        return next;
    }

    public void setNext(ConnectionFormVertex next){
        this.next = next;
    }

    public ConnectionFormVertex clone(){
        return new ConnectionFormVertex(point,next);
    }



}
