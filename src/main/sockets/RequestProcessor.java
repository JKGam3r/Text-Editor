package main.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class RequestProcessor extends Thread {
    private BufferedReader reader;

    private boolean running;

    private final Server server;

    private final Socket socket;

    private PrintWriter writer;

    public RequestProcessor(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch(IOException i) {
            i.printStackTrace();
        }
    }

    @Override
    public void run() {
        running = true;

        try {
            while(running) {
                server.sendData(reader.readLine(), this);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRunning() {
        running = false;
    }

    public void writeData(String data) {
        writer.println(data);
    }
}
