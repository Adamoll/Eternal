package com.eternalsrv.models;

import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;

/**
 * Created by Adam on 30-09-2017.
 */

public class ChatDialog extends QBChatDialog {
    private ArrayList<QBChatMessage> messages;

    public ArrayList<QBChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<QBChatMessage> messages) {
        this.messages = messages;
    }
}
