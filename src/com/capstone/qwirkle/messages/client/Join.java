package com.capstone.qwirkle.messages.client;

import com.capstone.qwirkle.GameController;
import com.capstone.qwirkle.PlayerClient;
import com.capstone.qwirkle.messages.Message;
import com.capstone.qwirkle.messages.server.Joined;

public class Join extends Message<PlayerClient> {
    private static final long serialVersionUID = 1L;
    String username;

    public Join(String username){
        this.username =username;
    }
    @Override
    public String toString() {
        return "Join()";
    }

    @Override
    public void applyLogic(PlayerClient playerClient) {
       //GameController.addClient(playerClient);

        playerClient.handle = username;
        GameController.sendAll(new Joined(playerClient.handle));
    }
}
