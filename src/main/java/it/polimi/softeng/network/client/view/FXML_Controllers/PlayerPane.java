package it.polimi.softeng.network.client.view.FXML_Controllers;

import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.ReducedModel.ReducedAssistantCard;
import it.polimi.softeng.model.ReducedModel.ReducedPlayer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is a custom FXML component controller for a player
 */
public class PlayerPane extends VBox implements Initializable {
    @FXML
    Label name, towers, coins, mnValue, turnValue;
    @FXML
    Button showBoard;
    private BoardPane board;

    private MessageSender messageSender;

    private Stage stage;

    /**
     * Default constructor for PlayerPane
     */
    public PlayerPane() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Assets/GUI/fxml/Player.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        }
        catch (IOException io) {
            io.printStackTrace();
            throw new RuntimeException(io);
        }
    }

    /**
     * Inherited initialize method to set up components
     * @param url default unused url
     * @param resourceBundle default unused resource
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        board=new BoardPane();
        stage=new Stage();
        stage.setScene(new Scene(board));
        stage.resizableProperty().setValue(false);
        stage.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue) {
                stage.hide();
            }
        });
        showBoard.setOnAction(event-> stage.show());
    }

    /**
     * Setter for message sender
     * @param messageSender the message sender to set
     */

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender=messageSender;
        this.board.setMessageSender(messageSender);
    }

    /**
     * Setups up the PlayerPane with info from the player
     * @param player the player this Pane represents
     */

    public void setupPlayer(ReducedPlayer player) {
        name.setText(player.getName());
        stage.setTitle(name.getText()+"'s school board");
        board.setupTowers(player.getSchoolBoard().getTowers(),player.getTeam());
        updatePlayer(player);
    }

    /**
     * Updates PlayerPane with info from the player
     * @param player the player this Pane represents
     */

    public void updatePlayer(ReducedPlayer player) {
        towers.setText(String.valueOf(player.getSchoolBoard().getTowers()));
        coins.setText(String.valueOf(player.getSchoolBoard().getCoins()));
        ReducedAssistantCard lastCard=player.getSchoolBoard().getLastUsedCard();
        mnValue.setText(lastCard==null?"N/A":String.valueOf(lastCard.getMotherNatureValue()));
        turnValue.setText(lastCard==null?"N/A":String.valueOf(lastCard.getTurnValue()));
        board.updateTowers(player.getSchoolBoard().getTowers());
        board.updateEntrance(player.getSchoolBoard().getEntrance(),player.getName().equals(messageSender.getSender()));
        for (Colour c : Colour.values()) {
            board.updateRow(c,player.getSchoolBoard().getDiningRoom().get(c),player.getSchoolBoard().getProfessorTable().get(c));
        }
    }

    /**
     * This method returns the name of the player this Pane belongs to
     * @return String the name of the player
     */

    public String getName() {
        return this.name.getText();
    }
}
