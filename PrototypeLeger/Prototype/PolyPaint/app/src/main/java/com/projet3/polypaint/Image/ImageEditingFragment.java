package com.projet3.polypaint.Image;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.support.constraint.solver.widgets.ConstraintHorizontalLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.os.Bundle;
import android.text.Layout;
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
import com.projet3.polypaint.CanvasElement.UMLActivity;
import com.projet3.polypaint.CanvasElement.UMLArtefact;
import com.projet3.polypaint.CanvasElement.UMLClass;
import com.projet3.polypaint.CanvasElement.UMLRole;
import com.projet3.polypaint.R;

import java.util.ArrayList;
import java.util.Stack;

public class ImageEditingFragment extends Fragment {


    private Button buttonClass;
    private Button buttonRole;
    private Button buttonActivity;
    private Button buttonArtefact;
    private Button buttonCanvas;
    private Button buttonMove;
    private Button buttonSelection;
    private Button buttonLasso;
    private Button buttonReset;
    private Button buttonCut;
    private Button buttonDuplicate;
    private Button buttonDelete;
    private ImageButton buttonRestore;
    private ImageButton buttonBack;

    private enum Mode{selection, lasso, creation, move}
    private enum ShapeType{uml_class, uml_activity, uml_artefact, uml_role}

    private final float DEFAULT_STROKE_WIDTH = 2f;
    private final float SELECTION_STROKE_WIDTH = 4f;
    private final String ADD_ACTION = "ADD";
    private final String REMOVE_ACTION = "REMOVE";

    private Canvas canvas;
    private PaintStyle defaultStyle;
    private Bitmap bitmap;
    private ImageView iView;
    private LinearLayout canvasBGLayout;
    private ArrayList<GenericShape> shapes;
    private ArrayList<GenericShape> cutShapes;
    private Stack<Pair<ArrayList<GenericShape>, String>> addStack;
    private Stack<Pair<ArrayList<GenericShape>, String>> removeStack;

    private Mode currentMode = Mode.creation;
    private ShapeType currentShapeType = ShapeType.uml_class;

    private Paint selectionPaint;
    private ArrayList<GenericShape> selections = null;
    private Path selectionPath = new Path();private boolean isMovingSelection = false;
    private int lastTouchPosX;
    private int lastTouchPosY;

    private View rootView;

    private boolean isResizingCanvas = false;


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

        initializeButtons();
        initializePaint();

