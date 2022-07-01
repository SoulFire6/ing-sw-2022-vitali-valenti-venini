package it.polimi.softeng.network.client.view;

import it.polimi.softeng.network.reducedModel.ReducedGame;

import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Objects;

/**
 * This class is the gui implementation of the view abstract class
 */
public class GUI extends Application implements View {
    private static GUI_ActionHandler controller;

    /**
     * Default constructor
     */
    public GUI() {
    }

    /**
     * Inherited from Application, used to start a JavaFx Runtime
     * @param stage the stage where the scene is loaded
     */
    @Override
    public void start(Stage stage) {
        controller=new GUI_ActionHandler();
        Parent root=controller.getRoot();
        stage.setTitle("Eriantys");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/Assets/GUI/Icons/AppIcon.png")).toExternalForm()));
        stage.setScene(new Scene(root));
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
        stage.setOnCloseRequest(event-> {
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * Wrapping launch method inherited from Runnable so that both views can be instantiated in the same way
     */
    @Override
    public void run() {
        System.out.println("STARTING");
        Application.launch();
    }

    /**
     * This method sets up the Socket
     * @param args the arguments to try using as default arguments
     * @return ObjectInputStream the object stream for receiving messages
     */
    @Override
    public ObjectInputStream setUpConnection(String[] args) {
        ObjectInputStream objectInputStream=null;
        while(controller==null) {
            threadSleep(500,"Waiting for controller");
        }
        Platform.runLater(()-> controller.setupLoginParams(args));
        while (objectInputStream==null) {
            while (controller.getSocket()==null) {
                Platform.runLater(()->controller.tryConnection());
                threadSleep(500,"Waiting for socket");
            }
            try {
                objectInputStream=new ObjectInputStream(controller.getSocket().getInputStream());
            }
            catch (IOException io) {
                display(MessageCenter.genMessage(MsgType.ERROR,"","Connection Error","Error connecting to server, try again"));
            }
        }
        return objectInputStream;
    }

    /**
     * This method closes the Socket
     */
    @Override
    public void closeConnection() {
        Platform.runLater(()->controller.closeConnection());
    }
    /**
     * This method displays messages received from server by passing them to the gui
     */
    @Override
    public void display(Message message) {
        while (controller==null) {
            threadSleep(1000,"Waiting for controller");
        }
        Platform.runLater(()-> controller.display(message));
    }

    /**
     * This method sets the GUI controller as the recipient of the model updates instead of itself
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        ReducedGame model=(ReducedGame) evt.getNewValue();
        model.removePropertyChangeListener(this);
        model.addPropertyChangeListener(controller);
        model.notifyGameLoaded();
    }

    /**
     * This method is used for waiting whilst gui is getting set up
     * @param milliseconds the time in between checks
     * @param logMessage the message to display to console
     */
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
