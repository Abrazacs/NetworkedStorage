package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException {

        ServerSocket srv = new ServerSocket(8189);
        System.out.println("Server started...");
        while (true) {
            Socket socket = srv.accept();
            Handler handler = new Handler(socket);
            new Thread(handler).start();
        }

    }
}