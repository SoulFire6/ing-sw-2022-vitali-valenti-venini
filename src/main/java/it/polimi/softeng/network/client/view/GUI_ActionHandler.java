package it.polimi.softeng.network.client.view;

import it.polimi.softeng.controller.TurnManager;
import it.polimi.softeng.model.ReducedModel.ReducedGame;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

//GUI Controller
public class GUI_ActionHandler implements Initializable, PropertyChangeListener {

    private static String username;
    private static Socket socket;
    private ObjectOutputStream toServer;
    private TurnManager.TurnState currentPhase;
    private String currentPlayer;
    private static final String DEFAULT_IP="127.0.0.1";
    private static final Integer DEFAULT_PORT=50033;
    private static final String IP_FORMAT="^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.){3}+([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])$";
    @FXML
    private Pane setupVBox,serverSetup,gameVBox;
    @FXML
    private TextField usernameField,ipField,portField;
    @FXML
    private Button joinButton,exitButton,createLobbyButton,joinLobbyButton,disconnectButton;
    @FXML
    private ToolBar assistantCards;


    public GUI_ActionHandler() {
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.joinButton.setOnAction(this::checkConnectionParams);
        //this.exitButton.setOnAction();

        this.setupVBox.setVisible(true);
        this.gameVBox.setVisible(false);
        this.serverSetup.setVisible(false);
    }

    public void setupLoginParams(String[] args) {
        if (usernameField.getText().isEmpty()) {
            usernameField.setText(args[0]);
        }
        if (ipField!=null && ipField.getText().isEmpty()) {
            ipField.setText(args[1]);
        }
        if (portField!=null && portField.getText().isEmpty()) {
            portField.setText(args[2]);
        }
    }

    public void checkConnectionParams(ActionEvent actionEvent) {
        Pattern pattern=Pattern.compile(IP_FORMAT);
        Alert alert=new Alert(Alert.AlertType.WARNING);
        username=usernameField.getText();
        if (username.length()<3 || username.length()>10 || username.contains(" ")) {
            alert.setContentText("Username must be 3-10 characters long and not contain spaces");
            alert.showAndWait();
            return;
        }
        if (ipField.getText()==null || ipField.getText().equals("") || ipField.getText().equalsIgnoreCase("local")) {
            ipField.setText(DEFAULT_IP);
        }
        if (!pattern.matcher(ipField.getText()).matches()) {
            alert.setContentText("Invalid ip format");
            alert.showAndWait();
            return;
        }
        if (portField.getText()==null || portField.getText().equals("") || portField.getText().equalsIgnoreCase("local")) {
            portField.setText(DEFAULT_PORT.toString());
        }
        try {
            int port=Integer.parseInt(portField.getText());
            if (port<49152 || port>65535) {
                alert.setContentText("Port range 49152-65535");
                alert.showAndWait();
                return;
            }
        }
        catch (IllegalArgumentException iae) {
            alert.setContentText("Port must be a number");
            alert.showAndWait();
            return;
        }
        try {
            socket=new Socket(ipField.getText(),Integer.parseInt(portField.getText()));
            toServer=new ObjectOutputStream(socket.getOutputStream());
            toServer.writeObject(MessageCenter.genMessage(MsgType.CONNECT, username, null, null));
            setupVBox.setVisible(false);
            serverSetup.setVisible(true);
        }
        catch (IOException io) {
            alert.setContentText("Error connecting to server, try again");
            alert.showAndWait();
        }
    }
    public Socket getSocket() {
        return socket;
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
        switch (evt.getPropertyName().toUpperCase()) {
            case "PLAYERS":
                System.out.println("UPDATE PLAYERS");
                break;
            case "PLAYER":
                System.out.println("FIND AND UPDATE SINGLE PLAYER");
                break;
            case "TURN STATE":
                ReducedGame model=((ReducedGame)evt.getNewValue());
                currentPhase=model.getCurrentPhase();
                currentPlayer=model.getCurrentPlayer();
                assistantCards.setVisible(currentPhase == TurnManager.TurnState.ASSISTANT_CARDS_PHASE);
                break;
            default:
                setupGame((ReducedGame)evt.getNewValue());
                System.out.println("LOAD FULL MODEL");
                break;
        }
    }
    private void setupGame(ReducedGame model) {
        serverSetup.setVisible(false);
        gameVBox.setVisible(true);
        //assistantCards
        //TODO implement
    }
}
