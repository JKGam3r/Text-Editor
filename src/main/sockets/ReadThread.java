package main.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReadThread extends Thread {
    private BufferedReader reader;

    public ReadThread(Socket socket) {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch(IOException i) {
            i.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String data;

            while(true) {
                data = reader.readLine();
            }
        } catch(IOException i) {
            i.printStackTrace();
        }
    }
}
