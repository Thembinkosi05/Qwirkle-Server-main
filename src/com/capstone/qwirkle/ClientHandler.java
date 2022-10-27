package com.capstone.qwirkle;

import com.capstone.qwirkle.server.ServerThread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable {
    private final PubSubBroker broker = PubSubBroker.getInstance();
    private Socket socket;
    private String id;
    private Displayer<String> displayer;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Map<String , ClientHandler> clients;

    private Runnable onConnected;
    private Runnable onDisconnected;

    public ClientHandler(Socket client, int id, Displayer<String> displayer) {
        socket = client;
        this.id = String.valueOf(id);
        clients = ServerThread.getConnectedClients();
    }


    public void setOnConnected(Runnable onConnected) {
        this.onConnected = onConnected;
    }

    public void setOnDisconnected(Runnable onDisconnected) {
        this.onDisconnected = onDisconnected;
    }

    @Override
    public void run() {
        try {
            while (true) {
                //Get Input and Output
                getStreams();

                //Process connection
                readMessages();
                //Stop Connection
                closeConnection();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Object message) {
//        final Object[] messages = new Object[]{message};

        try {

            oos.writeObject(message);

            oos.flush();

            display(message.toString());

        } catch (IOException e) {
            display("Error writing object. " + e.getMessage());
        }
    }

    protected void display(String message) {
        if (displayer == null) return;
        displayer.display(message);
    }

    private void getStreams() throws IOException {
        oos = new ObjectOutputStream(socket.getOutputStream());

        oos.flush();

        ois = new ObjectInputStream(socket.getInputStream());
        Subscriber connectionSub = (publisher, topic, params) -> {
            if ("connection".equals(topic)) {
                if (((publisher).equals(id))) {
                    Map<String, Object> message = new HashMap<>();
                    message.put("connection", "Connection successful. Client #" + id);
                    sendMessage(message);

                    message.put(id, "Client ID#: " + id + " is online.");
                    for (Map.Entry<String, ClientHandler> curClient : clients.entrySet()) {
                        curClient.getValue().sendMessage(message);
                        display("Message to #" + curClient.getKey() + ": " + message);
                    }
                }
            }
        };
        broker.subscribe("connection", connectionSub);
        display("I/O streams obtained...");
    }

    /**
     * When reading a message sent by client, publish first that connection is successful
     * then subsequent messages relate to chat
     *
     * @throws IOException thrown if object parsed from inputStream is not of the expected type.
     */
    private void readMessages() throws IOException {
        Map<String, Object> message = new HashMap<>();
        message.put("connection", "Connection successful. Client #" + id);


        broker.publish(id, "connection", null);
        if (onConnected != null) onConnected.run();

        String msgTopic = "message";

        Subscriber messageSub = (publisher, topic, params1) -> {
            switch(topic){
                case "message":
                    if(params1.containsKey(String.valueOf(id))) {
                        String message1 = (String) params1.get(String.valueOf(id));
                        display("Client #" + publisher + " says: " + message1);
                        sendMessage(params1);
//                    broker.publish(id, "message", );

                    }
                    break;
                case "broadcast":
                    String message2 = (String) params1.get(String.valueOf(id));
                    for (Map.Entry<String, ClientHandler> curClient : clients.entrySet()) {
                        curClient.getValue().sendMessage(message2);
                        display("Client #" + publisher + " says: " + message2);
                    }
                    break;
                case "quit":
                    try {
                        closeConnection();
                    } catch (IOException e) {
                        display("Could not close the connection...");
                        e.printStackTrace();
                    }
                default: break;
            }
        };
        broker.subscribe("message", messageSub);
        broker.subscribe("broadcast", messageSub);
        broker.subscribe("quit", messageSub);


        //the actual chatting part of the that loops until termination request from client
        do {
            try {
                Map<String, Object> params = (Map<String, Object>) ois.readObject();

                if(params.keySet().contains("broadcast")){
                    broker.publish(id, "broadcast", params);
                } else if(params.keySet().contains("connection"))
                    broker.publish(id, "connection", params);
                else if(params.keySet().contains("quit"))
                    broker.publish(id, "quit", null);
                else broker.publish(id, "message", params);

            } catch (ClassNotFoundException e) {
                display("Unknown object received...");
            }
        } while (true);
    }

    private void closeConnection() throws IOException {
        display("User terminated connection.-");
        oos.close();
        ois.close();
        socket.close();
        ServerThread.getConnectedClients().remove(id);

        if (onDisconnected != null) onDisconnected.run();
    }
}
