package com.capstone.qwirkle.messages.server;

import com.capstone.qwirkle.messages.Message;

public class Joined extends Message {
    private static final long serialVersionUID = 104L;
    private String player_username;
    public Joined(String player_username)
    {
        this.player_username = player_username;
    }
    @Override
    public String toString() {
        return String.format("%s is online",player_username);
    }
}
