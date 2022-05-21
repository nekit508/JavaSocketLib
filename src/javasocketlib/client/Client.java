package javasocketlib.client;

import javautils.types.lambdas.Out0In1;

import java.io.*;
import java.net.Socket;

public class Client {
    // lambda on packet entered
    Out0In1<String> onPacketEntered;

    // server ip
    String ip;

    // client port
    int port;

    // client socket
    Socket client;

    // buffers
    BufferedReader in;
    BufferedWriter out;

    // receiving thread
    Thread receivingThread = new Thread(() -> {
        while(true){
            try {
                onPacketEntered.get(in.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });

    public Client(String i, int p, Out0In1<String> ope) {
        ip = i;
        port = p;
        onPacketEntered = ope;

        try {
            client = new Socket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        createBuffers();
        runReceiver();
    }

    void createBuffers(){
        try {
            out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            in.close();
            out.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void runReceiver(){
        receivingThread.setDaemon(true);
        receivingThread.start();
    }

    public void send(String message){
        try {
            out.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
