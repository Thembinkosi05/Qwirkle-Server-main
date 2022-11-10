package com.capstone.qwirkle;

import com.capstone.qwirkle.messages.Message;
import com.capstone.qwirkle.messages.server.GameStarted;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class GameLobbies {
    private static PubSubBroker broker = PubSubBroker.getInstance();

    private static final ReentrantLock lock = new ReentrantLock();
    private static final Map<String, GameController> lobbies = new ConcurrentHashMap<>(); //Use thread-safe Map

    public GameLobbies() {
    }

    public Map<String,GameController> getLobbies(){
        return lobbies;
    }

    public static String generateLobbyID() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        Random ran = new Random();
        int startIdx = ran.nextInt(uuid.length() - 5);
        uuid = uuid.substring(startIdx, startIdx + 5);
        return uuid.toUpperCase();
    }

    public GameController getLobby(String lobbyID){
        if(!lobbies.containsKey(lobbyID)) return null;
        return lobbies.get(lobbyID);
    }

    public static void startGame(String gameID) {

        //lock.lock();
        GameController gameController = lobbies.get(gameID);
        gameController.createPlayersHand();
        Random ran = new Random();
        int pos = ran.nextInt(gameController.getPlayers().size());
        gameController.setCurrentPlayer(gameController.getPlayers().get(pos));
        GameController.sendAll(new GameStarted(gameController.getPlayers().get(pos)));
        System.out.println("bag size:"+gameController.getBag().size());
        System.out.println("player size: "+gameController.getPlayers().size());
       // lock.unlock();
    }


/*
    //unique lobbyID -> {Players}
    public static final Map<String, Lobby> lobbies = new HashMap<>();


    public static GameLobbies getInstance(){
        return new GameLobbies();
    }*/
    /**
     * Creates a new game lobby with a unique ID
     */
    /*
    public static Lobby addLobby() {
        lock.lock();
        String lobbyID = "";
        do {
            lobbyID = generateLobbyID(); //make sure a unique is generated
        } while (lobbies.containsKey(lobbyID));

        Lobby toReturn = new Lobby(lobbyID);
        lobbies.put(lobbyID, toReturn);
        lock.unlock();
        return toReturn;
    }

    public static void joinLobby(PlayerClient playerClient) {
        //if in game lobby then leave
        leaveLobby(playerClient);

        lock.lock();

        Lobby lobby = getFreeLobby();
        lobby.addPlayer(playerClient);
        playerClient.lobbyID = lobby.getLobbyID();

        Map<String, Message> params = new HashMap<>();
        params.put("joined", new Joined(lobby.getLobbyID(), lobby.getGame().getPlayers()));
        broker.publish(lobby, "new_player", params);

        lock.unlock();
    }

    public static void leaveLobby(PlayerClient playerClient) {
        lock.lock();
        if (!playerClient.lobbyID.equals("")) {
            //if player is in a lobby
            Lobby lobby = lobbies.get(playerClient.lobbyID);
            if(lobby != null) {
                lobby.leave(playerClient);
                playerClient.lobbyID = "";

                Map<String, Message> params = new HashMap<>();
                params.put("leave_game", new Left(lobby.getLobbyID(), playerClient.clientName));

                broker.publish(lobby, "player_left", params);
            }
        }

        lock.unlock();
    }
*/
    /**
     * Get the next free lobby or creates new lobby.
     *
     * @return a free lobby id
     *
    public static Lobby getFreeLobby() {
        Lobby toReturn = null;
        for (Lobby lobby : lobbies.values()) {
            if (!lobby.isFull())
                toReturn = lobby;
        }

        if (toReturn == null)
            return addLobby();
        return toReturn;

    }

    private static String generateLobbyID() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        Random ran = new Random();
        int startIdx = ran.nextInt(uuid.length() - 5);
        uuid = uuid.substring(startIdx, startIdx + 5);
        return uuid.toUpperCase();
    }

    public static Lobby getLobby(String lobbyID){
        if(!lobbies.containsKey(lobbyID)) return null;

        return lobbies.get(lobbyID);
    }


    private static void send(String lobbyID, Message message){
        lock.lock();
        if(!lobbies.containsKey(lobbyID)) return;


        Map<String, Message> params = new HashMap<>();
        params.put("lobby", message);

        broker.publish(lobbies.get(lobbyID), "lobby_broadcast", params);

        lock.unlock();
    }

    public static void sendAll(Message message) {
        lock.lock();

        Map<String, Message> params = new HashMap<>();
        params.put("server", message);
        broker.publish(getInstance(), "broadcast", params);
        lock.unlock();
    }
*/
/*
    public static class Lobby {

        private String lobbyID;
        private Set<PlayerClient> players;
        private Random ran;
        GameController game;
        private boolean started = false;

        public Lobby(String lobbyID) {
            this.lobbyID = lobbyID;
            players = new HashSet<>();
            ran = new Random();
            game = new GameController(lobbyID);
        }

        public void addPlayer(PlayerClient client) {
            if (!isFull()) {
                players.add(client);
                game.addPlayer(client.clientName);
                client.setPlayer(game.getPlayers().get(game.getPlayers().size() - 1));
                Dice.DColour colour = assignColour();
                client.send(new PlayerAssigned(colour, client.getPlayer()));
                client.getPlayer().setColour(colour);
//                sendToLobby(new Joined(lobbyID, game.getPlayers()));

                if(isFull()){ //check again to make sure this new player added is not the one to make the lobby full
                    sendToLobby(new GameStarted());
                    sendToLobby(new PlayersListed(lobbyID));
                    sendToLobby(new RoundStarted(game.pickDice(), game.getRoundNumber()));
                    started = true;
                }
            }  else System.out.printf("Game is in progress - lobby #%s, cannot add Client: '%s'", lobbyID, client.clientName);

        }

        public boolean isFull() {
            return players.size() == 4;
        }

        public String getLobbyID() {
            return lobbyID;
        }

        public void leave(PlayerClient client) {
            players.remove(client);
        }

        public GameController getGame() {
            return game;
        }

        public Set<PlayerClient> getPlayers() {
            return players;
        }

        public void sendToLobby(Message message){
            GameLobbies.send(lobbyID, message);
//            Map<String, Message> params = new HashMap<>();
//            params.put("lobby", message);
//            broker.publish(this, "lobby_broadcast", params);
        }
    }

 */
}
