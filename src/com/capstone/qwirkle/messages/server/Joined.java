package com.capstone.qwirkle.messages.server;

import com.capstone.qwirkle.messages.Message;
import com.capstone.qwirkle.models.Player;

import java.util.List;

public class Joined extends Message {
    private static final long serialVersionUID = 104L;
    public String name;


    public Joined(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%s",name);
    }
}
