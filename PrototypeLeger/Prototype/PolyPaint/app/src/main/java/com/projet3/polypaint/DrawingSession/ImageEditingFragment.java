package com.projet3.polypaint.DrawingSession;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.projet3.polypaint.CanvasElement.GenericShape;
import com.projet3.polypaint.CanvasElement.PaintStyle;
import com.projet3.polypaint.CanvasElement.TextBox;
import com.projet3.polypaint.CanvasElement.UMLActivity;
import com.projet3.polypaint.CanvasElement.UMLArtefact;
import com.projet3.polypaint.CanvasElement.UMLClass;
import com.projet3.polypaint.CanvasElement.UMLRole;
import com.projet3.polypaint.R;
import com.projet3.polypaint.Network.FetchManager;

import java.util.ArrayList;
import java.util.Stack;

public class ImageEditingFragment extends Fragment implements ImageEditingDialogManager.ImageEditingDialogSubscriber {


    protected Button buttonClass;
    protected Button buttonRole;
    protected Button buttonActivity;
    protected Button buttonArtefact;
    protected Button buttonText;
    protected Button buttonCanvas;
    protected Button buttonMove;
    protected Button buttonSelection;
    protected Button buttonLasso;
    protected Button buttonReset;
    protected Button buttonCut;
    protected Button buttonDuplicate;
    protected Button buttonDelete;
    protected Button buttonStack;
    protected Button buttonUnstack;
    protected ImageButton buttonRestore;
    protected ImageButton buttonBack;

    protected enum Mode{selection, lasso, creation, move}
    protected enum ShapeType{none, UmlClass, Activity, Artefact, Role, text_box}

    protected final float DEFAULT_STROKE_WIDTH = 2f;
    protected final float SELECTION_STROKE_WIDTH = 4f;
    protected final String ADD_ACTION = "ADD";
    protected final String REMOVE_ACTION = "REMOVE";

    protected Canvas canvas;
    protected PaintStyle defaultStyle;
    protected Bitmap bitmap;
    protected ImageView iView;
    protected LinearLayout canvasBGLayout;
    protected ArrayList<GenericShape> shapes;
    protected ArrayList<GenericShape> cutShapes;
    protected Stack<Pair<ArrayList<GenericShape>, String>> addStack;
    protected Stack<Pair<ArrayList<GenericShape>, String>> removeStack;
    protected Stack<GenericShape> stack;

    protected Mode currentMode = Mode.creation;
    protected ShapeType currentShapeType = ShapeType.UmlClass;

    protected Paint selectionPaint;
    protected ArrayList<GenericShape> selections = null;
    protected Path selectionPath = new Path();
    protected boolean isMovingSelection = false;
    protected int lastTouchPosX;
    protected int lastTouchPosY;

    protected View rootView;

    protected boolean isResizingCanvas = false;
    protected boolean isLongPressed = false;
    protected int idCpt;
    protected String id;


    public ImageEditingFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView=inflater.inflate(R.layout.activity_image_editing, container, false);
        iView = (ImageView)rootView.findViewById(R.id.canvasView);
        canvasBGLayout = (LinearLayout)rootView.findViewById(R.id.canvasBackground);
        shapes = new ArrayList<>();
        selections = new ArrayList<>();
        cutShapes = new ArrayList<>();

        addStack = new Stack<>();
        removeStack = new Stack<>();
        stack = new Stack();
        idCpt = 0;
        id = FetchManager.currentInstance.getUserUsername() + idCpt;

        initializeButtons();
        initializePaint();
        ImageEditingDialogManager.getInstance().subscribe(this);

