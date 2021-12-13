package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller implements Initializable {


    private DataInputStream is;
    private DataOutputStream os;
    public TextField input;
    public TextArea output;

    public void sendMessage(ActionEvent actionEvent) throws IOException {
        os.writeUTF(input.getText());

    }

    private void read() {
        try {
            while (true) {
                String message = is.readUTF();
                output.appendText(message + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
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
}
