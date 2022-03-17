package main.sockets;

import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

public class Server {

    private String hostIP;

    private final int PORT_NUMBER;

    private volatile boolean running;

    private final Set<RequestProcessor> users;

    public Server(int port) {
        PORT_NUMBER = port;

        users = new HashSet<>();

        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 5_000);
            hostIP = socket.getLocalAddress().getHostAddress();
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
    }

    public void runServer() {
        running = true;

        try(ServerSocket server = new ServerSocket(PORT_NUMBER, 10)) {
            while (running) {
                Thread.onSpinWait();

                Socket socket = server.accept();

                RequestProcessor rp = new RequestProcessor(this, socket);
                users.add(rp);
                rp.start();
            }
        } catch(IOException i) {
            i.printStackTrace();
        }
    }

    public void stopServer() {
        running = false;

        for(RequestProcessor user : users)
            user.stopRunning();
    }

    protected synchronized void sendData(String data, RequestProcessor sender) {
        for(RequestProcessor user : users) {
            if(user != sender) {
                user.writeData(data);
            }
        }
    }
}
