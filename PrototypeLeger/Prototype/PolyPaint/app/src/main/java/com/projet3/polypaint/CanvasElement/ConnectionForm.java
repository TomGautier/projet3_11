package com.projet3.polypaint.CanvasElement;

import android.app.FragmentManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Vector;

public class ConnectionForm extends GenericShape {
    protected final static int DEFAULT_WIDTH = 250;
    protected final static int DEFAULT_HEIGHT = 30;
    protected final int ARROW_WIDTH = 30;
    protected final int ARROW_HEIGHT = 50;


    private String type;
    //private ArrayList<ConnectionFormVertex> vertexs;
    //private ArrayList<ConnectionFormVertex> vertices;
    private ConnectionFormVertex first;
    private ConnectionFormVertex selectedVertex;
    private Path path;
    private Path selectPath;
    private Paint linePaint;
    private Paint arrowPaint;
    private Paint selectPaint;
    private Paint modifyPaint;
    private boolean vertexTouched;


    public ConnectionForm(String id, int x, int y, int width, int height, PaintStyle style, String type) {
        super(id, x, y, width, height, style);
        this.type = type;
        initializeVertices();
        initializePaints();
    }
    public ConnectionForm clone() {
        return new ConnectionForm(id + "clone", this.posX + CLONE_OFFSET, this.posY + CLONE_OFFSET, width, height, this.style, type);
    }
    private void initializeVertices() {
        //vertices = new ArrayList<>();
        selectedVertex = null;
        vertexTouched = false;
        //int wLine = width - ARROW_WIDTH;
        Point origin = new Point(posX - width/2, posY);
        Point middle1 = new Point(origin.x + width/3, posY);
        Point middle2 = new Point(middle1.x + width/3, posY);
        Point end = new Point(posX + width/2,posY);
        ConnectionFormVertex last = new ConnectionFormVertex(end, null);
        ConnectionFormVertex middleVertex2  = new ConnectionFormVertex(middle2,last);
        ConnectionFormVertex middleVertex1 = new ConnectionFormVertex(middle1,middleVertex2);
        first = new ConnectionFormVertex(origin,middleVertex1);
        //vertices.add(first);
        //vertices.add(last);
        //originVertex.setNext(endVertex);
        //vertexs.add(originVertex);
        //vertexs.add(endVertex);
        //vertexs.add(endVertex);
    }
    private void initializePaints(){
        linePaint = new Paint();
        // smooths
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.BLACK);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(12.0f);

        arrowPaint = new Paint();
        // smooths
        arrowPaint.setAntiAlias(true);
        arrowPaint.setColor(Color.BLUE);
        arrowPaint.setStyle(Paint.Style.FILL);
        //arrowPaint.setStrokeWidth(2.0f);

        modifyPaint = new Paint();
        // smooths
        modifyPaint.setAntiAlias(true);
        modifyPaint.setColor(Color.YELLOW);
        modifyPaint.setStyle(Paint.Style.STROKE);
        modifyPaint.setStrokeWidth(4.5f);

        selectPaint = new Paint();
        // smooths
        selectPaint.setAntiAlias(true);
        selectPaint.setColor(Color.RED);
        selectPaint.setStyle(Paint.Style.STROKE);
        selectPaint.setStrokeWidth(1.0f);

    }

    public boolean isVertexTouched(){
        return vertexTouched;
    }

    public void resetTouch(){
        vertexTouched = false;
        selectedVertex = null;
    }

    public void tryClipVertex(int x, int y){
        ConnectionFormVertex current = first;
        while (current != null){
            if (current.contains(x, y)) {
                selectedVertex = current;
                vertexTouched = true;
                return;
            }
            current = current.getNext();
        }
        //selectedVertex = null;
    }
    @Override
    public void relativeMove(int x, int y) {
        super.relativeMove(x,y);
        ConnectionFormVertex current = first;
        while (current != null){
            current.relativeMove(x,y);
            current = current.getNext();
        }
    }
    public void relativeSelectedVertexMove(int x, int y) {
        selectedVertex.relativeMove(x,y);
    }

    /*public void updateVertex(int x, int y){
        ConnectionFormVertex nVertex = null;
        if (selectedVertex != null) {
            if (selectedVertex == first) {
                first = new ConnectionFormVertex(new Point(x, y), selectedVertex);
            } else if (selectedVertex.getNext() == null) {
                nVertex = new ConnectionFormVertex(new Point(x, y), null);
                selectedVertex.setNext(nVertex);
            } else {
                nVertex = new ConnectionFormVertex(new Point(x, y), selectedVertex.getNext().clone());
                selectedVertex.setNext(nVertex);
            }
        }
    }*/

    @Override
    public Rect getBoundingBox() {
      return super.getBoundingBox();
    }


    @Override
    public void setAnchorPoints() {
        //no anchor points for connectionForms
    }

    @Override
    public void drawOnCanvas(Canvas canvas) {
        //Path p = new Path();

        path = new Path();
        Path arrow = new Path();
        ConnectionFormVertex firstVertex = first;
        path.moveTo(firstVertex.x(), firstVertex.y());
        ConnectionFormVertex currentVertex = firstVertex;
        while (currentVertex.getNext() != null){
            path.lineTo(currentVertex.getNext().x(),currentVertex.getNext().y());
            currentVertex = currentVertex.getNext();
        }
        //ConnectionFormVertex lastVertex = findLastVertex();
        ConnectionFormVertex lastVertex = currentVertex;
        arrow.moveTo(lastVertex.x() + ARROW_WIDTH, lastVertex.y());
        arrow.lineTo(lastVertex.x() + ARROW_WIDTH, lastVertex.y());
        arrow.lineTo(lastVertex.x(), lastVertex.y() + ARROW_HEIGHT / 2);
        arrow.lineTo(lastVertex.x(), lastVertex.y() - ARROW_HEIGHT / 2);
        canvas.drawPath(path, linePaint);
        canvas.drawPath(arrow, arrowPaint);
    }
    @Override
    public void drawSelectionBox(Canvas canvas, Paint paint) {
        selectPath = new Path();
        ConnectionFormVertex current = first;
        while(current != null){
            selectPath.addCircle(current.x(),current.y(),20, Path.Direction.CW);
            current = current.getNext();
        }
        canvas.drawPath(selectPath,modifyPaint);
    }

    public void showEditingDialog(FragmentManager fragmentManager) {
        /* Do nothing for now*/
        // ImageEditingDialogManager.getInstance().showXYZDialog(fragmentManager);
    }

}
