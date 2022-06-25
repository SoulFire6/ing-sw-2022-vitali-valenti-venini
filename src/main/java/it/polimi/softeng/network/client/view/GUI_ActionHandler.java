package it.polimi.softeng.network.client.view;

import it.polimi.softeng.controller.TurnManager;
import it.polimi.softeng.model.ReducedModel.ReducedGame;
import it.polimi.softeng.network.client.view.FXML_Controllers.LoginVBox;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;
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
    private TurnManager.TurnState currentPhase;
    private String currentPlayer;
    @FXML
    private LoginVBox loginVBox;
    @FXML
    private VBox serverSetupVBox,optionsVBox,gameVBox;
    @FXML
    private Label optionsLabel;
    @FXML
    TextField optionsField;
    @FXML
    private ToolBar assistantCards;
    public GUI_ActionHandler() {
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.loginVBox.setVisible(true);
        this.gameVBox.setVisible(false);
        this.serverSetupVBox.setVisible(false);
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
                toServer.writeObject(MessageCenter.genMessage(MsgType.CONNECT, username, null, null));
                loginVBox.setVisible(false);
                serverSetupVBox.setVisible(true);
            }
            catch (IOException io) {
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
                System.out.println("Message "+message);
                System.out.println("Context "+context);
                ArrayList<Button> buttonOptions=new ArrayList<>();
                optionsLabel.setText(message.substring(!message.contains("]")?0:message.indexOf("]")+2));
                optionsVBox.getChildren().clear();
                if (!context.contains(">")) {
                    optionsField.setVisible(true);
                    optionsField.setOnAction(event->{
                        try {
                            toServer.writeObject(MessageCenter.genMessage(MsgType.TEXT,username, optionsField.getText(),optionsField.getText()));
                            optionsField.setVisible(false);
                        } catch (IOException e) {
                            Alert alert=new Alert(Alert.AlertType.ERROR);
                            alert.setContentText("Error sending message to server");
                            alert.showAndWait();
                        }
                    });
                    break;
                }
                for (String option : context.split("-")) {
                    Button optionButton=new Button(option.contains(" > ")?option.substring(option.indexOf(" > ")+2):option);
                    optionButton.setPrefWidth(200);
                    optionButton.setOnAction(event-> {
                        try {
                            toServer.writeObject(MessageCenter.genMessage(MsgType.TEXT,username, optionButton.getText(),option.contains(" > ")?option.substring(0,option.indexOf(" >")):option));
                            optionsVBox.getChildren().clear();
                        } catch (IOException e) {
                            Alert alert=new Alert(Alert.AlertType.ERROR);
                            alert.setContentText("Error sending message to server");
                            alert.showAndWait();
                        }
                    });
                    buttonOptions.add(optionButton);
                    VBox.setMargin(optionButton,new Insets(10));
                }
                optionsVBox.getChildren().addAll(buttonOptions);
                break;
            case ERROR:
                Alert alert=new Alert(Alert.AlertType.ERROR);
                alert.setContentText(message);
                alert.showAndWait();
                break;
            default:
                if (optionsLabel.isVisible()) {
                    optionsLabel.setText(message);
                }
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
        serverSetupVBox.setVisible(false);
        gameVBox.setVisible(true);
        //assistantCards
        //TODO implement
    }
}
