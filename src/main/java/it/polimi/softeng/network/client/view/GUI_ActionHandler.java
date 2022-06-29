package it.polimi.softeng.network.client.view;

import it.polimi.softeng.exceptions.UpdateGUIException;
import it.polimi.softeng.model.ReducedModel.*;
import it.polimi.softeng.network.client.view.FXML_Controllers.GamePane;
import it.polimi.softeng.network.client.view.FXML_Controllers.LoginPane;
import it.polimi.softeng.network.client.view.FXML_Controllers.MessageSender;
import it.polimi.softeng.network.message.Info_Message;
import it.polimi.softeng.network.message.Message;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

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
    BorderPane mainPane;
    @FXML
    private LoginPane loginPane;
    @FXML
    private VBox inputPane;
    @FXML
    private GamePane gamePane;
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
        this.loginPane.setVisible(true);
        this.inputPane.setVisible(false);
        this.inputPane.setManaged(false);
        this.gamePane.setVisible(false);
        this.gamePane.setManaged(false);
        this.mainPane.getCenter().minHeight(600);
        this.mainPane.getCenter().minWidth(800);

    }

    public Parent getRoot() {
        return root;
    }

    public void setupLoginParams(String[] args) {
        TextField textField=this.loginPane.getUsernameField();
        if (textField!=null && textField.getText().isEmpty()) {
            textField.setText(args[0]);
        }
        textField=loginPane.getIpField();
        if (textField!=null && textField.getText().isEmpty()) {
            textField.setText(args[1]);
        }
        textField=loginPane.getPortField();
        if (textField!=null && textField.getText().isEmpty()) {
            textField.setText(args[2]);
        }
    }

    public void tryConnection() {
        if (this.loginPane!=null && this.loginPane.getValidLogin()) {
            try {
                String username=loginPane.getUsernameField().getText();
                socket=new Socket(loginPane.getIpField().getText(),Integer.parseInt(loginPane.getPortField().getText()));
                messageSender=new MessageSender(username,new ObjectOutputStream(socket.getOutputStream()));
                gamePane.setMessageSender(messageSender);
                loginPane.setVisible(false);
                loginPane.setManaged(false);
                inputPane.setVisible(true);
                inputPane.setManaged(true);
            }
            catch (IOException io) {
                loginPane.invalidateLogin();
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
                inputPane.getChildren().clear();
                ArrayList<Button> buttonOptions=new ArrayList<>();
                Label inputLabel=new Label(((Info_Message)message).getInfo());
                inputLabel.setStyle("-fx-background-color: white");
                inputPane.getChildren().add(inputLabel);
                if (!message.getContext().contains(">")) {
                    TextField optionField=new TextField();
                    optionField.setPrefWidth(200);
                    optionField.setOnAction(event->{
                        messageSender.sendMessage(MsgType.TEXT,optionField.getText(),optionField.getText());
                        inputPane.getChildren().clear();
                    });
                    inputPane.getChildren().add(optionField);
                    inputPane.setVisible(true);
                    inputPane.setManaged(true);
                    break;
                }
                for (String option : message.getContext().split("-")) {
                    Button optionButton=new Button(option.contains(" > ")?option.substring(option.indexOf(" > ")+2):option);
                    optionButton.setPrefWidth(200);
                    optionButton.setWrapText(true);
                    optionButton.setOnAction(event-> {
                        messageSender.sendMessage(MsgType.TEXT,optionButton.getText(),option.contains(" > ")?option.substring(0,option.indexOf(" >")):option);
                        inputPane.getChildren().clear();
                    });
                    buttonOptions.add(optionButton);
                    VBox.setMargin(optionButton,new Insets(10));
                }
                inputPane.getChildren().addAll(buttonOptions);
                break;
            case CLIENT_NUM:
                gamePane.addChatMessage(message.getSender(),((Info_Message)message).getInfo(),message.getSubType());
                if (inputPane.isVisible()) {
                    inputPane.getChildren().clear();
                    if (message.getContext().contains("]")) {
                        for (String username : message.getContext().substring(message.getContext().indexOf("[")+1, message.getContext().indexOf("]")).split(",")) {
                            Button clientButton=new Button(username);
                            clientButton.setTextAlignment(TextAlignment.CENTER);
                            clientButton.prefHeightProperty().bind(root.getScene().heightProperty().divide(20));
                            clientButton.prefWidthProperty().bind(clientButton.heightProperty().multiply(8));
                            clientButton.setWrapText(true);
                            clientButton.setStyle("-fx-background-color: white;-fx-font-size: "+clientButton.prefHeightProperty());
                            VBox.setMargin(clientButton,new Insets(10));
                            inputPane.getChildren().add(clientButton);
                        }
                    } else {
                        new Alert(Alert.AlertType.INFORMATION,"Connected players: "+message.getContext()).showAndWait();
                    }
                    Button disconnectButton=new Button("Disconnect");
                    disconnectButton.setTextAlignment(TextAlignment.CENTER);
                    disconnectButton.prefHeightProperty().bind(root.getScene().heightProperty().divide(20));
                    disconnectButton.prefWidthProperty().bind(disconnectButton.heightProperty().multiply(8));
                    disconnectButton.setOnAction(event-> {
                        messageSender.sendMessage(MsgType.DISCONNECT,"Disconnecting",null);
                        closeConnection();
                        Platform.exit();
                    });
                    VBox.setMargin(disconnectButton,new Insets(10));
                    inputPane.getChildren().add(disconnectButton);
                }
                break;
            case ERROR:
                Alert alert=new Alert(message.getSubType().equals(MsgType.ERROR)?Alert.AlertType.ERROR:Alert.AlertType.INFORMATION);
                alert.setContentText(((Info_Message)message).getInfo());
                alert.showAndWait();
                break;
            case TURNSTATE:
                gamePane.addChatMessage(messageSender.getSender(),message.getContext(),message.getSubType());
                break;
            case WHISPER:
                gamePane.addChatMessage(message.getSender(),((Info_Message)message).getInfo(),message.getSubType());
                break;
            case CONNECT:
            case TEXT:
                System.out.println(((Info_Message)message).getInfo());
                break;
            case DISCONNECT:
                Alert info=new Alert(Alert.AlertType.INFORMATION);
                info.setContentText(((Info_Message)message).getInfo());
                info.setOnCloseRequest(event->Platform.exit());
                info.showAndWait();
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
                            gamePane.updatePlayer(player);
                        }
                        break;
                    case PLAYER:
                        gamePane.updatePlayer((ReducedPlayer) evt.getNewValue());
                        break;
                    case TURN_STATE:
                        gamePane.updateTurnState((ReducedGame)evt.getNewValue());
                        break;
                    case ISLANDS:
                        gamePane.updateIslands((ArrayList<ReducedIsland>)evt.getNewValue());
                        break;
                    case CLOUDS:
                        gamePane.updateClouds((ArrayList<ReducedCloud>) evt.getNewValue());
                        break;
                    case COINS:
                        gamePane.updateCoins((Integer)evt.getNewValue());
                        break;
                    case BAG:
                        gamePane.updateBag((ReducedBag)evt.getNewValue());
                        break;
                    case CHARACTER_CARDS:
                        gamePane.updateCharacterCards((ArrayList<ReducedCharacterCard>) evt.getNewValue());
                        break;
                    case LOADED_GAME:
                        inputPane.setVisible(false);
                        gamePane.setVisible(true);
                        gamePane.setupGame((ReducedGame) evt.getNewValue());
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
