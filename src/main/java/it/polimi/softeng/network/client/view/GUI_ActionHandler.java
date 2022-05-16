package it.polimi.softeng.network.client.view;

import it.polimi.softeng.network.message.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentLinkedQueue;

//GUI Controller
public class GUI_ActionHandler implements Initializable {

    private final ConcurrentLinkedQueue<String> userInputs;

    @FXML
    private TextField usernameField,ipField,portField;
    @FXML
    private Button button;


    public GUI_ActionHandler(ConcurrentLinkedQueue<String> userInputs) {
        this.userInputs=userInputs;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.usernameField.setOnAction(this::saveField);
        this.ipField.setOnAction(this::saveField);
        this.portField.setOnAction(this::saveField);
        this.button.setOnAction(this::closeApp);
    }

    public void saveField(ActionEvent actionEvent) {
        TextField trigger=(TextField) actionEvent.getSource();
        System.out.println(trigger.getId()+": "+trigger.getText());
        trigger.setDisable(true);
    }
    public void closeApp(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
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
    //TODO add commands from game fxml
}
