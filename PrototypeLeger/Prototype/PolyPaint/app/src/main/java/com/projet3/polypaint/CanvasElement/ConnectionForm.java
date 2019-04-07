package com.projet3.polypaint.CanvasElement;

import android.app.FragmentManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.projet3.polypaint.DrawingCollabSession.Player;
import com.projet3.polypaint.DrawingSession.ImageEditingFragment;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.opengl.Matrix;
import android.util.Pair;
import android.widget.ProgressBar;

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
    public final static float DEFAULT_THICK = 6.0f;
    protected final String TYPE = "ConnectionForm";
    private String type;
    private int arrowHeight;
    private int arrowWidth;

    private ConnectionFormVertex first;
    private ConnectionFormVertex last;
    private ConnectionFormVertex selectedVertex;

    //private Pair<AnchorPoint, String> tailAnchoredShape;
    //private Pair<AnchorPoint, String> frontAnchorShape;
    //private AnchorPoint frontAnchorPoint;
   // private AnchorPoint tailAnchorPoint;
    private int frontanchorPointIndex;
    private String frontShapeId;
    private String tailShapeId;
    private int tailanchorPointIndex;
    private Path path;
    private Path verticesPath;
    private Path arrow;
    private Path frontArrow;

    private String fillingColor;
    private String borderColor;
    private Paint lineFillingPaint;
    private Paint lineBorderPaint;
    private Paint arrowPaint;
    private boolean isResizing;
    private float thick;


    public ConnectionForm(String id, String type, String fillingColor, String borderColor, float thick, Integer[] verticesX, Integer[] verticesY){
        this.id = id;
        this.type = type;
        this.fillingColor = fillingColor;
        this.borderColor = borderColor;
        this.thick = thick;
        initializeProperties();
        initializeVertices(verticesX,verticesY);
    }

    private void initializeProperties(){
        this.frontanchorPointIndex = -1;
        this.frontShapeId = "";
        this.tailanchorPointIndex =  -1;
        this.tailShapeId = "";
        path = new Path();
        selectedVertex = null;
        isResizing = false;

        lineFillingPaint= new Paint();
        lineFillingPaint.setAntiAlias(true);
        lineFillingPaint.setStyle(Paint.Style.FILL);
        //lineFillingPaint.setColor(Color.parseColor(this.fillingColor));
        lineFillingPaint.setColor(Color.RED);
        //lineFillingPaint.setStrokeWidth(this.thick);

        lineBorderPaint = new Paint();
        lineBorderPaint.setAntiAlias(true);
        lineBorderPaint.setStyle(Paint.Style.STROKE);
        lineBorderPaint.setColor(Color.parseColor(this.borderColor));
        lineBorderPaint.setStrokeWidth(this.thick);

        arrowPaint = new Paint();
        arrowPaint.setAntiAlias(true);
        arrowPaint.setStrokeWidth(1.0f);
        switch(type) {
            case "Agregation":
                arrowHeight = 30;
                arrowWidth = 50;
                arrowPaint.setColor(Color.BLACK);
                arrowPaint.setStyle(Paint.Style.STROKE);
                break;
            case "Composition":
                arrowHeight = 30;
                arrowWidth = 50;
                arrowPaint.setColor(Color.BLACK);
                arrowPaint.setStyle(Paint.Style.FILL);
                break;
            case "Inheritance":
                arrowHeight = 50;
                arrowWidth = 30;
                arrowPaint.setColor(Color.BLACK);
                arrowPaint.setStyle(Paint.Style.STROKE);
                break;
            case "Bidirectional":
                arrowHeight = 50;
                arrowWidth = 30;
                arrowPaint.setColor(Color.BLACK);
                arrowPaint.setStyle(Paint.Style.STROKE);
                break;
        }
    }

    private void initializeVertices(Integer[] valuesX, Integer[] valuesY){
        ArrayList<Point> points = new ArrayList();
        //ArrayList<ConnectionFormVertex> vertex;
        for (int i = 0; i < valuesX.length; i ++){
            points.add(new Point(valuesX[i],valuesY[i]));
        }
        last = new ConnectionFormVertex(points.get(points.size()-1),null, this,1);
        ConnectionFormVertex vertex1;
        ConnectionFormVertex vertex2 = last;
        for (int j = points.size() - 2; j > 1; j--) {
            vertex1 = new ConnectionFormVertex(points.get(j),vertex2,this, -1);
            vertex2 = new ConnectionFormVertex(points.get(j-1),vertex1,this, -1);
        }
        first = new ConnectionFormVertex(points.get(0),vertex2, this,0);
    }
    public ConnectionForm clone() {
        return new ConnectionForm(id + "clone", this.type, fillingColor, borderColor, thick, getVerticesXPos(CLONE_OFFSET), getVerticesYPos(CLONE_OFFSET));
    }
    public void setConnection(int anchorPointIndex, String shapeId, int vertexIndex){
        switch(vertexIndex){
            case 0:
                this.tailanchorPointIndex = anchorPointIndex;
                tailShapeId = shapeId;
                break;
            case 1:
                this.frontanchorPointIndex = anchorPointIndex;
                frontShapeId = shapeId;
                break;
        }


    }
    public void clearConnection(){
        tailShapeId = "";
        tailanchorPointIndex = -1;
        frontShapeId = "";
        frontanchorPointIndex = -1;
    }
    public ConnectionFormVertex getFirst(){
        return first;
    }
    public ConnectionFormVertex getLast(){
        return last;
    }

    public void removeConnection(int vertexIndex){
        switch(vertexIndex){
            case 0:
                this.tailanchorPointIndex = -1;
                this.tailShapeId = "";
                break;
            case 1:
                this.frontanchorPointIndex = -1;
                this.frontShapeId = "";
                break;
        }
    }

   public int getTailAnchorPointIndex(){
       return tailanchorPointIndex;
   }
   public String getTailShapeId(){
       return tailShapeId;
   }
   public int getFrontAnchorPointIndex(){
       return frontanchorPointIndex;
   }
   public String getFrontShapeId(){
       return frontShapeId;
   }
    @Override
    public String getFillingColor(){
        return fillingColor;
    }
    @Override
    public String getBorderColor(){
        return "#000000";
    }


    public void finishResize(){
        isResizing = false;
        selectedVertex = null;
    }

    @Override
    public void relativeMove(int x, int y) {
        //super.relativeMove(x,y);
        ConnectionFormVertex current = first;
        while (current != null){
            current.relativeMove(x,y);
            current = current.getNext();
        }
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public String getConnectionType() {
        return type;
    }

    public float getThick(){
        return thick;
    }

    public void relativeVertexMove(int x, int y, int index) {
       switch(index){
           case 0:
               selectedVertex.relativeMove(x,y);
               break;
           case 1:
               first.relativeMove(x,y);
               break;
           case 2:
               last.relativeMove(x,y);
               break;
       }
    }

    public ConnectionFormVertex getSelectedVertex(){
        return selectedVertex;
    }

    @Override
    public Path getSelectionPath() {
        return path;
    }


    public Integer[] getVerticesXPos(int offset){
        ArrayList<Integer> list = new ArrayList<>();
        ConnectionFormVertex current = first;
        while (current != null){
            list.add(current.x());
            current = current.getNext();
        }
        return list.toArray(new Integer[list.size()]);
    }
    public Integer[] getVerticesYPos(int offset){
        ArrayList<Integer> list = new ArrayList<>();
        ConnectionFormVertex current = first;
        while (current != null){
            list.add(current.y());
            current = current.getNext();
        }
        return list.toArray(new Integer[list.size()]);
    }


    @Override
    public boolean contains(int x, int y){
        ConnectionFormVertex current = first;
        while (current != null){
            if (current.contains(x, y)){
                selectedVertex = current;
                return true;
            }
            current = current.getNext();
        }
        return false;
    }
    @Override
    public boolean canResize(int x, int y){
        if (isResizing)
            return true;
        if (contains(x,y)){
            isResizing = true;
            return true;
        }
        return false;
    }


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



    @Override
    public void drawOnCanvas(Canvas canvas) {
        path.reset();
        path.moveTo(first.x(), first.y());
        ConnectionFormVertex currentVertex = first;
        float angle = 0;
        while (currentVertex.getNext() != null){
            if (!(currentVertex == first && type.equals(ImageEditingFragment.ConnectionFormType.Bidirectional.toString()))){
                path.addCircle(currentVertex.x(), currentVertex.y(), 5, Path.Direction.CW);
            }
            path.lineTo(currentVertex.getNext().x(),currentVertex.getNext().y());
            angle = getAngle(currentVertex.getNext().getPoint(), currentVertex.getPoint());
            currentVertex = currentVertex.getNext();
        }
        last = currentVertex;
        canvas.drawPath(path, lineFillingPaint);
        canvas.drawPath(path,lineBorderPaint);
        drawArrow(angle, canvas);
       // canvas.drawPath(arrow, arrowPaint);
    }
   /* public static int[][] generateDefaultPoints(int x, int y){
        int[][] array = new int[4][2];
        int width = GenericShape.getDefaultWidth(ImageEditingFragment.ShapeType.ConnectionForm);
        for (int i =0; i < array.length; i++){
            array[i][0] = (x - width/2) + (width/3)*i;
            array[i][1] = y;
        }
        return array;
    }*/
    public static Integer[] generateDefaultX(int x){
        Integer[] array = new Integer[4];
        int width = GenericShape.getDefaultWidth(ImageEditingFragment.ShapeType.ConnectionForm);
        for (int i =0; i < array.length; i++){
            array[i] = (x - width/2) + (width/3)*i;
            //array[i][1] = y;
        }
        return array;
    }
    public static Integer[] generateDefaultY(int y){
        Integer[] array = new Integer[4];
       // int width = GenericShape.getDefaultWidth(ImageEditingFragment.ShapeType.ConnectionForm);
        for (int i =0; i < array.length; i++){
            //array[i][0] = (x - width/2) + (width/3)*i;
            array[i] = y;
        }
        return array;
    }



    private void drawArrow(float angle, Canvas canvas){
        arrow = new Path();
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        switch (type){
            case "Inheritance":
                canvas.rotate(angle,last.x(),last.y());
                arrow.moveTo(last.x() + arrowWidth/2, last.y()); // Top
                arrow.lineTo(last.x() - arrowWidth/2, last.y() - arrowHeight/2); // Bottom left
                arrow.lineTo(last.x() - arrowWidth/2, last.y() + arrowHeight/2); // Bottom right
                arrow.lineTo(last.x() + arrowWidth/2, last.y()); // Back to Top
                arrow.close();
                break;
            case "Bidirectional":
                //front
                frontArrow = new Path();
                canvas.rotate(getAngle(first.getPoint(),first.getNext().getPoint()),first.x(),first.y());
                frontArrow.moveTo(first.x() + arrowWidth/2, first.y()); // Top
                frontArrow.lineTo(first.x() - arrowWidth/2, first.y() - arrowHeight/2); // Bottom left
                frontArrow.lineTo(first.x() - arrowWidth/2, first.y() + arrowHeight/2); // Bottom right
                frontArrow.lineTo(first.x() + arrowWidth/2, first.y()); // Back to Top
                frontArrow.close();
                canvas.drawPath(frontArrow,arrowPaint);
                //end
                canvas.restore();
                canvas.save(Canvas.MATRIX_SAVE_FLAG);
                canvas.rotate(angle, last.x(), last.y());
                arrow.moveTo(last.x() + arrowWidth/2, last.y()); // Top
                arrow.lineTo(last.x() - arrowWidth/2, last.y() - arrowHeight/2); // Bottom left
                arrow.lineTo(last.x() - arrowWidth/2, last.y() + arrowHeight/2); // Bottom right
                arrow.lineTo(last.x() + arrowWidth/2, last.y()); // Back to Top
                arrow.close();
                break;

            default: //composition et aggregation
                canvas.rotate(angle,last.x(),last.y());
                arrow.moveTo(last.x(), last.y() + arrowHeight/2); // Top
                arrow.lineTo(last.x() - arrowWidth/2, last.y()); // Left
                arrow.lineTo(last.x(), last.y() - arrowHeight/2); // Bottom
                arrow.lineTo(last.x() + arrowWidth/2, last.y()); // Right
                arrow.lineTo(last.x(), last.y() + arrowHeight/2); // Back to Top
                arrow.close();
                break;
        }
        canvas.drawPath(arrow,arrowPaint);
        canvas.restore();
    }
    private float getAngle(Point target, Point source) {
        float angle = (float) Math.toDegrees(Math.atan2(target.y - source.y, target.x - source.x));
        if(angle < 0){
            angle += 360;
        }
        return angle;
    }
    @Override
    public void drawSelectionBox(Canvas canvas, Paint paint) {
        verticesPath = new Path();
        ConnectionFormVertex current = first;
        while(current != null){
            verticesPath.addCircle(current.x(),current.y(),20, Path.Direction.CW);
            current = current.getNext();
        }
        canvas.drawPath(verticesPath, paint);
    }

    public void showEditingDialog(FragmentManager fragmentManager) {
        /* Do nothing for now*/
        // ImageEditingDialogManager.getInstance().showXYZDialog(fragmentManager);
    }

}
