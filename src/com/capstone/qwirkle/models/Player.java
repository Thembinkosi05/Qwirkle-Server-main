package com.capstone.qwirkle.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {
    private ArrayList<Tile> hand;
    public int points;
    private int gameID;

    public Player(ArrayList<Tile> hand)
    {
        points = 0;
        this.hand = hand;
    }

    public void setGameID(int gameIndex){
        gameID=gameIndex;
    }

    public ArrayList<Tile> getHand() {
        return hand;
    }

    public void setHand(ArrayList<Tile> hand) {
        this.hand = hand;
    }

    public int getGameID() {
        return gameID;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
