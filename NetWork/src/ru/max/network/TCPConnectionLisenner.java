package ru.max.network;

public interface TCPConnectionLisenner {

    void onConnectionReady(TCPConnection tcpConnection);
    void onRaceiveString(TCPConnection tcpConnection, String value);
    void onDisconnect(TCPConnection tcpConnection);
    void onException(TCPConnection tcpConnection, Exception e);

}
