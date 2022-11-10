package com.capstone.qwirkle.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {
    private static final long serialVersionUID = 71L;
    private ArrayList<Tile> hand;
    public String username= "0";
    public int points;
    private String gameID;
    public  int playerNo;
    public boolean swapping =false; //is player currently swapping
    public boolean placing = false; //is player currently placing



    public Player() {
        points = 0;
    }

    public boolean isSwapping() {
        return swapping;
    }

    public void setSwapping(boolean swapping) {
        this.swapping = swapping;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public ArrayList<Tile> getHand() {
        return hand;
    }

    public void setHand(ArrayList<Tile> hand) {
        this.hand = hand;
    }

    public String getGameID() {
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
