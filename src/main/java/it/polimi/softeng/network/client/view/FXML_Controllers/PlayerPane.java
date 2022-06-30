package it.polimi.softeng.network.client.view.FXML_Controllers;

import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.ReducedModel.ReducedAssistantCard;
import it.polimi.softeng.model.ReducedModel.ReducedPlayer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PlayerPane extends VBox implements Initializable {
    @FXML
    Label name, towers, coins, mnValue, turnValue;
    @FXML
    Button showBoard;
    private BoardPane board;

    private MessageSender messageSender;

    private Stage stage;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        board=new BoardPane();
        stage=new Stage();
        stage.setScene(new Scene(board));
        stage.setMinHeight(200);
        stage.setMinWidth(460);
        stage.setMaxHeight(200);
        stage.setMaxWidth(460);
        stage.setHeight(200);
        stage.setWidth(460);
        showBoard.setOnAction(event-> stage.showAndWait());
    }

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender=messageSender;
        this.board.setMessageSender(messageSender);
    }

    public void setupPlayer(ReducedPlayer player) {
        name.setText(player.getName());
        stage.setTitle(name.getText()+"'s school board");
        board.setupTowers(player.getSchoolBoard().getTowers(),player.getTeam());
        updatePlayer(player);
    }

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

    public String getName() {
        return this.name.getText();
    }
}
