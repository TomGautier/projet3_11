package com.projet3.polypaint.Image;

import android.app.DialogFragment;
import android.app.FragmentManager;

import java.util.ArrayList;

public class ImageEditingDialogManager {
    public interface ImageEditingDialogSubscriber {
        void onTextEditingDialogPositiveClick(String contents);
        void onTextEditingDialogNegativeClick();
    }

    private static ImageEditingDialogManager instance = null;
    private static ArrayList<ImageEditingDialogSubscriber> subscribers;

    private ImageEditingDialogManager() {
        subscribers = new ArrayList<>();
    }


    public static ImageEditingDialogManager getInstance() {
        if (instance == null) {
            instance = new ImageEditingDialogManager();
        }

        return instance;
    }


    public void subscribe(ImageEditingDialogSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void unsubscribe(ImageEditingDialogSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public void showTextEditingDialog(FragmentManager fragmentManager, String contents) {
        TextEditingDialog dialog = new TextEditingDialog();
        dialog.setContents(contents);
        dialog.show(fragmentManager, "text editing");
    }

    public void onTextEditingDialogPositiveClick(String contents) {
        for (ImageEditingDialogSubscriber s : subscribers)
            s.onTextEditingDialogPositiveClick(contents);
    }
    public void onTextEditingDialogNegativeClick() {
        for (ImageEditingDialogSubscriber s : subscribers)
            s.onTextEditingDialogNegativeClick();
    }
}
