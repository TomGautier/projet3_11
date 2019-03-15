package com.projet3.polypaint.DrawingCollabSession;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.opengl.GLException;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.projet3.polypaint.CanvasElement.GenericShape;
import com.projet3.polypaint.CanvasElement.PaintStyle;
import com.projet3.polypaint.CanvasElement.TextBox;
import com.projet3.polypaint.CanvasElement.UMLActivity;
import com.projet3.polypaint.CanvasElement.UMLArtefact;
import com.projet3.polypaint.CanvasElement.UMLClass;
import com.projet3.polypaint.CanvasElement.UMLRole;
import com.projet3.polypaint.DrawingSession.ImageEditingDialogManager;
import com.projet3.polypaint.DrawingSession.ImageEditingFragment;
import com.projet3.polypaint.R;
import com.projet3.polypaint.SocketManager;
import com.projet3.polypaint.UserLogin.UserManager;

import java.util.ArrayList;
import java.util.Stack;

public class CollabImageEditingFragment extends ImageEditingFragment
        implements ImageEditingDialogManager.ImageEditingDialogSubscriber, DrawingCollabSessionListener {




    @Override
    public void onJoinedSession(String drawingSessionId_) {
        drawingSessionId = drawingSessionId_;
        Toast.makeText(getContext(),"CONNECTE A UNE SESSION", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAddElement(CollabShape shape) {
        boolean isNewForm = (findGenShapeById(shape.getId(), true) == null);
        if (isNewForm){
            shapes.add(createGenShape(shape));
            updateCanvas();
            drawAllShapes();
        }
        Toast.makeText(getContext(),"AJOUTE UN ELEMENT", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDeleteElement(String[] ids) {
        for (int i = 0; i < ids.length; i++) {
            GenericShape shape = findGenShapeById(ids[i], true);
            if (shape != null){
                shapes.remove(shape);
                if (selections.contains(shape))
                    selections.remove(shape);
            }
        }
        updateCanvas();
        drawAllShapes();
        Toast.makeText(getContext(),"DELETE UN ELEMENT", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onModifyElements(CollabShape[] collabShapes) {
        for (int i = 0; i < collabShapes.length; i++) {
            GenericShape shape = findGenShapeById(collabShapes[i].getId(), false);
            if (shape != null){
                GenericShape newGenShape = createGenShape(collabShapes[i]);
                shapes.set(shapes.indexOf(shape),newGenShape);
                selections.set(selections.indexOf(shape),newGenShape);

            }
        }
        updateCanvas();
        drawAllShapes();
        Toast.makeText(getContext(),"MODIFIER UN ELEMENT", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onSelectedElements(String[] oldSelections, String[] newSelections) {
        for (int i = 0; i < oldSelections.length; i ++) {
            GenericShape shape = findGenShapeById(oldSelections[i], false);
            if (shape != null)
                selections.remove(shape);
        }
        for (int j = 0; j < newSelections.length; j++){
            GenericShape shape = findGenShapeById(oldSelections[j], false);
            if (shape != null)
                selections.add(shape);
        }
        updateCanvas();
        drawAllShapes();
        Toast.makeText(getContext(),"SELECTIONNER UN ELEMENT", Toast.LENGTH_LONG).show();

    }
    private GenericShape findGenShapeById(String id, boolean lookInAll){
        if (lookInAll){
            for (int i = 0; i < shapes.size(); i++){
                if (shapes.get(i).getId() == id)
                    return shapes.get(i);
            }
        }
        else{
            for (int i = 0; i < selections.size(); i++){
                if (selections.get(i).getId() == id)
                    return selections.get(i);
            }
        }
        return null;
    }

    private GenericShape createGenShape(CollabShape collabShape){
        GenericShape genShape = null;
        switch(collabShape.getProperties().getType()){
            case "umlClass":
                genShape = new UMLClass(collabShape.getId(),collabShape.getProperties().getMiddlePointCoord()[0],
                        collabShape.getProperties().getMiddlePointCoord()[1], defaultStyle);
                break;
            case "umlArtefact":
                genShape = new UMLArtefact(collabShape.getId(),collabShape.getProperties().getMiddlePointCoord()[0],
                        collabShape.getProperties().getMiddlePointCoord()[1], defaultStyle);
                break;
            case "umlActivity":
                genShape = new UMLActivity(collabShape.getId(),collabShape.getProperties().getMiddlePointCoord()[0],
                        collabShape.getProperties().getMiddlePointCoord()[1], defaultStyle);
                break;
            case "umlRole":
                genShape = new UMLRole(collabShape.getId(),collabShape.getProperties().getMiddlePointCoord()[0],
                        collabShape.getProperties().getMiddlePointCoord()[1], defaultStyle);
                break;
        }
        return genShape;
    }




    private View rootView;
    private String drawingSessionId;


    public CollabImageEditingFragment()  {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater,container,savedInstanceState);
        SocketManager.currentInstance.setupDrawingCollabSessionListener(this);
        SocketManager.currentInstance.joinCollabSession("MockSessionID");
        return rootView;
    }

    @Override
    public void onStop() {
        ImageEditingDialogManager.getInstance().unsubscribe(this);
        super.onStop();
    }


    @Override
    protected void checkSelection(int x, int y) {
        String[] oldIds = new String[selections.size()];
        for (int i = 0; i < oldIds.length; i ++ )
            oldIds[i] = selections.get(i).getId();

        super.checkSelection(x,y);
        if (selections.size() > 0){
            String[] newId = new String[1];
            newId[0] = selections.get(0).getId();
            SocketManager.currentInstance.selectElements(oldIds, newId); //dans le onSelectedElement, ajoute les selections au selectedcollabShapes
        }

    }


    @Override
    protected void checkLassoSelection() {
        String[] oldIds = new String[selections.size()];
        for (int i = 0; i < oldIds.length; i ++ )
            oldIds[i] = selections.get(i).getId();

        super.checkLassoSelection();
        if (selections.size() > 0){
            String[] newIds = new String[selections.size()];
            for (int j = 0; j <  newIds.length; j++){
                newIds[j] = selections.get(j).getId();
            }
            SocketManager.currentInstance.selectElements(oldIds, newIds);
        }
    }

    @Override
    protected GenericShape addShape(int posX, int posY) {
        GenericShape genShape = super.addShape(posX,posY);
        SocketManager.currentInstance.addElement(createCollabShape(genShape));
        return genShape;
    }
    private CollabShape createCollabShape(GenericShape shape){
        String hexColor = String.format("#%06X", (0xFFFFFF & selectionPaint.getColor()));
        CollabShapeProperties properties = new CollabShapeProperties(currentShapeType.toString(), hexColor,
                "#000000", shape.getCenterCoord(), shape.getHeight(),shape.getWidth(),0);
        CollabShape collabShape = new CollabShape(shape.getId(),drawingSessionId, UserManager.currentInstance.getUserUsername(),properties);
        return collabShape;
    }

    @Override
    public void deleteSelection() {
        String[] ids = new String[selections.size()];
        for (int i = 0; i < ids.length; i++){
            ids[i] = selections.get(i).getId();
        }
        SocketManager.currentInstance.deleteElements(ids);
        super.deleteSelection();
    }
    @Override
    protected void moveSelectedShape(MotionEvent event) {
        CollabShape[] selectedCollabShapes = new CollabShape[selections.size()];
        for(int i = 0; i < selections.size(); i++){
            GenericShape shape = selections.get(i);
            selectedCollabShapes[i] = createCollabShape(shape);
        }
        super.moveSelectedShape(event);
        SocketManager.currentInstance.modifyElements(selectedCollabShapes);
    }
    /*
    protected void cutSelection() {
        cutShapes.addAll(selections);
        deleteSelection();
    }
    public void duplicateSelection() {
        ArrayList<GenericShape> duplicatedShapes;
        // Check whether to duplicate selected shapes or clipboard (or nothing)
        if (!selections.isEmpty())
            duplicatedShapes = selections;
        else if (!cutShapes.isEmpty())
            duplicatedShapes = cutShapes;
        else return;

        // Same operation in either case
        ArrayList<GenericShape> stackElems = new ArrayList<>();
        for (GenericShape shape : duplicatedShapes){
            GenericShape nShape = shape.clone();
            shapes.add(nShape);
            stackElems.add(nShape);
        }

        if (selections.isEmpty()) {
            cutShapes = stackElems;
        }
        selections.clear();
        selections.addAll(stackElems);

        addToStack(stackElems, ADD_ACTION);
        updateCanvas();
        drawAllShapes();
        iView.invalidate();
    }
    public void reset() {

        if (shapes != null && shapes.size() > 0){
            addToStack(new ArrayList(shapes),REMOVE_ACTION);
            shapes.clear();
        }
        selections.clear();
        updateCanvas();
        drawAllShapes();
        iView.invalidate();
    }

    public void backCanevas() {
        if (addStack != null && !addStack.empty()){
            Pair pair = addStack.pop();
            selections.clear();
            for (GenericShape shape : (ArrayList<GenericShape>)pair.first){
                if (pair.second.equals(ADD_ACTION)){
                        shapes.remove(shape);
                }
                else if(pair.second.equals(REMOVE_ACTION)){
                    shapes.add(shape);

                }

            }
            removeStack.push(pair);
            updateCanvas();
            drawAllShapes();
            iView.invalidate();
        }


    }
    public void forthCanevas() {
        if (removeStack != null && !removeStack.empty()){
            Pair pair = removeStack.pop();
            selections.clear();
            for (GenericShape shape : (ArrayList<GenericShape>)pair.first){
                if (pair.second.equals(ADD_ACTION)){
                    shapes.add(shape);
                    selections.add(shape);
                }
                else if(pair.second.equals(REMOVE_ACTION)){
                    shapes.remove(shape);

                }

            }
            addStack.push(pair);
            updateCanvas();
            drawAllShapes();
            iView.invalidate();
        }

    }

    public boolean checkCanvasResizeHandle(int x, int y) {
        final int cornerTolerance = 10;
        
        return x > canvas.getWidth() - cornerTolerance &&
                x < canvas.getWidth() + cornerTolerance &&
                y > canvas.getHeight() - cornerTolerance &&
                y < canvas.getHeight() + cornerTolerance;
    }
    public boolean resizeCanvas(MotionEvent event) {
        int posX = (int)event.getX(0);
        int posY = (int)event.getY(0);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isResizingCanvas = true;
                break;
            case MotionEvent.ACTION_MOVE:
                ViewGroup.LayoutParams params = iView.getLayoutParams();
                params.height = posY;
                params.width = posX;
                iView.setLayoutParams(params);
                break;
            case MotionEvent.ACTION_UP:
                isResizingCanvas = false;
                break;
        }

        updateCanvas();
        drawAllShapes();

        return isResizingCanvas;
    }

    // ------------------------- Dialogs -------------------------
    // TextEditingDialog
    @Override
    public void onTextEditingDialogPositiveClick(String contents) {
        ((TextBox)selections.get(0)).setText(contents);
        updateCanvas();
        drawAllShapes();
        iView.invalidate();
    }
    @Override
    public void onTextEditingDialogNegativeClick() {
        if (((TextBox)selections.get(0)).getText().equals("")) {
            shapes.removeAll(selections);
            selections.clear();
            updateCanvas();
            drawAllShapes();
            iView.invalidate();
        }
    }*/
}
