package javasocketlib.server;

import javautils.types.lambdas.Out0In1;
import javautils.utils.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    // lambda on packet entered
    Out0In1<String> onPacketEntered;

    // server port and max connectable clients
    int port, maxConnects;

    // server socket class
    ServerSocket server;

    // client socket class
    Socket connectedClient;

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

    public Server(int p, int mc, Out0In1<String> ope){
        port = p;
        maxConnects = mc;
        onPacketEntered = ope;

        try {
            server = new ServerSocket(port, maxConnects);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Log.info("Opened server ip: " + server.getInetAddress().toString() + ", port: " + server.getLocalPort());
        }

        try {
            connectedClient = server.accept();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Log.info("Connected client ip: " + connectedClient.getInetAddress().toString());
        }

        createBuffers();
        runReceiver();
    }

    void createBuffers(){
        try {
            out = new BufferedWriter(new OutputStreamWriter(connectedClient.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(connectedClient.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            connectedClient.close();
            in.close();
            out.close();
            server.close();
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
            out.write(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
