package com.capstone.qwirkle;


import com.capstone.qwirkle.messages.Message;
import com.capstone.qwirkle.messages.server.GameStarted;
import com.capstone.qwirkle.models.Player;
import com.capstone.qwirkle.models.Tile;
import com.capstone.qwirkle.models.Tile.Shape;
import com.capstone.qwirkle.models.Tile.Colour;
import com.capstone.qwirkle.models.Tile.State;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameController implements Serializable {
    //private static final long serialVersionUID = 73L;

    private ArrayList<Tile> bag = new ArrayList<>();
    private ArrayList<Tile> GameBoard = new ArrayList<>();
    private ArrayList<Player> players = new ArrayList<>();
    private static ArrayList<PlayerClient> clients;
    private static final ReentrantLock lock = new ReentrantLock();
    private String gameID;
    public boolean isReady;
    public int playerTotal = 0;
    public static int curPlayerNo = 0;
    public Player curPlayer;

    ArrayList<Tile> verticalLine; //the line north and south.
    ArrayList<Tile> horizontalLine; //the line east and west.

    public void addClient(PlayerClient playerClient) {
        players.add(playerClient.getPlayer());
        clients.add(playerClient);
    }

    public String getGameID() {
        return gameID;
    }

    public void startGame() {
        createPlayersHand();
        Random ran = new Random();
        int pos = ran.nextInt(players.size());
        setCurrentPlayer(players.get(pos));
        sendAll(new GameStarted(players.get(pos)));
        System.out.println("bag size:"+getBag().size());
        System.out.println("player size: "+players.size());
    }

    private enum Direction {
        NORTH(0, -1),
        EAST(1, 0),
        SOUTH(0, 1),
        WEST(-1, 0);

        private int x;
        private int y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;

        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    public GameController(String gameID) {

        this.gameID =gameID;
        isReady=false;
        initialAllTiles();
        clients = new ArrayList<>();
    }

/*
    private void addPlayers(int playerTotal) {
        for(int i =0;i<playerTotal;i++){
            addPlayer(i+1);
        }
        setCurrentPlayer(players.get(0));
    }
*/

    /**
     * create all available tiles and add to the bag
     */
    public void initialAllTiles() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 6; k++) {
                    Tile tile = new Tile(Colour.values()[k], Shape.values()[j]);
                    tile.setState(Tile.State.IN_BAG);
                    bag.add(tile);
                }
            }
        }
        shuffle();
    }

    /**
     * shuffle pieces on the bag to make it even more random
     */
    private void shuffle() {
        Random r = new Random();
        for (int j = 0; j < 500; j++) {
            for (int i = 0; i < bag.size(); i++) {
                int random = r.nextInt(bag.size());
                Tile newPiece = bag.get(random);
                bag.remove(random);
                bag.add(newPiece);
            }
        }
    }

    public void createPlayersHand() {
        Random r = new Random();
        for (Player player : players) {
            ArrayList<Tile> hand = new ArrayList<>();
            for (int x = 0; x < 6; x++) {
                int randomPos = r.nextInt(bag.size());
                Tile randomPiece = bag.get(randomPos);
                bag.remove(randomPos);
                hand.add(randomPiece);
                randomPiece.setState(Tile.State.IN_HAND);
            }
            player.setHand(hand);
           // Player player = new Player(hand, playerNo);
           // players.add(player);
        }
    }

    private ArrayList<Tile> createPlayerHand() {
        ArrayList<Tile> hand = new ArrayList<>();
        Random r = new Random();
        for (int x = 0; x < 6; x++) {
            int randomPos = r.nextInt(bag.size());
            Tile randomTile = bag.get(randomPos);
            bag.remove(randomPos);
            hand.add(randomTile);
            randomTile.setState(Tile.State.IN_HAND);
        }
        return hand;
    }

    public int changeCurPlayer() {
        if (players.size() == 1) return 0;
        if (curPlayerNo == 0) {
            curPlayerNo++;
            curPlayer = players.get(curPlayerNo);
        } else if (curPlayerNo == 1) {
            curPlayerNo--;
            curPlayer = players.get(curPlayerNo);
        }
        return curPlayerNo;
    }

    public boolean validMovesExist (ArrayList<Tile> hand,ArrayList<Tile> gameBoard) {
        for(Tile tile : hand){
            if(isValidMove(tile.getRow(), tile.getCol(),tile,gameBoard)){
                return true;
            }
        }
        return false;
    }

    //check to see if the intended move is valid or not
    public boolean isValidMove(int x, int y, Tile tile, ArrayList<Tile> board) {
        //any placement if the board is empty
        if (board.isEmpty()) {
            return true;
        }

        //is position(x,y) is an empty position on the board
        for(Tile tile1 : board){
            if((tile1.getRow()==x)&&(tile1.getCol()==y))
                return false;
        }

        this.verticalLine = new ArrayList<>();
        verticalLine.add(tile);
        this.horizontalLine = new ArrayList<>();
        horizontalLine.add(tile);


        // Step 4: Find the tiles in line that Model.tile is being added to.
        createLine(x, y, verticalLine, Direction.NORTH, board);
        createLine(x, y, verticalLine, Direction.SOUTH, board);
        createLine(x, y, horizontalLine, Direction.EAST, board);
        createLine(x, y, horizontalLine, Direction.WEST, board);

        tile.horizontalLine = horizontalLine;
        tile.verticalLine = verticalLine;


        if (verticalLine.size() == 1 && horizontalLine.size() == 1) {
            return false;
        }
        if (verticalLine.size() > 1) {
            boolean sameShape = isSameShape(verticalLine);
            if (!isValidLine(verticalLine, sameShape)) {
                return false;
            }
        }
        if (horizontalLine.size() > 1) {
            boolean sameShape = isSameShape(horizontalLine);
            if (!isValidLine(horizontalLine, sameShape)) {
                return false;
            }
        }

        return true;
    }

    private void createLine(int x, int y, ArrayList<Tile> line, Direction dir, ArrayList<Tile> gameBoard) {
        // Increment x & y by direction first.
        x += dir.getX();
        y += dir.getY();

        while (getTileAt(x,y,gameBoard) != null) {
            //Add Model.tile to the line
            Tile tile = getTileAt(x,y,gameBoard);
            line.add(tile);
            if (dir == Direction.EAST || dir == Direction.WEST) {
                assert tile != null;
                tile.checkEW = false;
            }
            if (dir == Direction.NORTH || dir == Direction.SOUTH) {
                assert tile != null;
                tile.checkNS = false;
            }
            // Increment by the direction in x & y.
            x += dir.getX();
            y += dir.getY();
        }
    }

    private boolean isValidLine(ArrayList<Tile> line, boolean isSameShape) {
        if (isSameShape) {
            Shape shape1 = line.get(0).getShape();
            boolean[] differentColors = new boolean[6];
            for (Tile tile : line) {
                if (!tile.getShape().equals(shape1)) {
                    return false;
                }

                Tile.Colour color = tile.getColour();
                if (differentColors[color.ordinal()]) {
                    return false;
                } else {
                    differentColors[color.ordinal()] = true;
                }
            }
        } else {
            Tile.Colour color1 = line.get(0).getColour();
            boolean[] differentShapes = new boolean[6];
            for (Tile tile : line) {
                if (!tile.getColour().equals(color1)) {
                    return false;
                }
                Shape shape = tile.getShape();
                if (differentShapes[shape.ordinal()]) {
                    return false;
                } else {
                    differentShapes[shape.ordinal()] = true;
                }
            }
        }

        return true;
    }

    private boolean isSameShape(ArrayList<Tile> line) {
        // This function assumes that there are at least two tiles in the line
        // (otherwise must not be called, as it will throw exception)
        Tile tile1 = line.get(0);
        Tile tile2 = line.get(1);

        return tile1.getShape().equals(tile2.getShape());
    }

    public ArrayList<Point> getValidPositions (Tile tile, ArrayList<Tile> board){
        ArrayList<Point> validPosition = new ArrayList<>(); //store valid positions that will be highlighted(green) on the board
        for(Tile tile1 : board){                            //when the tile is selected on the hand
            int up = tile1.getRow()-1;
            int down = tile1.getRow()+1;
            int left = tile1.getCol()-1;
            int right = tile1.getCol()+1;

            if(isValidMove(up,tile1.getCol(),tile,board)){
                validPosition.add(new Point(up,tile1.getCol()));
            }
            if(isValidMove(down,tile1.getCol(),tile,board)){
                validPosition.add(new Point(down,tile1.getCol()));
            }
            if(isValidMove(tile1.getRow(),left,tile,board)){
                validPosition.add(new Point(tile1.getRow(),left));
            }
            if(isValidMove(tile1.getRow(),right,tile,board)){
                validPosition.add(new Point(tile1.getRow(),right));
            }
        }
        return validPosition;
    }

    public void addToBoard(ArrayList<Tile> hand) {
        boolean placed = false;
        Stream stream = hand.stream().filter(tile -> tile.state.equals(Tile.State.PLACING));
        List<Tile> placeList = (List<Tile>) stream.collect(Collectors.toList());
        for (Tile tile : placeList) {
            if (tile.state.equals(Tile.State.PLACING)) {
                hand.remove(tile);
                tile.setState(Tile.State.PLACED);
                GameBoard.add(tile);
                placed = true;
            }
        }
        if (placed) {
            curPlayer.setHand(hand);
            fillHand();
        }
    }

    public void swapPieces(ArrayList<Tile> hand) {
        boolean swapped = false;
        if (hand.size() > bag.size()) return;
        Stream stream = hand.stream().filter(tile -> tile.state.equals(Tile.State.SWAPPING));

        List<Tile> swapList = (List<Tile>) stream.collect(Collectors.toList());
        for (Tile tile : swapList) {
            if (tile.state.equals(Tile.State.SWAPPING)) {
                hand.remove(tile);
                tile.setState(Tile.State.IN_BAG);
                bag.add(tile);
                swapped = true;
            }
        }
        if (swapped) {
            shuffle();
            curPlayer.setHand(hand);
            fillHandForSwap();
        }
    }


    public void fillHandForSwap() {
        while (curPlayer.getHand().size() < 6 && bag.size() > 1) {
            Tile newTile = bag.remove(bag.size() - 1);
            newTile.setSwapped(true);
            curPlayer.getHand().add(newTile);
            newTile.setState(Tile.State.IN_HAND);
        }
    }

    /**
     * fill hand of the current player
     */
    public void fillHand() {
        while (curPlayer.getHand().size() < 6 && bag.size() > 1) {
            Tile newTile = bag.remove(bag.size() - 1);
            curPlayer.getHand().add(newTile);
            newTile.setState(Tile.State.IN_HAND);
        }
    }

    public Player addPlayer() {
        if (bag.size() < 6) return null;
        Player player = new Player();
//      player.setGameID(gameID);
        players.add(player);
        if(players.size()==playerTotal)isReady=true;
        if (players.size() == 1) curPlayer = players.get(0);
        return player;
    }

    public void placementScore(ArrayList<Tile> tiles){
        if(tiles.isEmpty())
            return;
        Stream stream = tiles.stream().filter(tile -> tile.state.equals(State.PLACING));
        ArrayList<Tile> placingList = (ArrayList<Tile>)stream.collect(Collectors.toList());
        int points = getPoints(placingList);
        /*
        for(Tile tile : placingList){
            if(tile.getState().equals(State.PLACING)){
                int row = tile.getRow();
                int col = tile.getCol();
                points =determineScore(tile);
            }
        }*/
        curPlayer.setPoints(points);
    }

    int getPoints(ArrayList<Tile> tiles) {
        int points = 0;
        for (Tile tile : tiles) {
            tile.setState(State.PLACED);
            if (tile.verticalLine.size() == 1 && tile.horizontalLine.size() == 1) points += 1;

            if (tile.verticalLine.size() > 1) {
                for (Tile t : tile.verticalLine) {
                    if (!t.checkNS) {
                        points = points + 1;
                        t.checkNS = true;

                    }

                }
            }

            if (horizontalLine.size() > 1) {
                for (Tile t2 : tile.horizontalLine) {
                    //if the check is false it has not been counted
                    if (!t2.checkEW) {
                        points += 1;
                        t2.checkEW = true;
                    }
                }

            }

            if (verticalLine.size() == 6) points += 6;
            if (horizontalLine.size() == 6) points += 6;

        }
        return points;
    }

    public void score(ArrayList<Tile> tiles){
        int points = 0;
        verticalLine= new ArrayList<>();
        horizontalLine =new ArrayList<>();
        for (Tile tile : tiles){
            if(tile.getState().equals(State.PLACING)){
                int i = 0;
                //vertical up
                while(getTileAt(tile.getRow()-i, tile.getCol(), tiles)!=null){
                    verticalLine.add(getTileAt(tile.getRow()-i, tile.getCol(),tiles));
                    i= i-1;
                }
                points =points+verticalLine.size();
                verticalLine.clear();
                i=0;

                //vertical down
                while(getTileAt(tile.getRow()+i, tile.getCol(),tiles)!=null){
                    verticalLine.add(getTileAt(tile.getRow()+i, tile.getCol(),tiles));
                    i= i+1;
                }
                points =points+verticalLine.size();
                verticalLine.clear();
                i=0;

                //horizontal right
                while(getTileAt(tile.getRow(), tile.getCol()+i,tiles)!=null){
                    horizontalLine.add(getTileAt(tile.getRow(), tile.getCol()+i,tiles));
                    i= i+1;
                }
                points =points+horizontalLine.size();
                horizontalLine.clear();
                i=0;

                //horizontal Left
                while(getTileAt(tile.getRow(), tile.getCol()-i,tiles)!=null){
                    horizontalLine.add(getTileAt(tile.getRow(), tile.getCol()-i,tiles));
                    i= i-1;
                }
                points =points+horizontalLine.size();
                horizontalLine.clear();
            }
        }
        curPlayer.setPoints(points);
    }

    /**
     * change turn of the current player to next player;
     */
    public void changeTurn(){
        int currentIndex = players.indexOf(curPlayer);
        if((currentIndex+1)==players.size())
        {
            setCurrentPlayer(players.get(0));
        }
        else{
            setCurrentPlayer(players.get(currentIndex+1));
        }
    }

    public void setCurrentPlayer(Player player){
        curPlayer=player;
    }

    private Tile getTileAt(int row, int col,ArrayList<Tile> tiles) {
        for(Tile tile : tiles){
            if((tile.getRow()==row)&&(tile.getCol()==col))
                return tile;
        }
        return null;
    }

   /* public void setGameID(int gamesIndex){
        gameID=gamesIndex;
    }*/

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public ArrayList<Tile> getGameBoard() {
        return GameBoard;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Tile> getBag() {
        return bag;
    }

    public void setToPlaced(ArrayList<Tile> hand){
        for(Tile tile : hand){
            if(tile.getState().equals(State.PLACING))
                tile.setState(State.PLACED);
        }
    }

    public void unswipe(){
        for(Tile tile : curPlayer.getHand()){
            tile.setSwapped(false);
        }
        curPlayer.setSwapping(false);
    }

    public static void send(PlayerClient client,Message message) {
        lock.lock();
        // Send message to player.
        client.sendMessage(message);
        lock.unlock();
    }

    public static void sendAll(Message message) {
        lock.lock();
        // Send message to each client.
        for(PlayerClient c : clients ) {
            c.sendMessage(message);
        }
        lock.unlock();
    }
}
