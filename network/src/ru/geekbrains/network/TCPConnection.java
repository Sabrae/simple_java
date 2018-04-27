package ru.geekbrains.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {

    private final Socket socket;
    private final Thread rxThread; //поток
    private final TCPConnectionListener eventListener;
    private final BufferedReader in;//читает строки
    private final BufferedWriter out;//выводит строки

    public TCPConnection(TCPConnectionListener eventListener, String ipAddr, int port) throws IOException {
        this (eventListener, new Socket (ipAddr, port));
    }

    public TCPConnection (TCPConnectionListener eventListener, Socket socket) throws IOException { // указано, что бросает IOException
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));//задание потока ввода + указали кодировку
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));//создание потока вывода с указанием кодировки
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPConnection.this);
                    while (!rxThread.isInterrupted()){
                        eventListener.onReceiveString(TCPConnection.this, in.readLine());
                        /* другой вариант:
                        String msg = in.readLine();//прочитали строку
                        eventListener.onReceiveString(TCPConnection.this, msg);
                         */
                    }
                } catch (IOException e){ //ловим исключение, которое может сгенерировать .readLine
                    eventListener.onException(TCPConnection.this, e);

                } finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });//поток, который будет слушать входящие соединения
        rxThread.start();//запускаем созданный поток
    }
    public synchronized void sendString(String value){
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect () {
         rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
        }
    }
    @Override
    public  String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}

