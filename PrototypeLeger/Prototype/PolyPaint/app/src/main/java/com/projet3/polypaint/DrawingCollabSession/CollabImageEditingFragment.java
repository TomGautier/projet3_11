package com.projet3.polypaint.DrawingCollabSession;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.projet3.polypaint.CanvasElement.GenericShape;
import com.projet3.polypaint.CanvasElement.UMLActivity;
import com.projet3.polypaint.CanvasElement.UMLArtefact;
import com.projet3.polypaint.CanvasElement.UMLClass;
import com.projet3.polypaint.CanvasElement.UMLRole;
import com.projet3.polypaint.DrawingSession.ImageEditingDialogManager;
import com.projet3.polypaint.DrawingSession.ImageEditingFragment;
import com.projet3.polypaint.Network.SocketManager;
import com.projet3.polypaint.UserLogin.UserManager;

import java.util.ArrayList;

public class CollabImageEditingFragment extends ImageEditingFragment
        implements ImageEditingDialogManager.ImageEditingDialogSubscriber, DrawingCollabSessionListener {

    private View rootView;
    private String drawingSessionId;
    private ArrayList<Player> players;
    private Player client;
    private int selectedColorCpt;

    public CollabImageEditingFragment()  {}

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater,container,savedInstanceState);
        players = new ArrayList<>();
        selectedColorCpt = 0;
        client = new Player(UserManager.currentInstance.getUserUsername(), selectedColorCpt);
        //selectedColorCpt++;
        SocketManager.currentInstance.setupDrawingCollabSessionListener(this);
        SocketManager.currentInstance.joinCollabSession("MockSessionID");
        return rootView;
    }
    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void setTouchListener() {
        iView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                updateCanvas();

                boolean continueListening = false;

                int posX = (int)event.getX(0);
                int posY = (int)event.getY(0);

                // Check if an showEditingDialog button was clicked
               // if (event.getAction() != MotionEvent.ACTION_MOVE &&
                        //!selections.isEmpty() && checkEditButton(posX, posY)) { /*Do nothing*/ }
                // Check if canvas is being resized
               // else if (isResizingCanvas || checkCanvasResizeHandle(posX, posY))
                    //return resizeCanvas(event);
                /*else*/ switch (currentMode) {
                        case selection:
                            checkSelection(posX, posY);
                            break;
                        case lasso:
                            doLassoSelection(event);
                            continueListening = true;
                            break;
                        case creation:
                            //ArrayList stackElems = new ArrayList();
                            //stackElems.add(addShape(posX, posY));
                            //addToStack(stackElems, ADD_ACTION);
                            SocketManager.currentInstance.addElement(createCollabShape(posX,posY));
                            break;
                        case move:
                            moveSelectedShape(event);
                            continueListening = true;
                            break;
                    }

                drawAllShapes();
                view.invalidate();
                return continueListening;
            }
        });

        canvasBGLayout.setOnTouchListener(new LinearLayout.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int posX = (int)event.getX(0);
                int posY = (int)event.getY(0);

                if (isResizingCanvas || checkCanvasResizeHandle(posX, posY)) {
                    return resizeCanvas(event);
                }

                return false;
            }
        });
    }
    @Override
    public void onStop() {
        ImageEditingDialogManager.getInstance().unsubscribe(this);
        super.onStop();
    }
    private Player findPlayer(String name){
        for (Player player : players){
            if (player.getName().equals(name))
                return player;
        }
        return null;
    }
   /* private Player findPlayer(GenericShape selectedShape){
        for (Player player : players){
            for (GenericShape shape : player.getSelectedShapes()){
                if (shape.getId().equals(selectedShape.getId()))
                    return player;
            }
        }
        return null;
    }*/


    private boolean isFreeToSelect(String id){
        for (GenericShape shape : client.getSelectedShapes()){
            if (shape.getId().equals(id))
                return false;
        }
        for (Player player : players){
            for (GenericShape shape : player.getSelectedShapes()){
                if (shape.getId().equals(id))
                    return false;
            }
        }
        return true;
    }
    private boolean isNewPlayer(String name){
        if (client.getName().equals(name))
            return false;
        else
        {
            for (Player player : players) {
                if (player.getName().equals(name)) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    protected void drawAllShapes() {
        for (GenericShape shape : shapes)
            shape.drawOnCanvas(canvas);

        //if (selections.size() > 0) {
            /*for (GenericShape shape : selections) {
                shape.drawSelectionBox(canvas, findPlayer(shape).getSelectionPaint());
            }*/
        for (GenericShape shape : client.getSelectedShapes())
            shape.drawSelectionBox(canvas, client.getSelectionPaint());

        for (Player player : players) {
            for (GenericShape shape : player.getSelectedShapes())
                shape.drawSelectionBox(canvas, player.getSelectionPaint());
        }
        // }
    }


    @Override
    protected void checkSelection(int x, int y) {
        //String[] oldIds = new String[client.getSelectedShapes().size()];
        ArrayList<String> olds = new ArrayList();
        ArrayList<String> newIds = new ArrayList();
        for (GenericShape shape : client.getSelectedShapes()){
            olds.add(shape.getId());
        }
        for (GenericShape shape : shapes){
            if (shape.getBoundingBox().contains(x, y) && isFreeToSelect(shape.getId())){
                newIds.add(shape.getId());
                break;
            }
        }
        SocketManager.currentInstance.selectElements(olds.toArray(new String[olds.size()]), newIds.toArray(new String[newIds.size()]));
    }


    @Override
    protected void checkLassoSelection() {

        ArrayList<String> olds = new ArrayList();
        ArrayList<String> news = new ArrayList();
        for (GenericShape shape : client.getSelectedShapes()){
            olds.add(shape.getId());
        }
        for (GenericShape shape : shapes){
            canvas.clipRect(shape.getBoundingBox(), Region.Op.REPLACE);
            if (!canvas.clipPath(selectionPath, Region.Op.DIFFERENCE) && isFreeToSelect(shape.getId()))
                news.add(shape.getId());
        }
        SocketManager.currentInstance.selectElements(olds.toArray(new String[olds.size()]), news.toArray(new String[news.size()]));
        canvas.clipRect(new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), Region.Op.REPLACE);

    }


    private CollabShape createCollabShape(GenericShape shape){
        String hexColor = String.format("#%06X", (0xFFFFFF & selectionPaint.getColor()));
        CollabShapeProperties properties = new CollabShapeProperties(currentShapeType.toString(), hexColor,
                "#000000", shape.getCenterCoord(), shape.getHeight(),shape.getWidth(),0);
        CollabShape collabShape = new CollabShape(shape.getId(),drawingSessionId, client.getName(),properties);
        return collabShape;
    }
    private CollabShape createCollabShape(int posX, int posY){
        id = client.getName() + Integer.toString(idCpt++);
        String hexColor = String.format("#%06X", (0xFFFFFF & selectionPaint.getColor()));
        CollabShapeProperties properties = new CollabShapeProperties(currentShapeType.toString(), hexColor,
                "#000000", new int[] {posX,posY},GenericShape.getDefaultHeight(currentShapeType.toString())
                ,GenericShape.getDefaultWidth(currentShapeType.toString()),0);
        return new CollabShape(id,drawingSessionId, client.getName(),properties);
    }
    @Override
    protected void reset() {

        /*if (shapes != null && shapes.size() > 0){
            addToStack(new ArrayList(shapes),REMOVE_ACTION);
            shapes.clear();
        }
        selections.clear();
        updateCanvas();
        drawAllShapes();
        iView.invalidate();*/
        ArrayList<String> ids = new ArrayList<>();
        for (GenericShape shape : shapes)
            ids.add(shape.getId());
        SocketManager.currentInstance.deleteElements(ids.toArray(new String[ids.size()]));
    }

    @Override
    protected void stack(){
        if (shapes.size() > 0)
            SocketManager.currentInstance.stackElement(shapes.get(shapes.size()-1).getId());
    }
    @Override
    protected void unStack(){
        if (!stack.empty())
            SocketManager.currentInstance.unstackElement(createCollabShape(stack.peek()));
    }
    @Override
    public void deleteSelection() {
        /*String[] ids = new String[selections.size()];
        for (int i = 0; i < ids.length; i++){
            ids[i] = selections.get(i).getId();
        }*/
        ArrayList<String> ids = new ArrayList<>();
        for (GenericShape shape : client.getSelectedShapes()){
            ids.add(shape.getId());
        }
        SocketManager.currentInstance.deleteElements(ids.toArray(new String[ids.size()]));
    }
    @Override
    public void duplicateSelection() {
        // Check whether to duplicate selected shapes or clipboard (or nothing)
        ArrayList<CollabShape> collabShapes = new ArrayList<>();
        if (!client.getSelectedShapes().isEmpty()){
            for (GenericShape shape : client.getSelectedShapes()){
                collabShapes.add(createCollabShape(shape.clone()));
            }
            SocketManager.currentInstance.duplicateElements(collabShapes.toArray(new CollabShape[collabShapes.size()]));

        }
        else if (!cutShapes.isEmpty()){
            for (GenericShape shape : cutShapes){
                collabShapes.add(createCollabShape(shape));
            }
            SocketManager.currentInstance.duplicateCutElements(collabShapes.toArray(new CollabShape[collabShapes.size()]));
        }
        else return;

        // Same operation in either case
        //ArrayList<GenericShape> stackElems = new ArrayList<>();
        //ArrayList<CollabShape> collabShapes = new ArrayList<>();


       // if (selections.isEmpty()) {
           // cutShapes = stackElems;
        //}
        //selections.clear();
       // selections.addAll(stackElems);

        //addToStack(stackElems, ADD_ACTION);
       // updateCanvas();
        //drawAllShapes();
        //iView.invalidate();
    }
    @Override
    protected void moveSelectedShape(MotionEvent event) {
        /*CollabShape[] selectedCollabShapes = new CollabShape[selections.size()];
        for(int i = 0; i < selections.size(); i++){
            GenericShape shape = selections.get(i);
            selectedCollabShapes[i] = createCollabShape(shape);
        }
        super.moveSelectedShape(event);
        SocketManager.currentInstance.modifyElements(selectedCollabShapes);*/
        int posX = (int)event.getX(0);
        int posY = (int)event.getY(0);
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                for (GenericShape shape : client.getSelectedShapes()){
                    if (shape.getBoundingBox().contains(posX, posY)) {
                        isMovingSelection = true;
                        lastTouchPosX = posX;
                        lastTouchPosY = posY;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isMovingSelection) {
                   // ArrayList<CollabShape> shapes = new ArrayList<>();
                    for (GenericShape shape : client.getSelectedShapes()){
                        shape.relativeMove(posX - lastTouchPosX, posY - lastTouchPosY);
                        //CollabShape collShape = createCollabShape(shape);
                        //collShape.getProperties().setMiddlePointCoord(posX - lastTouchPosX, posY - lastTouchPosY);
                        //shapes.add(collShape);
                    }
                    //SocketManager.currentInstance.modifyElements(shapes.toArray(new CollabShape[shapes.size()]));
                    lastTouchPosX = posX;
                    lastTouchPosY = posY;
                }
                break;
            case MotionEvent.ACTION_UP:
                ArrayList<CollabShape> shapes = new ArrayList<>();
                for (GenericShape shape : client.getSelectedShapes()){
                    shapes.add(createCollabShape(shape));
                }
                SocketManager.currentInstance.modifyElements(shapes.toArray(new CollabShape[shapes.size()]));
                isMovingSelection = false;
                break;
        }
        /*switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (GenericShape shape : selections) {
                    if (shape.getBoundingBox().contains(posX, posY)) {
                        isMovingSelection = true;
                        lastTouchPosX = posX;
                        lastTouchPosY = posY;
                    }
                }
                if (!isMovingSelection)
                    selections.clear();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isMovingSelection) {
                    for (GenericShape shape : selections)
                        shape.relativeMove(posX - lastTouchPosX, posY - lastTouchPosY);

                    lastTouchPosX = posX;
                    lastTouchPosY = posY;
                }
                break;
            case MotionEvent.ACTION_UP:
                isMovingSelection = false;
                break;
        }*/
    }

    //EVENT SERVER
    @Override
    public void onJoinedSession(String drawingSessionId_) {
        drawingSessionId = drawingSessionId_;
        //  Toast.makeText(getContext(),"CONNECTE A UNE SESSION", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onNewUserJoined(String[] players) {
        for (int i =  0; i < players.length; i++){
            if (isNewPlayer(players[i]))
                this.players.add(new Player(players[i],++selectedColorCpt));
        }
    }
    @Override
    public void onAddElement(CollabShape shape, String author) {
        Player player = findPlayer(author);
        player = (player == null) ? client : player;
        GenericShape genShape = createGenShape(shape);
        shapes.add(genShape);
        player.clearSelectedShape();
        player.addSelectedShape(genShape);
        /*if (!client.getName().equals(shape.getAuthor()) && !playerAlreadyRegistered(shape.getAuthor())){
            selectedColorCpt++;
            players.add(new Player(shape.getAuthor(),selectedColorCpt));
        }*/
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //iView.invalidate();
                updateCanvas();
                drawAllShapes();
                rootView.invalidate();

            }
        });
    }

    @Override
    public void onDuplicateElements(CollabShape[] shapes, String author) {
        Player player = findPlayer(author);
        player = (player == null) ? client : player;
        player.clearSelectedShape();
        for (int i =0; i < shapes.length; i++){
            GenericShape shape = createGenShape(shapes[i]);
            player.addSelectedShape(shape);
            this.shapes.add(shape);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateCanvas();
                drawAllShapes();
                rootView.invalidate();
            }
        });
    }

    @Override
    public void onDuplicateCutElements(CollabShape[] shapes, String author) {
        Player player = findPlayer(author);
        player = (player == null) ? client : player;
        for (int i =0; i < shapes.length; i++){
            GenericShape shape = createGenShape(shapes[i]);
            player.addSelectedShape(shape);
            this.shapes.add(shape);
        }
        if (client.getName().equals(author)){
            this.cutShapes.clear();
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateCanvas();
                drawAllShapes();
                rootView.invalidate();
            }
        });
    }


    @Override
    public void onDeleteElement(String[] ids, String author) {
        Player player = findPlayer(author);
        player = (player == null) ? client : player;
        for (int i = 0; i < ids.length; i++){
            GenericShape shape = findGenShapeById(ids[i]);
            player.removeSelectedShape(shape);
            shapes.remove(shape);
        }
        if (shapes.size() == 0){ //DONC UN RESET
            client.clearSelectedShape();
            for (Player playr : players)
                playr.clearSelectedShape();
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateCanvas();
                drawAllShapes();
                rootView.invalidate();
            }
        });
        //  Toast.makeText(getContext(),"DELETE UN ELEMENT", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onCutElements(String[] ids, String author) {
        Player player = findPlayer(author);
        player = (player == null) ? client : player;
        for (int i = 0; i < ids.length; i++){
            GenericShape shape = findGenShapeById(ids[i]);
            player.removeSelectedShape(shape);
            shapes.remove(shape);
            if (client.getName().equals(author))
                cutShapes.add(shape);
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateCanvas();
                drawAllShapes();
                rootView.invalidate();
            }
        });
    }

    @Override
    public void onModifyElements(CollabShape[] collabShapes, String author) {
        Player player = findPlayer(author);
        player = (player == null) ? client : player;
        player.clearSelectedShape();
        for (int i = 0; i < collabShapes.length; i ++){
            GenericShape newShape = createGenShape(collabShapes[i]);
            GenericShape oldShape = findGenShapeById(newShape.getId());
            if (oldShape != null){
                shapes.remove(oldShape);
                shapes.add(newShape);
                player.addSelectedShape(newShape);
            }
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateCanvas();
                drawAllShapes();
                rootView.invalidate();
            }
        });
        /*Player player = findPlayer(author);
        player = (player == null) ? client : player;
        for (int i = 0; i < collabShapes.length; i++){
            GenericShape shape = createGenShape(collabShapes[i]);
            player.removeSelectedShape(shape);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateCanvas();
                drawAllShapes();
                rootView.invalidate();
            }
        });
        //  Toast.makeText(getContext(),"MODIFIER UN ELEMENT", Toast.LENGTH_LONG).show();*/

    }

    @Override
    public void onSelectedElements(String[] oldSelections, String[] newSelections, String author) {
        /*GenericShape shape;
        String owner = "";
        Player player = null;
        for (int i = 0; i < oldSelections.length; i ++) {
            shape = findGenShapeById(oldSelections[i], false);
            if (shape != null){
                player = findPlayer(shape);
                player.removeSelectedShape(shape);
                selections.remove(shape);
            }
        }
        for (int j = 0; j < newSelections.length; j++){
            shape = findGenShapeById(newSelections[j], true);
            if (shape != null && player != null){
                player.addSelectedShape(shape);
                selections.add(shape);
            }
        }*/
        Player player = findPlayer(author);
        player = (player == null) ? client : player;
        for (int i = 0; i < oldSelections.length; i ++){
            player.removeSelectedShape(findGenShapeById(oldSelections[i]));
        }
        for (int j = 0; j < newSelections.length; j ++){
            if (isFreeToSelect(newSelections[j]))
                player.addSelectedShape(findGenShapeById(newSelections[j]));
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateCanvas();
                drawAllShapes();
                rootView.invalidate();
            }
        });
        // Toast.makeText(getContext(),"SELECTIONNER UN ELEMENT", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onStackElement(String id, String author) {
        Player player = findPlayer(author);
        player = (player == null) ? client : player;
        GenericShape shape = findGenShapeById(id);
        shapes.remove(shape);
        player.removeSelectedShape(shape);
        //if (player.getSelectedShapes().contains(shape)){
          //  player.removeSelectedShape(shape);
        //}
        if (client.getName().equals(author)){
            stack.push(shape);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateCanvas();
                drawAllShapes();
                rootView.invalidate();
            }
        });
    }

    @Override
    public void onUnstackElement(CollabShape shape, String author) {
        Player player = findPlayer(author);
        player = (player == null) ? client : player;
        GenericShape genShape = createGenShape(shape);
        shapes.add(genShape);
        player.clearSelectedShape();
        player.addSelectedShape(genShape);
        if (client.getName().equals(author)){
            stack.pop();
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateCanvas();
                drawAllShapes();
                rootView.invalidate();
            }
        });
    }

    /*private boolean shapeAlreadySelected(String id){
        for (GenericShape shape : client.getSelectedShapes()){
            if (shape.getId().equals(id))
                return true;
        }
        for (Player player : players){
            for (GenericShape shape : player.getSelectedShapes()){
                if (shape.getId().equals(id))
                    return true;
            }
        }
        return false;
    }*/
    private GenericShape findGenShapeById(String id) {
        for (int i = 0; i < shapes.size(); i++) {
            if (shapes.get(i).getId().equals(id))
                return shapes.get(i);
        }
        return null;
    }


    private GenericShape createGenShape(CollabShape collabShape){
        GenericShape genShape = null;
        switch(collabShape.getProperties().getType()){
            case "UmlClass":
                genShape = new UMLClass(collabShape.getId(),collabShape.getProperties().getMiddlePointCoord()[0],
                        collabShape.getProperties().getMiddlePointCoord()[1], collabShape.getProperties().getWidth(),
                        collabShape.getProperties().getHeight(), defaultStyle);
                break;
            case "Artefact":
                genShape = new UMLArtefact(collabShape.getId(),collabShape.getProperties().getMiddlePointCoord()[0],
                        collabShape.getProperties().getMiddlePointCoord()[1], collabShape.getProperties().getWidth(),
                        collabShape.getProperties().getHeight(), defaultStyle);
                break;
            case "Activity":
                genShape = new UMLActivity(collabShape.getId(),collabShape.getProperties().getMiddlePointCoord()[0],
                        collabShape.getProperties().getMiddlePointCoord()[1], collabShape.getProperties().getWidth(),
                        collabShape.getProperties().getHeight(), defaultStyle);
                break;
            case "Role":
                genShape = new UMLRole(collabShape.getId(),collabShape.getProperties().getMiddlePointCoord()[0],
                        collabShape.getProperties().getMiddlePointCoord()[1], collabShape.getProperties().getWidth(),
                        collabShape.getProperties().getHeight(), defaultStyle);
                break;
        }
        return genShape;
    }

    protected void cutSelection() {
        if (client.getSelectedShapes().size() > 0){
            ArrayList<String> ids = new ArrayList<>();
            for (GenericShape shape: client.getSelectedShapes()){
                ids.add(shape.getId());
            }
            SocketManager.currentInstance.cutElements(ids.toArray(new String[ids.size()]));
        }
        //cutShapes.addAll(selections);
        //deleteSelection();
    }
    /*
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
