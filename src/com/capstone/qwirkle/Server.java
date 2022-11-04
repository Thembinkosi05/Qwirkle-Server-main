package com.capstone.qwirkle;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private ServerSocket serverSocket;
    int counter;
    public Server() {
        counter =0;
    }
    /**Starts the connection*/
    public void start(){
        try {
            serverSocket=new ServerSocket(5050);
            System.out.println("Server started on "+ InetAddress.getLocalHost().getHostAddress());
            GameLobbies.addLobby(1);
            while (!serverSocket.isClosed()){
                Socket clientSocket=serverSocket.accept();
                counter++;
                System.out.println("Client#"+counter+ " is connected from: "+clientSocket.getInetAddress());
                //handles mul
                PlayerClient playerClient =new PlayerClient(clientSocket,counter);
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
