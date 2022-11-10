package com.capstone.qwirkle.messages.server;

import com.capstone.qwirkle.GameController;
import com.capstone.qwirkle.messages.Message;
import com.capstone.qwirkle.models.Player;

import java.util.List;

public class GameStarted extends Message {
    private static final long serialVersionUID = 103L;

   // public GameController gameController;
    public Object gameController;
    public GameStarted(Object gameController) {
        this.gameController = gameController;
    }

    @Override
    public String toString() {
        return String.format("game started for gameID: %s",gameController);
    }
}
