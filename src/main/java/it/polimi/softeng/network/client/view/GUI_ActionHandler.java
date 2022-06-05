package it.polimi.softeng.network.client.view;

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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

//GUI Controller
public class GUI_ActionHandler implements Initializable, PropertyChangeListener {
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
        this.usernameField.setOnAction(this::saveField);
        this.ipField.setOnAction(this::saveField);
        this.portField.setOnAction(this::saveField);
        this.exitButton.setOnAction(this::closeApp);

        this.testChangeUIButton.setOnAction(this::testChangeUI);
        this.testChangeUIButton.setTooltip(new Tooltip("Click to change UI"));
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
    public void closeWindow(ActionEvent actionEvent) {
        ((Stage)((Button)actionEvent.getSource()).getScene().getWindow()).close();
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
    public Button getClosePopupButton() {
        return this.closePopupButton;
    }
    //TODO add commands from game fxml

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
