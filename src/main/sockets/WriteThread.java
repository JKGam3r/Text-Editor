package main.sockets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class WriteThread extends Thread {
    private PrintWriter writer;

    public WriteThread(Socket socket) {
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch(IOException i) {
            i.printStackTrace();
        }
    }

    @Override
    public void run() {
        long currentTime        = System.currentTimeMillis();
        final float interval    = 0.01f;

        while(true) {
            if((System.currentTimeMillis() - currentTime) / 1_000f >= interval) {
                writer.println("");
                currentTime = System.currentTimeMillis();
            }
        }
    }
}
