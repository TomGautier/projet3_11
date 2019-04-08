package com.projet3.polypaint.Chat;

public interface ChatListener {

    void onNewMessage(String message);
    void onInviteToConversation(String from, String conversation);
    void onResponseToConversationInvitation(String username, String conversation, boolean response);
}