        setTouchListener();
        return rootView;
    }

    @Override
    public void onStop() {
        ImageEditingDialogManager.getInstance().unsubscribe(this);
        super.onStop();
    }

    private void initializeButtons(){
        //forms
        buttonActivity = (Button)rootView.findViewById(R.id.buttonActivity);
        buttonActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShapeType(ShapeType.Activity);
            }
        });

        buttonArtefact = (Button)rootView.findViewById(R.id.buttonArtefact);
        buttonArtefact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShapeType(ShapeType.Artefact);
            }
        });

        buttonClass = (Button)rootView.findViewById(R.id.buttonClass);
        buttonClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShapeType(ShapeType.UmlClass);
            }
        });

        buttonRole = (Button)rootView.findViewById(R.id.buttonRole);
        buttonRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShapeType(ShapeType.Role);
            }
        });

        buttonText = (Button)rootView.findViewById(R.id.buttonTextBox);
        buttonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShapeType(ShapeType.text_box);
            }
        });


        //actions
        buttonDelete = (Button)rootView.findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelection();
            }
        });

        buttonCut = (Button)rootView.findViewById(R.id.buttonCut);
        buttonCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cutSelection();
            }
        });

        buttonDuplicate = (Button)rootView.findViewById(R.id.buttonDuplicate);
        buttonDuplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duplicateSelection();
            }
        });

        buttonStack = (Button)rootView.findViewById(R.id.buttonStack);
        buttonStack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stack();
            }
        });
        buttonUnstack = (Button)rootView.findViewById(R.id.buttonUnstack);
        buttonUnstack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unStack();
            }
        });


        buttonReset = (Button)rootView.findViewById(R.id.buttonReset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        buttonBack = (ImageButton)rootView.findViewById(R.id.backButton);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backCanevas();
            }
        });

        buttonRestore = (ImageButton)rootView.findViewById(R.id.restoreButton);
        buttonRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forthCanevas();
            }
        });

        buttonMove = (Button)rootView.findViewById(R.id.buttonMove);
        buttonMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMode(Mode.move);
            }
        });


        //selections
        buttonLasso = (Button)rootView.findViewById(R.id.buttonLasso);
        buttonLasso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMode(Mode.lasso);
            }
        });

        buttonSelection = (Button)rootView.findViewById(R.id.buttonSelection);
        buttonSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMode(Mode.selection);
            }
        });

    }

    protected void initializePaint() {
        // Border paint
        int borderColor = ResourcesCompat.getColor(getResources(), R.color.shape, null);
        Paint borderPaint = new Paint();
        borderPaint.setColor(borderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
        borderPaint.setStrokeCap(Paint.Cap.ROUND);
        borderPaint.setAntiAlias(true);

        // Background paint
        int backgroundColor = ResourcesCompat.getColor(getResources(), R.color.shapeFillTest, null);
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.FILL);

        // Text paint
        Paint textPaint = new Paint();
        textPaint.setColor(borderColor);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setTextSize(TextBox.FONT_SIZE);
        textPaint.setTypeface(Typeface.MONOSPACE);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);

        defaultStyle = new PaintStyle(borderPaint, backgroundPaint, textPaint);

        // Selection paint
        int selectionColor = ResourcesCompat.getColor(getResources(), R.color.shapeSelection, null);
        selectionPaint = new Paint();
        selectionPaint.setColor(selectionColor);
        selectionPaint.setStyle(Paint.Style.STROKE);
        selectionPaint.setStrokeWidth(SELECTION_STROKE_WIDTH);
        selectionPaint.setStrokeCap(Paint.Cap.ROUND);
        selectionPaint.setAntiAlias(true);

    }

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
                if (event.getAction() != MotionEvent.ACTION_MOVE &&
                    !selections.isEmpty() && checkEditButton(posX, posY)) { /*Do nothing*/ }
                // Check if canvas is being resized
                else if (isResizingCanvas || checkCanvasResizeHandle(posX, posY))
                    return resizeCanvas(event);
                else switch (currentMode) {
                    case selection:
                        checkSelection(posX, posY);
                        break;
                    case lasso:
                        doLassoSelection(event);
                        continueListening = true;
                        break;
                    case creation:
                        ArrayList stackElems = new ArrayList();
                        stackElems.add(addShape(posX, posY));
                        addToStack(stackElems, ADD_ACTION);
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


    protected void stack(){

    }
    protected void unStack(){

    }
    protected void checkSelection(int x, int y) {
        selections.clear();

        for (int i = shapes.size() - 1; i >= 0; i--) {
            if (shapes.get(i).getBoundingBox().contains(x, y)){
                selections.add(shapes.get(i));
                return;
            }
        }
    }
    protected boolean checkEditButton(int x, int y) {
        for (int i = selections.size() - 1; i >= 0; i--) {
            if (selections.get(i).getEditButton().contains(x, y)){
                GenericShape clicked = selections.get(i);
                selections.clear();
                selections.add(clicked);
                clicked.showEditingDialog(getFragmentManager());
                return true;
            }
        }
        return false;
    }

    protected void doLassoSelection(MotionEvent event) {
        int posX = (int)event.getX(0);
        int posY = (int)event.getY(0);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                selectionPath.reset();
                selectionPath.moveTo(posX, posY);
                break;
            case MotionEvent.ACTION_MOVE:
                selectionPath.lineTo(posX, posY);
                Path tempPath = new Path(selectionPath);
                tempPath.close();
                canvas.drawPath(tempPath, selectionPaint);
                break;
            case MotionEvent.ACTION_UP:
                selectionPath.close();
                checkLassoSelection();
                selectionPath.reset();
                break;
        }
    }

    protected void checkLassoSelection() {
        selections.clear();

        for (GenericShape shape : shapes) {
            canvas.clipRect(shape.getBoundingBox(), Region.Op.REPLACE);

            // Check if entire bounding box is contained in selectionPath
            if (!canvas.clipPath(selectionPath, Region.Op.DIFFERENCE))
                selections.add(shape);
        }

        // Reset clip to full canvas
        canvas.clipRect(new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), Region.Op.REPLACE);
    }

    protected GenericShape addShape(int posX, int posY) {
        selections.clear();
        GenericShape nShape = null;
        id = FetchManager.currentInstance.getUserUsername() + Integer.toString(idCpt++);
        switch (currentShapeType) {
            case UmlClass :
                nShape = new UMLClass(id,posX, posY, GenericShape.getDefaultWidth(currentShapeType.toString()),
                        GenericShape.getDefaultHeight(currentShapeType.toString()), defaultStyle);
                break;
            case Activity :
                nShape = new UMLActivity(id, posX, posY, GenericShape.getDefaultWidth(currentShapeType.toString()),
                        GenericShape.getDefaultHeight(currentShapeType.toString()), defaultStyle);
                break;
            case Artefact :
                nShape = new UMLArtefact(id, posX, posY,GenericShape.getDefaultWidth(currentShapeType.toString()),
                        GenericShape.getDefaultHeight(currentShapeType.toString()), defaultStyle);
                break;
            case Role :
                nShape = new UMLRole(id, posX, posY, GenericShape.getDefaultWidth(currentShapeType.toString()),
                        GenericShape.getDefaultHeight(currentShapeType.toString()), defaultStyle);
                break;
            case text_box :
                nShape = new TextBox(Integer.toString(idCpt), posX, posY, defaultStyle);
                ImageEditingDialogManager.getInstance().showTextEditingDialog(getFragmentManager(), "");
                break;
        }
        if (nShape != null) {
            shapes.add(nShape);
            selections.clear();
            selections.add(nShape);
        }

        return nShape;

    }

    protected void addToStack(ArrayList<GenericShape> nShapes, String action){
        Pair pair = new Pair(nShapes, action);
        addStack.push(pair);
    }

    protected void drawAllShapes() {
        for(GenericShape shape : shapes)
            shape.drawOnCanvas(canvas);

        if (selections.size() > 0)
            for (GenericShape shape : selections)
                shape.drawSelectionBox(canvas, selectionPaint);
    }

    protected void updateCanvas() {
        bitmap = Bitmap.createBitmap(iView.getWidth(), iView.getHeight(), Bitmap.Config.ARGB_8888);
        iView.setImageBitmap(bitmap);
        canvas = new Canvas(bitmap);
    }

    protected void setShapeType(ShapeType type) {
        currentShapeType = type;
        currentMode = Mode.creation;
    }

    protected void setMode(Mode mode) {
        currentMode = mode;
    }

    public void deleteSelection() {
        if (selections.size() > 0) {
            ArrayList<GenericShape> stackElems = new ArrayList<>();
            for (GenericShape shape : selections) {
                shapes.remove(shape);
                stackElems.add(shape);
            }
            selections.clear();
            addToStack(stackElems,REMOVE_ACTION);


        }
        updateCanvas();
        drawAllShapes();
        iView.invalidate();
    }
    protected void moveSelectedShape(MotionEvent event) {
        int posX = (int)event.getX(0);
        int posY = (int)event.getY(0);

        switch (event.getAction()) {
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
        }
    }
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
    protected void reset() {

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
    }
}
