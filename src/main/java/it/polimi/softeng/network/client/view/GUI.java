package it.polimi.softeng.network.client.view;

import it.polimi.softeng.model.ReducedModel.ReducedGame;
import it.polimi.softeng.network.message.Info_Message;
import it.polimi.softeng.network.message.Message;

import it.polimi.softeng.network.message.MsgType;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
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
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        GUI gui=new GUI();
        new Thread(gui).start();
        ObjectInputStream fromServer=gui.setUpConnection(new String[]{"cal","local","local"});
        Message inMessage;
        while ((inMessage=(Message) fromServer.readObject())!=null) {
            System.out.println(((Info_Message)inMessage).getInfo());
        }
        gui.display("Testing popup",MsgType.ERROR);
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
    public ObjectInputStream setUpConnection(String[] args) {
        while(controller==null) {
            threadSleep(500,"Waiting for controller");
        }
        Platform.runLater(()-> controller.setupLoginParams(args));
        while (controller.getSocket()==null) {
            threadSleep(2000,"Waiting for socket");
        }
        try {
            return new ObjectInputStream(controller.getSocket().getInputStream());
        }
        catch (IOException io) {
            display("Error connecting to server, try again",MsgType.ERROR);
            return setUpConnection(args);
        }
    }

    @Override
    public void closeConnection() {
        Platform.runLater(()->controller.closeConnection());
    }

    @Override
    public void display(String message, MsgType displayType) {
        //TODO ADD OTHER DISPLAY TYPES
        while (controller==null) {
            threadSleep(1000,"Waiting for controller");
        }
        switch (displayType) {
            case CONNECT:
                Platform.runLater(()->controller.testChangeUI(null));
                break;
            default:
                Platform.runLater(()-> {
                    Alert alert = new Alert(Alert.AlertType.NONE);
                    alert.setContentText(message);
                    switch (displayType) {
                        case ERROR:
                            alert.setAlertType(Alert.AlertType.ERROR);
                            break;
                        default:
                            alert.setAlertType(Alert.AlertType.INFORMATION);
                            break;
                    }
                    alert.showAndWait();
                });
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
