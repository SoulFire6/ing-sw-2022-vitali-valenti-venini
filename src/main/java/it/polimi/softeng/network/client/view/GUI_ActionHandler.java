package it.polimi.softeng.network.client.view;

import it.polimi.softeng.controller.TurnManager;
import it.polimi.softeng.exceptions.UpdateGUIException;
import it.polimi.softeng.model.ReducedModel.ReducedGame;
import it.polimi.softeng.network.client.view.FXML_Controllers.GameAnchorPane;
import it.polimi.softeng.network.client.view.FXML_Controllers.LoginVBox;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

//GUI Controller
public class GUI_ActionHandler implements Initializable, PropertyChangeListener {
    private String username;
    private Socket socket;
    private ObjectOutputStream toServer;
    @FXML
    private LoginVBox loginVBox;
    @FXML
    private VBox inputVBox;
    @FXML
    private GameAnchorPane gameAnchorPane;
    public GUI_ActionHandler() {
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.loginVBox.setVisible(true);
        this.inputVBox.setVisible(false);
        this.gameAnchorPane.setVisible(false);
    }

    public void setupLoginParams(String[] args) {
        TextField textField=loginVBox.getUsernameField();
        if (textField!=null && textField.getText().isEmpty()) {
            textField.setText(args[0]);
        }
        textField=loginVBox.getIpField();
        if (textField!=null && textField.getText().isEmpty()) {
            textField.setText(args[1]);
        }
        textField=loginVBox.getPortField();
        if (textField!=null && textField.getText().isEmpty()) {
            textField.setText(args[2]);
        }
    }

    public void tryConnection() {
        if (loginVBox.getValidLogin()) {
            try {
                username=loginVBox.getUsernameField().getText();
                socket=new Socket(loginVBox.getIpField().getText(),Integer.parseInt(loginVBox.getPortField().getText()));
                toServer=new ObjectOutputStream(socket.getOutputStream());
                System.out.println("SENDING");
                toServer.writeObject(MessageCenter.genMessage(MsgType.CONNECT, username, null, null));
                System.out.println("SENT");
                loginVBox.setVisible(false);
                inputVBox.setVisible(true);
            }
            catch (IOException io) {
                loginVBox.invalidateLogin();
                Alert alert=new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Error connecting to server");
                alert.showAndWait();
            }
        }
    }
    public Socket getSocket() {
        return socket;
    }

    public void display(String message, String context, MsgType type) {
        //TODO add other display types
        //TODO add style
        switch (type) {
            case INPUT:
                inputVBox.setVisible(true);
                inputVBox.getChildren().clear();
                System.out.println("Message "+message);
                System.out.println("Context "+context);
                ArrayList<Button> buttonOptions=new ArrayList<>();
                Label inputLabel=new Label(message.substring(!message.contains("]")?0:message.indexOf("]")+2));
                inputLabel.setStyle("-fx-background-color: white");
                inputVBox.getChildren().add(inputLabel);
                if (!context.contains(">")) {
                    TextField optionField=new TextField();
                    optionField.setOnAction(event->{
                        try {
                            toServer.writeObject(MessageCenter.genMessage(MsgType.TEXT,username, optionField.getText(),optionField.getText()));
                            inputVBox.getChildren().clear();
                        } catch (IOException e) {
                            Alert alert=new Alert(Alert.AlertType.ERROR);
                            alert.setContentText("Error sending message to server");
                            alert.showAndWait();
                        }
                    });
                    inputVBox.getChildren().add(optionField);
                    break;
                }
                for (String option : context.split("-")) {
                    Button optionButton=new Button(option.contains(" > ")?option.substring(option.indexOf(" > ")+2):option);
                    optionButton.setPrefWidth(200);
                    optionButton.setWrapText(true);
                    optionButton.setOnAction(event-> {
                        try {
                            toServer.writeObject(MessageCenter.genMessage(MsgType.TEXT,username, optionButton.getText(),option.contains(" > ")?option.substring(0,option.indexOf(" >")):option));
                            inputVBox.getChildren().clear();
                        } catch (IOException e) {
                            Alert alert=new Alert(Alert.AlertType.ERROR);
                            alert.setContentText("Error sending message to server");
                            alert.showAndWait();
                        }
                    });
                    buttonOptions.add(optionButton);
                    VBox.setMargin(optionButton,new Insets(10));
                }
                inputVBox.getChildren().addAll(buttonOptions);
                break;
            case ERROR:
                Alert alert=new Alert(Alert.AlertType.ERROR);
                alert.setContentText(message);
                alert.showAndWait();
                break;
            default:
                //TODO add chat messages from server and other players while in game
                break;
        }
    }

    public void closeConnection() {
        try {
            toServer.close();
            socket.close();
        }
        catch (IOException ignored) {
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //TODO implement various GUI updates
        Platform.runLater(()->{
            try {
                switch (evt.getPropertyName().toUpperCase()) {
                    case "PLAYERS":
                        System.out.println("UPDATE PLAYERS");
                        break;
                    case "PLAYER":
                        System.out.println("FIND AND UPDATE SINGLE PLAYER");
                        break;
                    case "TURN STATE":
                        gameAnchorPane.updateTurnState((ReducedGame)evt.getNewValue());
                        break;
                    default:
                        inputVBox.setVisible(false);
                        gameAnchorPane.setVisible(true);
                        gameAnchorPane.setupGame(toServer,username,(ReducedGame) evt.getNewValue());
                        System.out.println("LOAD FULL MODEL");
                        break;
                }
            }
            catch (UpdateGUIException uge) {
                Alert alert=new Alert(Alert.AlertType.ERROR);
                alert.setContentText(uge.getMessage());
                alert.showAndWait();
                alert.setOnCloseRequest(event->{
                    Platform.exit();
                    System.exit(-1);
                });
            }
        });
    }
}
