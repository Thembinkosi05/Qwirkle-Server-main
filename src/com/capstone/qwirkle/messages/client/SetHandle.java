package com.capstone.qwirkle.messages.client;

import com.capstone.qwirkle.PlayerClient;
import com.capstone.qwirkle.messages.Message;
import com.capstone.qwirkle.messages.server.handleSet;
import com.capstone.qwirkle.models.Player;

public class SetHandle extends Message<PlayerClient> {
    private static final long serialVersionUID = 5L;

    public String handle;

    public SetHandle(String handle) {
        this.handle = handle;
    }

    @Override
    public String toString() {
        return String.format("SetHandle('%s')", handle);
    }

    @Override
    public void applyLogic(PlayerClient playerClient) {
        // Check if the handle is already being used. If is, append client number.

        // Set the handle.
        playerClient.handle = handle;
        // Tell the client about the handle that was decided upon.
        playerClient.sendMessage(new handleSet(handle));
    }
}
