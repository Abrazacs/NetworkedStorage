package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;


public class Controller implements Initializable {

    private Path baseDir;
    private DataInputStream is;
    private DataOutputStream os;
    public ListView<String> clientFiles;
    public ListView<String> serverFiles;

    private void read() {
        try {
            while (true) {
                String command = is.readUTF();
                if (command.equals("#list#")) {
                    int fileCount = is.readInt();
                    Platform.runLater(() -> serverFiles.getItems().clear());
                    for (int i = 0; i < fileCount; i++) {
                        String name = is.readUTF();
                        Platform.runLater(() -> serverFiles.getItems().add(name));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getFileNames() throws IOException {
        return Files.list(baseDir)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            baseDir = Paths.get(System.getProperty("user.home"));
            clientFiles.getItems().addAll(getFileNames());
            Socket socket = new Socket("localhost", 8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            Thread thread = new Thread(this::read);
            thread.setDaemon(true);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        String file = clientFiles.getSelectionModel().getSelectedItem();
        Path filePath = baseDir.resolve(file);
        os.writeUTF("#upload#");
        os.writeUTF(file);
        os.writeLong(Files.size(filePath));
        os.write(Files.readAllBytes(filePath));
        os.flush();
    }

//    public void download(ActionEvent actionEvent) {
//        String file = serverFiles.getSelectionModel().getSelectedItem();
//
//    }
}
