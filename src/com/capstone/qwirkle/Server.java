package com.capstone.qwirkle;

import com.capstone.qwirkle.models.Player;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;

public class Server {
    private ServerSocket serverSocket;
    Timer timer;
    int counter;
    String gameID;
    GameLobbies gameLobbies;
    GameController curGameController;
    public Server() {
        counter =0;
        gameLobbies = new GameLobbies();
    }
    /**Starts the connection*/
    public void start(){
        try {
            serverSocket=new ServerSocket(5050);
            System.out.println("Server started on "+ InetAddress.getLocalHost().getHostAddress());
            GameLobbies gameLobbies = new GameLobbies();
            while (!serverSocket.isClosed()){
                Socket clientSocket=serverSocket.accept();
                counter++;
                System.out.println("Client#"+counter+ " is connected from: "+clientSocket.getInetAddress());
                PlayerClient playerClient =new PlayerClient(clientSocket);
                /*
                if(counter==1){
                    gameID = GameLobbies.generateLobbyID();
                    gameLobbies.getLobbies().put(gameID,new GameController(gameID)); //new game created
                }

                if(counter==2){
                    //game should start after 30 seconds if to two players join
                    Thread.sleep(30000);
                    curGameController = gameLobbies.getLobby(gameID);

                }*/

                if(counter==1){
                    gameID ="sss";
                    gameLobbies.getLobbies().put(gameID,new GameController(gameID)); //new game created
                    curGameController = gameLobbies.getLobby(gameID);
                }
                Player player = new Player();
                player.setGameID(gameID);
                playerClient.setPlayer(player);
                curGameController.addClient(playerClient);

                //if two or more players have joined and 30 sec has elapsed
                if(counter==2){
                    curGameController.startGame();
                    counter=0;
                }
            }
        }catch (IOException e){
            closeServer();
            System.out.println("Error: "+e.getMessage());
        }
    }

    public static void main(String[] args) {
        Server Server = new Server();
        Server.start();
    }
    public void closeServer(){
        try
        {
            if(serverSocket!=null)
                serverSocket.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
