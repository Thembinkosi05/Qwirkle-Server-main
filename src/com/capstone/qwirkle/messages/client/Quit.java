package com.capstone.qwirkle.messages.client;

import com.capstone.qwirkle.GameController;
import com.capstone.qwirkle.PlayerClient;
import com.capstone.qwirkle.messages.Message;

public class Quit extends Message<PlayerClient> {
    private static final long serialVersionUID = 3L;

    @Override
    public void applyLogic(PlayerClient playerClient)
    {
        GameController.leaveGame(playerClient);
    }
    @Override
    public String toString() {
        return super.toString();
    }
}
