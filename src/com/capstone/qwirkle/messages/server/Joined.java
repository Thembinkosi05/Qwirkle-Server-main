package com.capstone.qwirkle.messages.server;

import com.capstone.qwirkle.messages.Message;
import com.capstone.qwirkle.models.Player;

import java.util.List;

public class Joined extends Message {
    private static final long serialVersionUID = 104L;
    public String lobbyID;
    public List<Player> players;
    public String name;


    public Joined(String lobbyID, List<Player> players, String name) {
        this.name = name;
        this.lobbyID = lobbyID;
        this.players = players;
    }

    @Override
    public String toString() {
        return String.format("%s has joined lobby => %s)",name,lobbyID);
    }
}
