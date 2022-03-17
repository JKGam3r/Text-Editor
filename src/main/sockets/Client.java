package main.sockets;

import java.io.IOException;
import java.net.Socket;

public class Client {
    private final String HOST_IP;

    private final int PORT_NUMBER;

    public Client(String HOST_IP, int port) {
        this.HOST_IP = HOST_IP;
        PORT_NUMBER = port;
    }

    public void startClient() {
        try {
            Socket socket = new Socket(HOST_IP, PORT_NUMBER);

            new ReadThread(socket).start();
            new WriteThread(socket).start();
        } catch(IOException i) {
            i.printStackTrace();
        }
    }
}
