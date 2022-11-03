package com.capstone.qwirkle;

import com.capstone.qwirkle.models.Player;
import com.capstone.qwirkle.models.Tile;
import com.capstone.qwirkle.models.Tile.Shape;
import com.capstone.qwirkle.models.Tile.Color;
import com.capstone.qwirkle.models.Tile.State;

import java.util.ArrayList;
import java.util.Random;

public class GameController {


    private ArrayList<Tile> bag = new ArrayList<>();   //108 initial number of tile
    private ArrayList<Tile> board = new ArrayList<>();

    private ArrayList<Player> players = new ArrayList<>();
    private Integer gameID;  //different lobbies
    public boolean isReady;
    public int playerTotal = 0;
    public static int curPlayerNo = 0;
    public Player curPlayer;

    public GameController(int playerTotal) {
        generatePieces();
        this.playerTotal=playerTotal;
        isReady=false;
    }

    public void setGameID(int gamesIndex){
        gameID=gamesIndex;
    }

    public void generatePieces() {
        //repeat this three times so each shape has three in the same color
        for (int i = 0; i < 3; i++) {

            for (int s = 0; s < 6; s++) {

                //each color gets one shape
                for (int c = 0; c < 6; c++) {
                    Tile tile = new Tile(Color.values()[c], Shape.values()[s]);
                    tile.setState(Tile.State.inBag);
                    bag.add(tile);
                }
            }
        }
        shuffle();
    }

    private void shuffle() {
        // Very basic shuffle
        Random r = new Random();
        for (int j = 0; j < 500; j++) {
            for (int i = 0; i < bag.size(); i++) {
                int randomPos = r.nextInt(bag.size());
                Tile newPiece = bag.get(randomPos);
                bag.remove(randomPos);
                bag.add(newPiece);
            }
        }
    }

    public static void leaveGame(PlayerClient playerClient){

    }

    public static void addClient(){

    }
}
