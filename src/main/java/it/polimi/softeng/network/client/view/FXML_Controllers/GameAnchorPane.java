package it.polimi.softeng.network.client.view.FXML_Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class GameAnchorPane extends AnchorPane {
    @FXML
    VBox you, oppositePlayer, leftPlayer, rightPlayer;
    @FXML
    GridPane tiles;
    @FXML
    AnchorPane characterCards;
    @FXML
    Button toggleHandButton;
    @FXML
    ToolBar assistantCards;
    public GameAnchorPane() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Assets/GUI/fxml/Game.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        }
        catch (IOException io) {
            throw new RuntimeException(io);
        }
    }
}
