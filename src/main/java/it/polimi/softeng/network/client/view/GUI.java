package it.polimi.softeng.network.client.view;

import it.polimi.softeng.model.ReducedModel.ReducedGame;
import it.polimi.softeng.network.message.Message;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class GUI extends Application implements View {

    private String username=null;
    private ObjectOutputStream toServer;

    private static GUI_ActionHandler controller;

    public GUI() {
    }
    public static void main(String[] args) throws InterruptedException {
        GUI gui=new GUI();
        new Thread(gui).start();
        gui.display("Testing popup");

        Thread.sleep(1000);

    }
    @Override
    public void start(Stage stage) throws Exception {
        controller=new GUI_ActionHandler();
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
        System.out.println("STARTING");
        Application.launch();
    }

    @Override
    public void setToServer(ObjectOutputStream toServer) {
        this.toServer=toServer;
        //TODO swap out gui from input fields to game
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
            threadSleep(3000,"Waiting for controller");
        }
        while (!controller.getUsernameField().isDisabled()) {
            threadSleep(3000,"Waiting for username...");
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
            threadSleep(3000,"Waiting for ip...");
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
            threadSleep(3000,"Waiting for port...");
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
            threadSleep(2000,"Waiting for controller");
        }
        Platform.runLater(()-> {
            try {
                FXMLLoader popupLoader=new FXMLLoader(getClass().getResource("/fxml/Display.fxml"));
                popupLoader.setController(controller);
                Parent popupRoot=popupLoader.load();
                Stage popupStage=new Stage();
                popupStage.setScene(new Scene(popupRoot));
                while (controller.getPopupLabel()==null) {
                    threadSleep(2000,"Waiting for popup label");
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
    private void threadSleep(int milliseconds, String logMessage) {
        try {
            System.out.println(logMessage);
            Thread.sleep(milliseconds);
        }
        catch (Exception e) {
            System.out.println("Error: woke up too soon");
        }
    }

    //GUI makes the fxml controller the property listener instead of itself
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        ReducedGame model=(ReducedGame) evt.getNewValue();
        model.removePropertyChangeListener(this);
        model.addPropertyChangeListener(controller);
        model.notifyGameLoaded();
    }
}
