package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Handler implements Runnable {

    private final DataInputStream is;
    private final DataOutputStream os;

    public Handler(Socket socket) throws IOException {
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        System.out.println("Client accepted...");
    }

    @Override
    public void run() {
        try {
            while (true) {
                String command = is.readUTF().trim();
                System.out.println("received command: " + command);
                switch (command) {
                    case "getFile": {
                        os.writeUTF("Enter file name...");
                        String fileName = is.readUTF();
                        os.writeUTF("File: " + fileName);
                        break;
                    }
                    case "getListFiles":
                        os.writeUTF("List: {File1, File2, File3}");
                        break;
                    case "putFile": {
                        os.writeUTF("Enter file name");
                        String fileName = is.readUTF();
                        os.writeUTF("Enter file size");
                        long size = is.readLong();
                        os.writeUTF("Upload: file " + fileName + " uploaded, size: " + size);
                        break;
                    }
                }
                os.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}