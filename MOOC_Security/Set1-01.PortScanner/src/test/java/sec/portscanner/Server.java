package sec.portscanner;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {

    int port;
    boolean waiting = false;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(this.port);
        } catch (IOException ex) {
            throw new RuntimeException("Issues with starting a server that would be scanned at port " + this.port, ex);
        }

        this.waiting = true;
        Socket clientSocket;
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException ex) {
            throw new RuntimeException("Issues with listening and waiting for a connection (port " + this.port + ").", ex);
        }

        try {
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Issues with closing an existing connection.", ex);
        }

    }
}
