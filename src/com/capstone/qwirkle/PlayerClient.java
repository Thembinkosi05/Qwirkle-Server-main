package com.capstone.qwirkle;

import com.capstone.qwirkle.messages.Message;
import com.capstone.qwirkle.models.Player;
import com.capstone.qwirkle.messages.server.*;
import com.capstone.qwirkle.messages.client.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class PlayerClient {
    private final PubSubBroker broker = PubSubBroker.getInstance();
    private Socket socket;
    private String id;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    private BlockingQueue<Message> outgoingMessages = new LinkedBlockingDeque<>();

    private ReadingThread readThread;
    private WriteThread writeThread;

    //details about the current connection's client
    Player player = null;
    String username;


    public PlayerClient(Socket socket,int username) {
        this.socket=socket;
        readThread=new ReadingThread();
        readThread.start();
       // GameController.addClient(this);
       // System.out.println(new Joined(username));
    }

    public class ReadingThread extends Thread{
        @Override
        public void run() {
            System.out.println("Reading thread started running");
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());

                writeThread = new WriteThread();
                writeThread.start();
                Message message;
                do {
                    message = (Message) in.readObject();

                    message.applyLogic(PlayerClient.this);
                    System.out.println(username + "------>" + message);
                } while (message.getClass() != Quit.class);
            } catch (Exception e) {
                System.out.println("ReadingThread error:" + e.getMessage() + "for" + username);
                e.printStackTrace();
            }
        }
    }

    private class WriteThread extends Thread{
        @Override
        public void run() {
            System.out.println("Writing thread started running");
            writeThread = this;
            try {
                while (!interrupted()) {
                    Message message = outgoingMessages.take();
                    out.writeObject(message);
                    out.flush();
                }
            } catch (Exception e) {
                System.out.println("WriteThread error:" + e.getMessage() + "for" + username);
                e.printStackTrace();
            }
        }
    }

    public void disconnect(){
        try {
            socket.close();
            writeThread.interrupt();
            readThread.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        try {
            outgoingMessages.put(message);
        } catch (InterruptedException e) {
            System.out.println("Error:" + e.getMessage());
        }
    }
}
