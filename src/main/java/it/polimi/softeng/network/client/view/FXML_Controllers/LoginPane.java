package it.polimi.softeng.network.client.view.FXML_Controllers;

import it.polimi.softeng.exceptions.ServerCreationException;
import it.polimi.softeng.network.server.Server;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.regex.Pattern;


public class LoginPane extends BorderPane implements Initializable {
    @FXML
    TextField usernameField, ipField, portField;
    @FXML
    Label usernameLabel, ipLabel, portLabel;
    @FXML
    Button joinButton, hostButton;
    private boolean validLogin;
    private static final String DEFAULT_IP="127.0.0.1";
    private static final Integer DEFAULT_PORT=50033;
    private static final String IP_FORMAT="^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.){3}+([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])$";

    public LoginPane() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Assets/GUI/fxml/Login.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        }
        catch (IOException io) {
            io.printStackTrace();
            throw new RuntimeException(io);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.validLogin=false;
        this.joinButton.setOnAction(this::connectToServer);
        this.hostButton.setOnAction(this::hostServer);
        for (TextField textField : Arrays.asList(usernameField,ipField,portField)) {
            textField.prefHeightProperty().bind(heightProperty().divide(20));
            textField.prefWidthProperty().bind(widthProperty().divide(10));
            textField.setAlignment(Pos.CENTER);
        }
        for (Label label : Arrays.asList(usernameLabel,ipLabel,portLabel)) {
            label.prefHeightProperty().bind(heightProperty().divide(20));
            label.prefWidthProperty().bind(widthProperty().divide(5));
            label.setAlignment(Pos.CENTER_LEFT);
            label.setTextAlignment(TextAlignment.LEFT);
        }

    }

    public TextField getUsernameField() {
        return this.usernameField;
    }
    public TextField getIpField() {
        return this.ipField;
    }
    public TextField getPortField() {
        return this.portField;
    }

    private void connectToServer(ActionEvent actionEvent) {
        Alert alert=new Alert(Alert.AlertType.WARNING);
        if (incorrectUsername(alert) || incorrectIP(alert) || incorrectPort(alert)) {
            alert.showAndWait();
            return;
        }
        validLogin=true;
    }
    private void hostServer(ActionEvent actionEvent) {
        Alert alert=new Alert(Alert.AlertType.ERROR);
        if (incorrectUsername(alert) || incorrectPort(alert)) {
            alert.showAndWait();
            return;
        }
        try {
            //tests if server with that port already exists and can be connected to
            new Socket(DEFAULT_IP,Integer.parseInt(portField.getText()));
            alert.setContentText("Server with that port already exists, try joining instead");
            alert.showAndWait();
        }
        catch (IOException io) {
            Thread serverThread=new Thread(()->{
                try {
                    Server server=new Server();
                    server.main(new String[]{portField.getText()});
                }
                catch (ServerCreationException sce) {
                    Platform.runLater(()->{
                        alert.setContentText(sce.getMessage());
                        alert.showAndWait();
                    });
                }
            });
            serverThread.setDaemon(true);
            serverThread.start();
            connectToServer(actionEvent);
        }
    }

    private boolean incorrectUsername(Alert alert) {
        alert.setContentText("Username must be 3-10 characters long and not contain spaces");
        return usernameField.getText().length()<3 || usernameField.getText().length()>10 || usernameField.getText().contains(" ");
    }

    private boolean incorrectIP(Alert alert) {
        alert.setContentText("Invalid ip format");
        if (ipField.getText()==null || ipField.getText().equals("") || ipField.getText().equalsIgnoreCase("local")) {
            ipField.setText(DEFAULT_IP);
            return false;
        }
        return !Pattern.compile(IP_FORMAT).matcher(ipField.getText()).matches();
    }

    private boolean incorrectPort(Alert alert) {
        if (portField.getText()==null || portField.getText().equals("") || portField.getText().equalsIgnoreCase("local")) {
            portField.setText(DEFAULT_PORT.toString());
            return false;
        }
        try {
            int port=Integer.parseInt(portField.getText());
            alert.setContentText("Port range 49152-65535");
            return port<49152 || port>65535;
        }
        catch (IllegalArgumentException iae) {
            alert.setContentText("Port must be a number");
            return true;
        }
    }
    public boolean getValidLogin() {
        return this.validLogin;
    }
    public void invalidateLogin() {
        this.validLogin=false;
    }
}
