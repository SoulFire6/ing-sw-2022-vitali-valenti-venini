package it.polimi.softeng.network.client.view;

import it.polimi.softeng.controller.TurnManager;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
    private VBox setupVBox,gameVBox;
    @FXML
    private Pane serverSetup;
    @FXML
    private TextField usernameField,ipField,portField;
    @FXML
    private Button exitButton, closePopupButton;
    @FXML
    private Label popupLabel;
    @FXML
    private Button testChangeUIButton,toggleHandButton;
    @FXML
    private ToolBar assistantCards;


    public GUI_ActionHandler() {
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.exitButton.setOnAction(this::checkConnectionParams);

        this.testChangeUIButton.setOnAction(this::testChangeUI);
        this.testChangeUIButton.setTooltip(new Tooltip("Click to change UI"));
        this.toggleHandButton.setOnAction(this::testToggleHand);

        this.setupVBox.setVisible(true);
        this.gameVBox.setVisible(false);
        this.serverSetup.setVisible(false);
    }

    //TODO remove test method
    public void testChangeUI(ActionEvent actionEvent) {
        ArrayList<Pane> displays=new ArrayList<>(Arrays.asList(setupVBox,gameVBox,serverSetup));
        for (Pane curr : displays) {
            if (curr.isVisible()) {
                Pane next=displays.get((displays.indexOf(curr)+1)%displays.size());
                curr.setVisible(false);
                next.setVisible(true);
                System.out.println("Switching from "+curr.getId()+" to "+next.getId()+": "+((!curr.isVisible() && next.isVisible())?"SUCCESS":"ERROR"));
                break;
            }
        }
    }
    private void testToggleHand(ActionEvent actionEvent) {
        assistantCards.setVisible(!assistantCards.isVisible());
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
    public void closeApp(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }
    public void closeWindow(ActionEvent actionEvent) {
        ((Stage)((Button)actionEvent.getSource()).getScene().getWindow()).close();
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
                System.out.println("UPDATE TURN STATE");
                break;
            default:
                System.out.println("LOAD FULL MODEL");
                break;
        }
    }
}
