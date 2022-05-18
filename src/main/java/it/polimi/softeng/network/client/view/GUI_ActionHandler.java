package it.polimi.softeng.network.client.view;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentLinkedQueue;

//GUI Controller
public class GUI_ActionHandler implements Initializable {

    private final ConcurrentLinkedQueue<String> userInputs;

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


    public GUI_ActionHandler(ConcurrentLinkedQueue<String> userInputs) {
        this.userInputs=userInputs;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.usernameField.setOnAction(this::saveField);
        this.ipField.setOnAction(this::saveField);
        this.portField.setOnAction(this::saveField);
        this.exitButton.setOnAction(this::closeApp);
        this.testChangeUIButton.setOnAction(this::testChangeUI);
        this.toggleHandButton.setOnAction(this::testToggleHand);

        this.setupVBox.setVisible(true);
        this.gameVBox.setVisible(false);
        this.serverSetup.setVisible(false);
    }

    //TODO remove test method
    private void testChangeUI(ActionEvent actionEvent) {
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
    public Label getPopupLabel() {
        return this.popupLabel;
    }
    //TODO add commands from game fxml
}
