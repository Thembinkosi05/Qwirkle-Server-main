package com.capstone.qwirkle.messages.client;

import com.capstone.qwirkle.GameController;
import com.capstone.qwirkle.PlayerClient;
import com.capstone.qwirkle.messages.Message;

public class Join extends Message<PlayerClient> {
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return "Join()";
    }

    @Override
    public void applyLogic(PlayerClient playerClient) {
        GameController.addClient(playerClient);
    }
}
