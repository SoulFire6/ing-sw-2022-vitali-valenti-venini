package it.polimi.softeng.network.client.view;

import it.polimi.softeng.exceptions.UpdateGUIException;
import it.polimi.softeng.model.ReducedModel.*;
import it.polimi.softeng.network.client.view.FXML_Controllers.GamePane;
import it.polimi.softeng.network.client.view.FXML_Controllers.LoginVBox;
import it.polimi.softeng.network.client.view.FXML_Controllers.MessageSender;
import it.polimi.softeng.network.message.Info_Message;
import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

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

    private Parent root;
    private Socket socket;

    private MessageSender messageSender;
    @FXML
    private LoginVBox loginVBox;
    @FXML
    private VBox inputVBox;
    @FXML
    private GamePane gameAnchorPane;
    public GUI_ActionHandler() {
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/Assets/GUI/fxml/Eriantys.fxml"));
            loader.setController(this);
            root=loader.load();
        }
        catch (IOException io) {
            io.printStackTrace();
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.loginVBox.setVisible(true);
        this.inputVBox.setVisible(false);
        this.gameAnchorPane.setVisible(false);
    }

    public Parent getRoot() {
        return root;
    }

    public void setupLoginParams(String[] args) {
        TextField textField=this.loginVBox.getUsernameField();
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
        if (this.loginVBox!=null && this.loginVBox.getValidLogin()) {
            try {
                String username=loginVBox.getUsernameField().getText();
                socket=new Socket(loginVBox.getIpField().getText(),Integer.parseInt(loginVBox.getPortField().getText()));
                ObjectOutputStream toServer=new ObjectOutputStream(socket.getOutputStream());
                System.out.println("SENDING");
                toServer.writeObject(MessageCenter.genMessage(MsgType.CONNECT, username, null, null));
                System.out.println("SENT");
                messageSender=new MessageSender(username,toServer);
                gameAnchorPane.setMessageSender(messageSender);
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

    public void display(Message message) {
        switch (message.getSubType()) {
            case INPUT:
                inputVBox.setVisible(true);
                inputVBox.getChildren().clear();
                ArrayList<Button> buttonOptions=new ArrayList<>();
                Label inputLabel=new Label(((Info_Message)message).getInfo());
                inputLabel.setStyle("-fx-background-color: white");
                inputVBox.getChildren().add(inputLabel);
                if (!message.getContext().contains(">")) {
                    TextField optionField=new TextField();
                    optionField.setPrefWidth(200);
                    optionField.setOnAction(event->{
                        messageSender.sendMessage(MsgType.TEXT,optionField.getText(),optionField.getText());
                        inputVBox.getChildren().clear();
                    });
                    inputVBox.getChildren().add(optionField);
                    break;
                }
                for (String option : message.getContext().split("-")) {
                    Button optionButton=new Button(option.contains(" > ")?option.substring(option.indexOf(" > ")+2):option);
                    optionButton.setPrefWidth(200);
                    optionButton.setWrapText(true);
                    optionButton.setOnAction(event-> {
                        messageSender.sendMessage(MsgType.TEXT,optionButton.getText(),option.contains(" > ")?option.substring(0,option.indexOf(" >")):option);
                        inputVBox.getChildren().clear();
                    });
                    buttonOptions.add(optionButton);
                    VBox.setMargin(optionButton,new Insets(10));
                }
                inputVBox.getChildren().addAll(buttonOptions);
                break;
            case CLIENT_NUM:
                gameAnchorPane.addChatMessage(message.getSender(),((Info_Message)message).getInfo(),message.getSubType());
                if (inputVBox.isVisible()) {
                    inputVBox.getChildren().clear();
                    for (String username : message.getContext().substring(message.getContext().indexOf("[")+1, message.getContext().indexOf("]")).split(",")) {
                        Label userLabel=new Label(username);
                        userLabel.prefHeightProperty().bind(root.getScene().heightProperty().divide(4));
                        userLabel.prefWidthProperty().bind(root.getScene().widthProperty().divide(3));
                        userLabel.setStyle("-fx-background-color: white;-fx-font-size: "+userLabel.prefHeightProperty());
                        inputVBox.getChildren().add(userLabel);
                    }
                }
                break;
            case ERROR:
                Alert alert=new Alert(message.getSubType().equals(MsgType.ERROR)?Alert.AlertType.ERROR:Alert.AlertType.INFORMATION);
                alert.setContentText(((Info_Message)message).getInfo());
                alert.showAndWait();
                break;
            case TURNSTATE:
            case WHISPER:
                gameAnchorPane.addChatMessage(message.getSender(),((Info_Message)message).getInfo(),message.getSubType());
                break;
            case CONNECT:
            case TEXT:
                System.out.println(((Info_Message)message).getInfo());
                break;
            default:
                Alert warning=new Alert(Alert.AlertType.WARNING);
                warning.setContentText("Unexpected message type");
                warning.showAndWait();
                break;
        }
    }

    public void closeConnection() {
        try {
            messageSender.closeConnection();
            socket.close();
        }
        catch (IOException ignored) {
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //TODO implement various GUI updates

        //TODO update methods must use message sender
        Platform.runLater(()->{
            try {
                switch (ReducedGame.UpdateType.valueOf(evt.getPropertyName())) {
                    case PLAYERS:
                        for (ReducedPlayer player : (ArrayList<ReducedPlayer>) evt.getNewValue()) {
                            System.out.println("Updating "+player.getName());
                            gameAnchorPane.updatePlayer(player);
                        }
                        System.out.println("UPDATE PLAYERS");
                        break;
                    case PLAYER:
                        //gameAnchorPane.updatePlayer(toServer,username,(ReducedPlayer) evt.getNewValue());
                        System.out.println("FIND AND UPDATE SINGLE PLAYER");
                        break;
                    case TURN_STATE:
                        gameAnchorPane.updateTurnState((ReducedGame)evt.getNewValue());
                        break;
                    case ISLANDS:
                        gameAnchorPane.updateIslands((ArrayList<ReducedIsland>)evt.getNewValue());
                        break;
                    case CLOUDS:
                        gameAnchorPane.updateClouds((ArrayList<ReducedCloud>) evt.getNewValue());
                        break;
                    case COINS:
                        gameAnchorPane.updateCoins((Integer)evt.getNewValue());
                        break;
                    case BAG:
                        gameAnchorPane.updateBag((ReducedBag)evt.getNewValue());
                        break;
                    case CHARACTER_CARDS:
                        gameAnchorPane.updateCharacterCards((ArrayList<ReducedCharacterCard>) evt.getNewValue());
                        break;
                    case LOADED_GAME:
                        inputVBox.setVisible(false);
                        gameAnchorPane.setVisible(true);
                        gameAnchorPane.setupGame((ReducedGame) evt.getNewValue());
                        break;
                    default:
                        Alert alert=new Alert(Alert.AlertType.WARNING);
                        alert.setContentText("Unrecognised update");
                        alert.showAndWait();
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
            catch (ClassCastException cce) {
                Alert alert=new Alert(Alert.AlertType.ERROR);
                alert.setContentText(cce.getMessage());
                alert.showAndWait();
            }
        });
    }
}
