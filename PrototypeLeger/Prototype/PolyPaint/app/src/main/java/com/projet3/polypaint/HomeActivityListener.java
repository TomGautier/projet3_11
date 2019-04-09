package com.projet3.polypaint;

import com.projet3.polypaint.DrawingCollabSession.CollabShape;

public interface HomeActivityListener {

   void onInviteToDrawingSession(String from, String imageId);
   void onResponseToDrawingSessionInvitation(String username, String imageId, boolean response);

}
