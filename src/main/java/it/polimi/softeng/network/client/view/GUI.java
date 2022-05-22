package it.polimi.softeng.network.client.view;

import it.polimi.softeng.model.ReducedModel.ReducedGame;
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

    private String username=null;
    private ObjectOutputStream toServer;

    private ReducedGame model=null;

    private static GUI_ActionHandler controller;

    private static final ConcurrentLinkedQueue<Message> userInputs=new ConcurrentLinkedQueue<>();

    public GUI() {
    }
    public static void main(String[] args) throws InterruptedException {
        GUI gui=new GUI();
        new Thread(gui::main).start();
        gui.display("Testing popup");

        Thread.sleep(1000);

    }
    public void main() {
        System.out.println("STARTING");
        Application.launch();
    }
    @Override
    @FXML
    public void start(Stage stage) throws Exception {
        controller=new GUI_ActionHandler(userInputs);
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/fxml/Eriantys.fxml"));
        loader.setController(controller);
        Parent root=loader.load();
        stage.setTitle("Eriantys");
        stage.setScene(new Scene(root));
        stage.show();
        stage.setOnCloseRequest(event-> Platform.exit());
    }
    @Override
    public void run() {
        while (model==null) {
            threadSleep(3000);
            System.out.println("Waiting for model");
        }
        synchronized (model) {
            try {
                while (true) {
                    wait();
                    //update model
                }
            }
            catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            //update gui

        }
        //TODO: runs once model is loaded from server, update view every time model changes
    }

    @Override
    public void setToServer(ObjectOutputStream toServer) {
        this.toServer=toServer;
        //TODO swap out gui from input fields to game
        new Thread(this).start();
    }

    @Override
    public void setModel(ReducedGame model) {
        this.model=model;
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
    public String setUsername() {
        while (controller==null) {
            System.out.println("Waiting for controller");
            threadSleep(3000);
        }
        while (!controller.getUsernameField().isDisabled()) {
            System.out.println("Waiting for username...");
            threadSleep(3000);
        }
        String username=controller.getUsernameField().getText();
        if (username.equals("")) {
            Platform.runLater(()->{
                controller.getUsernameField().setDisable(false);
                controller.getUsernameField().setPromptText("Username must not be empty");
            });
            username=setUsername();
        }
        this.username=username;
        return username;
    }

    @Override
    public String setIP(String defaultIP) {
        while (!controller.getIpField().isDisabled()) {
            System.out.println("Waiting for ip...");
            threadSleep(3000);
        }
        String ip=controller.getIpField().getText();
        if (ip.equals("") || ip.equals("local")) {
            return defaultIP;
        }
        return ip;
    }

    @Override
    public int setPort(int defaultPort) {
        while (!controller.getPortField().isDisabled()) {
            System.out.println("Waiting for port...");
            threadSleep(3000);
        }
        String port=controller.getPortField().getText();
        if (port.equals("") || port.equals("local")) {
            return defaultPort;
        }
        try {
            return Integer.parseInt(port);
        }
        catch (NumberFormatException nfe) {
            Platform.runLater(()->{
                controller.getPortField().clear();
                controller.getPortField().setDisable(false);
                controller.getPortField().setPromptText("Port must be a number");
            });
            return setPort(defaultPort);
        }
    }

    @Override
    public void display(String message) {
        while (controller==null) {
            System.out.println("Waiting for controller");
            threadSleep(2000);
        }
        Platform.runLater(()-> {
            try {
                FXMLLoader popupLoader=new FXMLLoader(getClass().getResource("/fxml/Display.fxml"));
                popupLoader.setController(controller);
                Parent popupRoot=popupLoader.load();
                Stage popupStage=new Stage();
                popupStage.setScene(new Scene(popupRoot));
                while (controller.getPopupLabel()==null) {
                    System.out.println("Waiting for popup label");
                    threadSleep(2000);
                }
                controller.getClosePopupButton().setOnAction(controller::closeWindow);
                controller.getPopupLabel().setText(message);
                popupStage.show();

            }
            catch (IOException io) {
                io.printStackTrace();
            }
        });
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
