package com.capstone.qwirkle.messages.server;

import com.capstone.qwirkle.messages.Message;

public class SetUsername extends Message {
    private static final long serialVersionUID = 104L;
    public String username;

    public SetUsername(String username) {
        this.username = username;
    }
    @Override
    public String toString() {
        return String.format("username is set", username);}
}
