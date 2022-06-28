package it.polimi.softeng.network.client.view;

import it.polimi.softeng.model.ReducedModel.ReducedGame;

import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.ObjectInputStream;

public class GUI extends Application implements View {
    private static GUI_ActionHandler controller;

    public GUI() {
    }
    @Override
    public void start(Stage stage) {
        controller=new GUI_ActionHandler();
        Parent root=controller.getRoot();
        stage.setTitle("Eriantys");
        stage.setScene(new Scene(root));
        stage.show();
        stage.setOnCloseRequest(event-> {
            Platform.exit();
            System.exit(0);
        });
    }
    @Override
    public void run() {
        System.out.println("STARTING");
        Application.launch();
    }

    @Override
    public ObjectInputStream setUpConnection(String[] args) {
        while(controller==null) {
            threadSleep(500,"Waiting for controller");
        }
        Platform.runLater(()-> controller.setupLoginParams(args));
        while (controller.getSocket()==null) {
            Platform.runLater(()->controller.tryConnection());
            threadSleep(500,"Waiting for socket");
        }
        try {
            return new ObjectInputStream(controller.getSocket().getInputStream());
        }
        catch (IOException io) {
            display(MessageCenter.genMessage(MsgType.ERROR,"","Connection Error","Error connecting to server, try again"));
            return setUpConnection(args);
        }
    }

    @Override
    public void closeConnection() {
        Platform.runLater(()->controller.closeConnection());
    }

    @Override
    public void display(Message message) {
        while (controller==null) {
            threadSleep(1000,"Waiting for controller");
        }
        Platform.runLater(()-> controller.display(message));
    }

    //GUI makes the fxml controller the property listener instead of itself
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        ReducedGame model=(ReducedGame) evt.getNewValue();
        model.removePropertyChangeListener(this);
        model.addPropertyChangeListener(controller);
        model.notifyGameLoaded();
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
}
