package ry.max.chat_server;

import ru.max.network.TCPConnection;
import ru.max.network.TCPConnectionLisenner;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionLisenner {

    public static void main(String[] args) {
        new ChatServer();
    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer () {
        System.out.println("Server running...");
        try (ServerSocket serverSocket = new ServerSocket(8189)){
            while (true) {
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("TCPConnection exeption: " + e);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sentToAllConections("Client connected: " + tcpConnection);
    }

    @Override
    public synchronized void onRaceiveString(TCPConnection tcpConnection, String value) {
        sentToAllConections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sentToAllConections("Client diconnected: " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exeption: " + e);
    }
    private void sentToAllConections(String value){
        System.out.println(value);
        final int cnt = connections.size();
        for (int i=0;i<connections.size();i++) connections.get(i).sendString(value);

    }
}
