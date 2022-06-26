package it.polimi.softeng.network.client.view.FXML_Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;


public class LoginVBox extends VBox {
    @FXML
    TextField usernameField, ipField, portField;
    @FXML
    Label usernameLabel, ipLabel, portLabel;
    @FXML
    Button joinButton;
    private boolean validLogin;
    private static final String DEFAULT_IP="127.0.0.1";
    private static final Integer DEFAULT_PORT=50033;
    private static final String IP_FORMAT="^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.){3}+([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])$";

    public LoginVBox() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Assets/GUI/fxml/LoginVBox.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
            this.validLogin=false;
            this.joinButton.setOnAction(this::connectToServer);
            for (TextField textField : Arrays.asList(usernameField,ipField,portField)) {
                textField.prefHeightProperty().bind(this.heightProperty().divide(20));
                textField.prefWidthProperty().bind(this.widthProperty().divide(10));
                textField.setAlignment(Pos.CENTER);
            }
            for (Label label : Arrays.asList(usernameLabel,ipLabel,portLabel)) {
                label.prefHeightProperty().bind(this.heightProperty().divide(20));
                label.prefWidthProperty().bind(this.widthProperty().divide(5));
                label.setAlignment(Pos.CENTER_LEFT);
                label.setTextAlignment(TextAlignment.LEFT);
            }
        }
        catch (IOException io) {
            io.printStackTrace();
            throw new RuntimeException(io);
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

    private void connectToServer(javafx.event.ActionEvent actionEvent) {
        Pattern pattern=Pattern.compile(IP_FORMAT);
        Alert alert=new Alert(Alert.AlertType.WARNING);
        if (usernameField.getText().length()<3 || usernameField.getText().length()>10 || usernameField.getText().contains(" ")) {
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
        validLogin=true;
    }
    public boolean getValidLogin() {
        return this.validLogin;
    }
    public void invalidateLogin() {
        this.validLogin=false;
    }
}
