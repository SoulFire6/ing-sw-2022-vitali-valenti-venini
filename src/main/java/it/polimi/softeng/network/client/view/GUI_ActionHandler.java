package it.polimi.softeng.network.client.view;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextField;
import org.w3c.dom.Text;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentLinkedQueue;

//GUI Controller
public class GUI_ActionHandler implements Initializable {

    private final GUI gui;
    private final ConcurrentLinkedQueue<String> userInputs;

    @FXML
    private Label label;
    @FXML
    private TextField textField;
    @FXML
    private Button button;


    public GUI_ActionHandler(GUI gui,ConcurrentLinkedQueue<String> userInputs) {
        this.gui=gui;
        this.userInputs=userInputs;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.textField.setOnAction(this::saveAndClear);
        this.button.setOnAction(this::closeApp);
    }

    public void saveAndClear(ActionEvent actionEvent) {
        userInputs.add(textField.getText());
        System.out.println(textField.getCharacters().toString());
        textField.clear();
    }
    public void closeApp(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    public TextField getTextField() {
        return this.textField;
    }
    public Label getLabel() {
        return this.label;
    }
}
