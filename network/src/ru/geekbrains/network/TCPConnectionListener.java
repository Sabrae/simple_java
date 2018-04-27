package ru.geekbrains.network;

public interface TCPConnectionListener {

    void onConnectionReady(TCPConnection tcpConnection);//когда соединение готовое
    void onReceiveString(TCPConnection tcpConnection, String value); //если принимаем строку
    void onDisconnect(TCPConnection tcpConnection);//если дисконнект
    void onException(TCPConnection tcpConnection, Exception e);//если ошибка

}
