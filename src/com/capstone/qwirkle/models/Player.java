package com.capstone.qwirkle.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {
    private ArrayList<Tile> hand;
    public String username;
    public int points;
    private int gameID;
    public  int playerNo;
    public boolean swapping =false; //is player currently swapping
    public boolean placing = false; //is player currently placing

    public boolean isSwapping() {
        return swapping;
    }

    public void setSwapping(boolean swapping) {
        this.swapping = swapping;
    }

    public Player(ArrayList<Tile> hand, String username) {
        this.username=username;
        points = 0;
        this.hand = hand;
    }
    public boolean isPlacing() {
        return placing;
    }

    public void setPlacing(boolean placing) {
        this.placing = placing;
    }

    public int getPlayerNo() {
        return playerNo;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
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
        this.points += points;
    }

    public void placeTile(){

    }

    @Override
    public String toString() {
        return "Player " + playerNo;
    }
}
