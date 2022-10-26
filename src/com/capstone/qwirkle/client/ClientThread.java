package com.capstone.qwirkle.client;

import com.capstone.qwirkle.Displayer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientThread extends Thread{
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private String serverAddress;
    private Socket connection;
    private String id = "";

    private final Displayer<String> displayer;
    private final Runnable onConnected;
    private Runnable onDisconnected;

    public ClientThread(String serverAddress, Displayer<String> displayer, Runnable onConnected, Runnable onDisconnected) {
        this.serverAddress = serverAddress;
        this.displayer = displayer;
        this.onConnected = onConnected;
        this.onDisconnected = onDisconnected;
    }

    protected void display(String message) {
        if(displayer == null) return;
        displayer.display(message);
    }

    @Override
    public void run() {
        try {
            connectToServer(); //create socket connection to server

            getStreams();   //get I/O streams

            readMessages(); //process messages received

            closeConnection(); //close connection when done

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Publish message to server or client
    private void sendMessage(Object message){
        try {
            Map<String, Object> params = new HashMap<>();
            if(message instanceof String) {
                String[] processStrArr = extractClientID((String) message);

                if(processStrArr.length > 1)
                    params.put(processStrArr[0], processStrArr[1]);
                else params.put(processStrArr[0], null);


            } else params.put(id, message);

            oos.writeObject(params);

            oos.flush();

            display(message.toString());
        } catch (IOException e) {
            display("Error sending message... " + e.getMessage());
        }

    }

    private String[] extractClientID(String message){
        String[] extractedStrArr = null;
        //will take exemplar string of "@2 hey" and split into string array of ["2"; ]
        if(message.startsWith("@") && message.length() > 3){ //messages of "@" or "@1" or @
            extractedStrArr = message.split(" ");
            extractedStrArr[0] = extractedStrArr[0].substring(1);
        } else if(message.startsWith("quit"))
            extractedStrArr = new String[]{message};
        else display("Invalid direct message ");
        return extractedStrArr;
    }
    private void connectToServer() throws IOException {
        display("Attempting connection to server...");

        connection = new Socket(InetAddress.getLocalHost(), 500);

        display("Connected to: " + connection.getInetAddress().getHostName());
    }

    private void getStreams() throws IOException {
        oos = new ObjectOutputStream(connection.getOutputStream());

        oos.flush();

        ois = new ObjectInputStream(connection.getInputStream());

        display("I/O streams initialised.");
    }

    private void readMessages() throws IOException {
        //Send initial msg to server
        Map<String, Object> message = new HashMap<>();
        message.put("connection", "Connection successful. Client #" + id);
        sendMessage(message);

        if(onConnected != null) onConnected.run();

        try {
            do {
                message = (Map<String, Object>) ois.readObject();

                //            message =
            } while (true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void closeConnection() {

    }
}
