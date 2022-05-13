package it.polimi.softeng.network.client.view;

import it.polimi.softeng.network.message.Message;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GUI extends Application implements View, Runnable {

    private ObjectOutputStream toServer;

    private static GUI_ActionHandler controller;

    private static final ConcurrentLinkedQueue<String> userInputs=new ConcurrentLinkedQueue<>();

    public GUI() {
    }
    public static void main(String[] args) throws InterruptedException {
        new Thread(()-> new GUI().main()).start();
        Thread.sleep(1000);
        Platform.runLater(()->controller.getTextField().setText("HELLO"));

    }
    public void main() {
        System.out.println("STARTING");
        Application.launch();
    }
    @Override
    @FXML
    public void start(Stage stage) throws Exception {
        controller=new GUI_ActionHandler(this,userInputs);
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/fxml/RequestString.fxml"));
        loader.setController(controller);
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
        synchronized (userInputs) {
            while(userInputs.size()==0) {
                System.out.println("Waiting for username...");
                threadSleep(3000);
            }
            String username=userInputs.poll();
            System.out.println("Got username: "+username);
            return username;
        }
    }

    @Override
    public String setIP(String defaultIP) {
        threadSleep(300);
        Platform.runLater(()-> controller.getLabel().setText("Server ip (local or empty for default localhost): "));
        synchronized (userInputs) {
            while(userInputs.size()==0) {
                System.out.println("Waiting for ip address...");
                threadSleep(3000);
            }
            String ip=userInputs.poll();
            if (ip.equals("") || ip.equals("local")) {
                return defaultIP;
            }
            System.out.println("Got ip: "+ip);
            return ip;
        }
    }

    @Override
    public int setPort(int defaultPort) {
        Platform.runLater(()-> controller.getLabel().setText("Server port (local or empty for default 50033): "));
        synchronized (userInputs) {
            while(userInputs.size()==0) {
                System.out.println("Waiting for port...");
                threadSleep(3000);
            }
            try {
                String port=userInputs.poll();
                System.out.println("Got ip: "+port);
                if (port.equals("")) {
                    return defaultPort;
                }
                return Integer.parseInt(port);
            }
            catch (NumberFormatException nfe) {
                System.out.println("Error getting port, value is not a number");
                return 0;
            }
        }
    }

    @Override
    @FXML
    public void display(String message) {
        //Platform.runLater(()-> controller.getLabel().setText(message));
    }

    private void threadSleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        }
        catch (Exception e) {
            System.out.println("Error: woke up too soon");
        }
    }
}
