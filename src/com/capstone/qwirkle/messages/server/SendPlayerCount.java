package com.capstone.qwirkle.messages.server;

import com.capstone.qwirkle.messages.Message;

public class SendPlayerCount extends Message {
    private static final long serialVersionUID = 106L;
    int count,gameID;
    public SendPlayerCount(int count,int gameID){
        this.count = count;
        this.gameID = gameID;
    }

    @Override
    public String toString() {
        return String.format("player count: %s",count);
    }
}
