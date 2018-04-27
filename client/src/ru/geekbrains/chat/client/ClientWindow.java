package ru.geekbrains.chat.client;

import ru.geekbrains.network.TCPConnection;
import ru.geekbrains.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {
    private static final String IP_ADDR = "192.168.0.2";//192.168.0.2"; //"89.222.249.131";
    private static final int PORT = 8189;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    private final JTextArea log = new JTextArea(); // поле для ввода
    private final JTextField fieldNickname = new JTextField("Sabrae"); // поле с никнеймом (можно задать по умолчанию)
    private final JTextField fieldInput = new JTextField(); // поле для ввода текста

    private TCPConnection connection;


    private ClientWindow () {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT); // устанавливаем размеры окна
        setLocationRelativeTo(null); // задаем положение окна по середине экрана
        setAlwaysOnTop(true); // окно всегда сверху

        log.setEditable(false); // запрещаем редактирование текста в главном окне
        log.setLineWrap(true); // включаем перенос по словам
        add (log, BorderLayout.CENTER); // добавление поля ввода по центру

        fieldInput.addActionListener(this);// для перехватывания нажатия добавляем себя
        add (fieldInput, BorderLayout.SOUTH); // добавление поля ввода текста внизу
        add (fieldNickname, BorderLayout.NORTH); // добавление поля с ником наверху


        setVisible(true); //сделать окно всегда видимым

        try {
            connection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            printMsg("Connection exception: " + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText(); // полученме строкм
        if (msg.equals("")) return;
        fieldInput.setText(null);
        connection.sendString(fieldNickname.getText() + ": "+ msg); // в поле с ником
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMsg("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMsg(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMsg("Connection close");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMsg("Connection exception: " + e);
    }

    private synchronized void printMsg (String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n"); // ловим строчку + переход на новую строку
                log.setCaretPosition(log.getDocument().getLength());// автоматическое поднятие строчек
            }
        });
    }
}
