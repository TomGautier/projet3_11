package com.projet3.polypaint.CanvasElement;

import android.graphics.Canvas;

import com.projet3.polypaint.DrawingSession.ImageEditingFragment;

import java.util.ArrayList;

public class ShapeAnimator extends Thread {

    private ImageEditingFragment fragment;

    public ShapeAnimator(ImageEditingFragment fragment) {
        super();
        setPriority(MIN_PRIORITY);
        this.fragment= fragment;
    }
    @Override
    public void run() {
        while (true) {
            try {
                sleep(50);
                ArrayList<GenericShape> shapes = fragment.getShapes();

                if (shapes != null) {
                    Canvas c = fragment.getCanvas();
                    for (GenericShape s : shapes) {
                        if (s.isAnimating) s.drawOnCanvas(c);
                    }
                    fragment.invalidateImageView();
                }
            } catch (InterruptedException e) {
                System.out.println("Animator thread interrupted.");
            }
        }
    }

}