        setTouchListener();
        return rootView;
    }

    private void initializeButtons(){
        //forms
        buttonActivity = (Button)rootView.findViewById(R.id.buttonActivity);
        buttonActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShapeType(ShapeType.uml_activity);
            }
        });

        buttonArtefact = (Button)rootView.findViewById(R.id.buttonArtefact);
        buttonArtefact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShapeType(ShapeType.uml_artefact);
            }
        });

        buttonClass = (Button)rootView.findViewById(R.id.buttonClass);
        buttonClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShapeType(ShapeType.uml_class);
            }
        });

        buttonRole = (Button)rootView.findViewById(R.id.buttonRole);
        buttonRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShapeType(ShapeType.uml_role);
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

    private void initializePaint() {
        int borderColor = ResourcesCompat.getColor(getResources(), R.color.shape, null);
        Paint borderPaint = new Paint();
        borderPaint.setColor(borderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
        borderPaint.setStrokeCap(Paint.Cap.ROUND);
        borderPaint.setAntiAlias(true);

        int backgroundColor = ResourcesCompat.getColor(getResources(), R.color.shapeFillTest, null);
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.FILL);

        defaultStyle = new PaintStyle(borderPaint, backgroundPaint);

        int selectionColor = ResourcesCompat.getColor(getResources(), R.color.shapeSelection, null);
        selectionPaint = new Paint();
        selectionPaint.setColor(selectionColor);
        selectionPaint.setStyle(Paint.Style.STROKE);
        selectionPaint.setStrokeWidth(SELECTION_STROKE_WIDTH);
        selectionPaint.setStrokeCap(Paint.Cap.ROUND);
        selectionPaint.setAntiAlias(true);

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchListener() {
        iView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                updateCanvas();

                boolean continueListening = false;

                int posX = (int)event.getX(0);
                int posY = (int)event.getY(0);

                if (isResizingCanvas || checkCanvasResizeHandle(posX, posY)) {
                    return resizeCanvas(event);
                }

                switch (currentMode) {
                    case selection :
                        checkSelection(posX, posY);
                        break;
                    case lasso :
                        doLassoSelection(event);
                        continueListening = true;
                        break;
                    case creation :
                        ArrayList stackElems = new ArrayList();
                        stackElems.add(addShape(posX, posY));
                        addToStack(stackElems, ADD_ACTION);
                        break;
                    case move :
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

    private void checkSelection(int x, int y) {
        selections.clear();

        for (int i = shapes.size() - 1; i >= 0; i--) {
            if (shapes.get(i).getBoundingBox().contains(x, y)){
                selections.add(shapes.get(i));
                return;
            }
        }

        selections.clear();
    }

    private void doLassoSelection(MotionEvent event) {
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

    private void checkLassoSelection() {
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

    private GenericShape addShape(int posX, int posY) {
        selections.clear();
        GenericShape nShape = null;
        switch (currentShapeType) {
            case uml_class :
                nShape = new UMLClass(posX, posY, defaultStyle);
                break;
            case uml_activity :
                nShape = new UMLActivity(posX, posY, defaultStyle);
                break;
            case uml_artefact :
                nShape = new UMLArtefact(posX, posY, defaultStyle);
                break;
            case uml_role :
                nShape = new UMLRole(posX, posY, defaultStyle);
                break;
        }
        if (nShape != null)
            shapes.add(nShape);

        return nShape;

    }
    private void addToStack(ArrayList<GenericShape> nShapes, String action){
        Pair pair = new Pair(nShapes, action);
        addStack.push(pair);
    }

    private void drawAllShapes() {
        for(GenericShape shape : shapes)
            shape.drawOnCanvas(canvas);

        if (selections.size() > 0)
            for (GenericShape shape : selections)
                shape.drawSelectionBox(canvas, selectionPaint);
    }

    private void updateCanvas() {
        bitmap = Bitmap.createBitmap(iView.getWidth(), iView.getHeight(), Bitmap.Config.ARGB_8888);
        iView.setImageBitmap(bitmap);
        canvas = new Canvas(bitmap);
    }

    private void setShapeType(ShapeType type) {
        currentShapeType = type;
        currentMode = Mode.creation;
    }

    private void setMode(Mode mode) {
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
    private void moveSelectedShape(MotionEvent event) {
        int posX = (int)event.getX(0);
        int posY = (int)event.getY(0);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (GenericShape shape : selections)
                    if (shape.getBoundingBox().contains(posX, posY)) {
                        isMovingSelection = true;
                        lastTouchPosX = posX;
                        lastTouchPosY = posY;
                    }
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
    private void cutSelection() {
        cutShapes.addAll(selections);
        deleteSelection();
    }
    public void duplicateSelection() {
        if (!selections.isEmpty()){
            ArrayList<GenericShape> stackElems = new ArrayList<>();
            for (GenericShape shape : selections){
                GenericShape nShape = shape.clone();
                shapes.add(nShape);
                stackElems.add(nShape);
            }
            selections.clear();
            selections.addAll(stackElems);

            addToStack(stackElems, ADD_ACTION);
            updateCanvas();
            drawAllShapes();
            iView.invalidate();
        }
        else if (!cutShapes.isEmpty()) {
            shapes.addAll(cutShapes);
            cutShapes.clear();

            updateCanvas();
            drawAllShapes();
            iView.invalidate();
        }

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
}
