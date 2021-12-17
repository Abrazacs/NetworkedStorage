package server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Handler implements Runnable {

    private static final int BUFFER_SIZE = 8192;

    private Path currentDir;
    private byte[] buffer;
    private final DataInputStream is;
    private final DataOutputStream os;

    public Handler(Socket socket) throws IOException {
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        currentDir = Paths.get("serverFiles");
        sendServerFiles();
        System.out.println("Client accepted...");
        buffer = new byte[BUFFER_SIZE];
    }

    private void sendServerFiles() throws IOException {
        os.writeUTF("#list#");
        List<String> names = getFileNames();
        os.writeInt(names.size());
        for (String name : names) {
            os.writeUTF(name);
        }
        os.flush();
    }

    private List<String> getFileNames() throws IOException {
        return Files.list(currentDir)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
    }

    @Override
    public void run() {
        try {
            while (true) {
                String command = is.readUTF();
                System.out.println("received command: " + command);
                if (command.equals("#upload#")) {
                    String fileName = is.readUTF();
                    long size = is.readLong();
                    try (FileOutputStream fos = new FileOutputStream(
                            currentDir.resolve(fileName).toFile())) {
                        for (int i = 0; i < (size + BUFFER_SIZE - 1) / BUFFER_SIZE; i++) {
                            int read = is.read(buffer);
                            fos.write(buffer, 0, read);
                        }
                    }
                    sendServerFiles();
                } else if (command.equals("#download#")){
                    String file = is.readUTF();
                    Path filePath = currentDir.resolve(file);
                    os.writeLong(Files.size(filePath));
                    os.write(Files.readAllBytes(filePath));
                    os.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}