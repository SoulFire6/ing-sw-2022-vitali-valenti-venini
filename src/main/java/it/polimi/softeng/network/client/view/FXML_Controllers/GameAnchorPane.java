package it.polimi.softeng.network.client.view.FXML_Controllers;

import it.polimi.softeng.exceptions.UpdateGUIException;
import it.polimi.softeng.model.ReducedModel.*;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class GameAnchorPane extends AnchorPane {
    @FXML
    VBox you, oppositePlayer, leftPlayer, rightPlayer;
    @FXML
    GridPane tiles;
    @FXML
    AnchorPane characterCards,turnState;
    @FXML
    Button toggleHandButton;
    @FXML
    ToolBar assistantCards;
    @FXML
    Label currentPhase,currentPlayer;
    private final ArrayList<VBox> otherPlayers=new ArrayList<>();
    private final ArrayList<IslandAnchorPane> visibleIslands=new ArrayList<>();
    private final ArrayList<CloudAnchorPane> visibleClouds=new ArrayList<>();
    public GameAnchorPane() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Assets/GUI/fxml/Game.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
            tiles.setAlignment(Pos.CENTER);
            for (Node node : tiles.getChildren()) {
                if (node.getId()!=null) {
                    if (node.getId().contains("Island")) {
                        visibleIslands.add((IslandAnchorPane) node);
                    }
                    if (node.getId().contains("Cloud")) {
                        visibleClouds.add((CloudAnchorPane) node);
                    }
                }
            }
            for (IslandAnchorPane island : visibleIslands) {
                island.prefHeightProperty().bind(tiles.heightProperty().subtract(100).divide(4));
                island.prefWidthProperty().bind(tiles.widthProperty().subtract(100).divide(4));
            }
            for (CloudAnchorPane cloud : visibleClouds) {
                cloud.prefHeightProperty().bind(tiles.heightProperty().subtract(100).divide(4));
                cloud.prefWidthProperty().bind(tiles.widthProperty().subtract(100).divide(4));
            }
            toggleHandButton.setOnAction(event->assistantCards.setVisible(!assistantCards.isVisible()));
            otherPlayers.addAll(Arrays.asList(oppositePlayer,rightPlayer,leftPlayer));
        }
        catch (IOException io) {
            io.printStackTrace();
            throw new RuntimeException(io);
        }
    }

    public void setupGame(ObjectOutputStream toServer, String sender, ReducedGame model) throws UpdateGUIException {
        setupIslands(toServer,sender);
        updateIslands(toServer,sender,model.getIslands());
        setupClouds(toServer,sender);
        updateClouds(model.getClouds());
        for (ReducedPlayer player : model.getPlayers()) {
            updatePlayer(toServer,sender,player);
        }
        updateTurnState(model);
    }

    public void setupIslands(ObjectOutputStream toServer, String sender) {
        for (IslandAnchorPane island : visibleIslands) {
            island.setupButtons(toServer,sender);
        }
    }
    public void updateIslands(ObjectOutputStream toServer, String sender, ArrayList<ReducedIsland> reducedIslands) throws UpdateGUIException {
        boolean foundIsland;
        ReducedIsland motherNatureIsland=null;
        for (ReducedIsland reducedIsland : reducedIslands) {
            if (reducedIsland.hasMotherNature()) {
                motherNatureIsland=reducedIsland;
                break;
            }
        }
        if (motherNatureIsland==null) {
            throw new UpdateGUIException("Error updating islands");
        }
        for (IslandAnchorPane island : visibleIslands) {
            foundIsland=false;
            for (ReducedIsland reducedIsland : reducedIslands) {
                if (island.getId().equals(reducedIsland.getID())) {
                    foundIsland=true;
                    island.update(toServer,sender,reducedIsland,Math.abs(reducedIslands.indexOf(motherNatureIsland)-reducedIslands.indexOf(reducedIsland)));
                    break;
                }
            }
            //hides merged islands
            if (!foundIsland) {
                island.setVisible(false);
                visibleIslands.remove(island);
            }
        }
    }

    public void setupClouds(ObjectOutputStream toServer, String sender) {
        for (CloudAnchorPane cloud : visibleClouds) {
            cloud.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, mouseEvent -> {
                try {
                    toServer.writeObject(MessageCenter.genMessage(MsgType.CHOOSECLOUD,sender,cloud.getId(),cloud.getId()));
                } catch (IOException io) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Error sending message to server");
                    alert.showAndWait();
                }
            });
        }
    }
    public void updateClouds(ArrayList<ReducedCloud> reducedClouds) {
        boolean foundCloud;
        for (CloudAnchorPane cloud : visibleClouds) {
            foundCloud=false;
            for (ReducedCloud reducedCloud : reducedClouds) {
                if (cloud.getId().equals(reducedCloud.getId())) {
                    foundCloud=true;
                    cloud.update(reducedCloud);
                    break;
                }
            }
            //hides unused clouds
            if (!foundCloud) {
                cloud.setVisible(false);
                visibleClouds.remove(cloud);
            }
        }
    }

    public void updatePlayer(ObjectOutputStream toServer, String sender, ReducedPlayer player) {
        if (player.getName().equals(sender)) {
            //Update interactive elements
            updateHand(toServer, sender, player.getSchoolBoard().getHand());
            updateSchoolBoard(toServer, sender, player.getSchoolBoard());
        } else {
            //Update passive elements
            for (VBox playerVBox : otherPlayers) {
                if (playerVBox.getId().equals(player.getName())) {
                    updateSchoolBoard(player.getSchoolBoard());
                }
            }
        }
    }

    public void updateHand(ObjectOutputStream toServer, String sender, ArrayList<ReducedAssistantCard> cards) {
        assistantCards.getItems().clear();
        for (ReducedAssistantCard card : cards) {
            Button assistantCard=new Button(card.getId());
            assistantCard.setPrefWidth(assistantCards.getPrefWidth());
            assistantCard.setPrefHeight(assistantCard.getPrefHeight());
            assistantCard.setStyle("-fx-background-image: url("+getClass().getResource("/Assets/GUI/Cards/Assistants/"+card.getId()+"_LQ.png")+");-fx-background-size: contain; -fx-background-repeat: no-repeat");
            assistantCard.setOnAction(event->{
                try {
                    toServer.writeObject(MessageCenter.genMessage(MsgType.PLAYASSISTCARD,sender,card.getId(),card.getId()));
                }
                catch (IOException io) {
                    Alert alert=new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Error sending message to server");
                    alert.showAndWait();
                }
            });
            assistantCards.getItems().add(assistantCard);
        }
    }

    public void updateSchoolBoard(ObjectOutputStream toServer, String sender, ReducedSchoolBoard schoolBoard) {
        //TODO implement
    }

    public void updateSchoolBoard(ReducedSchoolBoard schoolBoard) {
        //TODO implement
    }
    public void updateTurnState(ReducedGame model) {
        currentPhase.setText(model.getCurrentPhase().getDescription());
        currentPlayer.setText(model.getCurrentPlayer());
    }
}
