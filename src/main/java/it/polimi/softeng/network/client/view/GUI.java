package it.polimi.softeng.network.client.view;

import it.polimi.softeng.network.message.Message;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.FutureTask;

public class GUI extends Application implements View, Runnable {

    private ObjectOutputStream toServer;
    @FXML
    private TextField textField;
    @FXML
    private Button button;
    @FXML
    private Label label;

    private static final ConcurrentLinkedQueue<String> userInputs=new ConcurrentLinkedQueue<>();

    public GUI() {
    }
    public void main() {
        System.out.println("STARTING");
        Application.launch();
    }
    @Override
    @FXML
    public void start(Stage stage) throws Exception {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/fxml/RequestString.fxml"));
        Parent root=loader.load();
        stage.setTitle("Eriantys");
        stage.setScene(new Scene(root));
        stage.show();
        stage.setOnCloseRequest(event-> Platform.exit());
    }
    @Override
    public void run() {
        //TODO: runs once model is loaded from server, update view every time model changes
    }

    @Override
    public void setToServer(ObjectOutputStream toServer) {
        this.toServer=toServer;
    }

    @Override
    public void sendMessage(Message message) {
        try {
            toServer.writeObject(message);
            toServer.flush();
            toServer.reset();
        }
        catch (IOException io) {
            display("Could not send message");
        }
    }

    @Override
    @FXML
    public String setUsername() {
        //TODO finish
        FutureTask<String> task=new FutureTask<>(()->{
            label.setText("Username: ");
            synchronized (userInputs) {
                while (userInputs.size() == 0) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
                System.out.println("DONE");
                return userInputs.poll();
            }
        });
        Platform.runLater(task);
        try {
            return task.get();
        }
        catch (Exception e) {
            System.out.println("Error getting username");
            return null;
        }
    }

    @Override
    public String setIP(String defaultIP) {
        /*
        synchronized (userInputs) {
            while (userInputs.size() == 0) {

            }
            return userInputs.poll();
        }
         */
        return null;
    }

    @Override
    public int setPort(int defaultPort) {
        /*
        synchronized (userInputs) {
            while (userInputs.size() == 0) {

            }
            return userInputs.poll();
        }
         */
        return 0;
    }

    @Override
    @FXML
    public void display(String message) {
        textField.setText(message);
    }

    public synchronized void saveAndClear(ActionEvent actionEvent) {
        userInputs.add(textField.getText());
        System.out.println(textField.getCharacters().toString());
        textField.clear();
    }

    public void closeApp(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }
}
