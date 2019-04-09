package com.projet3.polypaint.CanvasElement;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

public class ConnectionFormVertex {
    protected final int ANCHOR_POINT_RADIUS =  30;
    private Point point;
    private ConnectionFormVertex next;
    private ConnectionForm owner;
    private Rect box;
    private int index;

    public ConnectionFormVertex(Point point, ConnectionFormVertex next, ConnectionForm owner, int index){
        this.point = point;
        this.next = next;
        this.index = index;
        this.owner = owner;
        setBox();
    }
    public Rect getBox(){
        return box;
    }
    public void setBox(){
        box = new Rect(point.x - ANCHOR_POINT_RADIUS - 5, point.y - ANCHOR_POINT_RADIUS - 5,
                point.x + ANCHOR_POINT_RADIUS + 5, point.y + ANCHOR_POINT_RADIUS + 5);
    }
    public boolean contains(int x, int y){
        return box.contains(x,y);
        //return box.contains(x,y);
    }
    public void relativeMove(int x, int y){
        point.x += x;
        point.y += y;
        setBox();
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


    public ConnectionFormVertex clone(){
        return new ConnectionFormVertex(point,next, owner, index);
    }
    public int getIndex(){
        return index;
    }
    public ConnectionForm getOwner(){
        return owner;
    }



}
