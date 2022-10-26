package com.capstone.qwirkle.server;

import com.capstone.qwirkle.ClientConnector;
import com.capstone.qwirkle.Displayer;
import com.capstone.qwirkle.PubSubBroker;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerThread extends Thread {
    private final PubSubBroker broker = PubSubBroker.getInstance();
    private ServerSocket server;
    private Socket connection;
    private int id = 1;
    private static Map<String, ClientConnector> connectedClients;

    private Displayer<String> displayer;
    private Runnable onConnected;
    private Runnable onDisconnected;

    public ServerThread(Displayer<String> displayer, Runnable onConnected, Runnable onDisconnected) {
        this.displayer = displayer;
        this.onConnected = onConnected;
        this.onDisconnected = onDisconnected;
        connectedClients = new ConcurrentHashMap<>();
    }

    public static Map<String, ClientConnector> getConnectedClients() {
        return connectedClients;
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(500, 100);
            display("Started server: " + InetAddress.getLocalHost().getHostAddress());

            while (true) {
                waitForConnection();
//                //Get Input and Output
//                getStreams();
//

//                //Process connection
//                readMessages();

//                //Stop Connection
//                closeConnection();
                ++id;
            }

        } catch (IOException e) {
            display("Error writing object! " + e.getMessage());
            e.printStackTrace();
        }
    }

//    public void sendMessage(String message) {
//        final String[] messages = new String[]{message};
//
//            try {
//
//                messages[0] = "SERVER>>> " + messages[0];
//
//                oos.writeObject(messages[0]);
//
//                oos.flush();
//
//                display(messages[0]);
//
//            } catch (IOException e) {
//                display("Error writing object. " + e.getMessage());
////                e.printStackTrace();
//            }
//    }

    protected void display(String message) {
        if (displayer == null) return;
        displayer.display(message);
    }

    private void waitForConnection() throws IOException {
        display("Waiting for connection...");

        connection = server.accept();
        ClientConnector connector = new ClientConnector(connection, ++id, this::display);
        connector.setOnConnected(onConnected);
        connector.setOnDisconnected(onDisconnected);
        new Thread(connector).start();

        display("Connection #" + id + " received from: "
                + connection.getInetAddress().getHostName());
    }

//    private void getStreams() throws IOException {
//        oos = new ObjectOutputStream(connection.getOutputStream());
//
//        oos.flush();
//
//        ois = new ObjectInputStream(connection.getInputStream());
//
//        display("I/O streams obtained...");
//    }

//    private void readMessages() throws IOException {
//        String message = "Connection successful. Client #" + id;
//        sendMessage(message);
//
//        if (onConnected != null) onConnected.run();
//
//        do {
//            try {
//                message = (String) ois.readObject();
//                Map<String, Object> params = new HashMap<>();
//                params.put("text", message);
//                broker.publish(id, "hello", params);
//                display(message);
//
//            } catch (ClassNotFoundException e) {
//                display("Unknown object received...");
////                e.printStackTrace();
//            }
//        } while (!message.equals("CLIENT>>> TERMINATE"));
//    }

//    private void closeConnection() throws IOException {
//        display("User terminated connection.-");
//        oos.close();
//        ois.close();
//        connection.close();
//
//        if (onDisconnected != null) onDisconnected.run();
//    }

}
