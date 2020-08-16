package ru.max.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {

    private final Socket socket;
    private final Thread rxThead;
    private final TCPConnectionLisenner eventListener;
    private final BufferedReader in;
    private final BufferedWriter out;

    public TCPConnection(TCPConnectionLisenner eventListener, String ipAddr, int port) throws IOException {
        this(eventListener, new Socket(ipAddr, port));


    }

    public TCPConnection(TCPConnectionLisenner eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        rxThead = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPConnection.this);
                    while (!rxThead.isInterrupted()) {


                        eventListener.onRaceiveString(TCPConnection.this,in.readLine());
                    }
                } catch (IOException e) {
                    eventListener.onException(TCPConnection.this, e);
                }finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        rxThead.start();
    }

    public synchronized void sendString(String value){
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
            disconect();
        }
    }
    public synchronized void disconect() {
        rxThead.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
        }
    }


    @Override
    public String toString() {
        return "TCPConnection: " + socket.getLocalAddress() + ": " + socket.getPort();

    }


}
