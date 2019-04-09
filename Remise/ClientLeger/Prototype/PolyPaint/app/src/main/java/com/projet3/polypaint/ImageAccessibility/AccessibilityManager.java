package com.projet3.polypaint.ImageAccessibility;

import android.app.FragmentManager;

import java.util.ArrayList;

public class AccessibilityManager {

    public interface AccessibilityDialogSubscriber {
        void onAccessibilityPositiveResponse(boolean isPrivate, boolean isProtected, String password);
        void onAccessibilityNegativeResponse();
        void onPasswordPositiveResponse(String password);
        void onPasswordNegativeResponse();
    }

    private static AccessibilityManager instance = null;
    private static ArrayList<AccessibilityDialogSubscriber> subscribers;

    private AccessibilityManager() {
        subscribers = new ArrayList<>();
    }


    public static AccessibilityManager getInstance() {
        if (instance == null) {
            instance = new AccessibilityManager();
        }

        return instance;
    }


    public void subscribe(AccessibilityDialogSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void unsubscribe(AccessibilityDialogSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public void showAccessibilityDialog(FragmentManager fragmentManager) {
        AccessibilityDialog dialog = new AccessibilityDialog();
        dialog.show(fragmentManager, "accessibility");
    }
    public void showPasswordDialog(FragmentManager fragmentManager) {
        ImagePasswordDialog dialog = new ImagePasswordDialog();
        dialog.show(fragmentManager, "password");
    }

    public void onAccessibilityPositiveClick(boolean isPrivate, boolean isProtected, String password) {
        for (AccessibilityDialogSubscriber s : subscribers)
            s.onAccessibilityPositiveResponse(isPrivate, isProtected, password);
    }
    public void onAccessibilityNegativeClick() {
        for (AccessibilityDialogSubscriber s : subscribers)
            s.onAccessibilityNegativeResponse();
    }

    public void onPasswordPositiveClick(String password) {
        for (AccessibilityDialogSubscriber s : subscribers)
            s.onPasswordPositiveResponse(password);
    }
    public void onPasswordNegativeClick() {
        for (AccessibilityDialogSubscriber s : subscribers)
            s.onPasswordNegativeResponse();
    }
}
