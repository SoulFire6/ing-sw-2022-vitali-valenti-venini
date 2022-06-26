package it.polimi.softeng.network.client.view.FXML_Controllers;

import it.polimi.softeng.controller.TurnManager;
import it.polimi.softeng.exceptions.UpdateGUIException;
import it.polimi.softeng.model.ReducedModel.*;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class GamePane extends AnchorPane {
    @FXML
    VBox you, oppositePlayer, leftPlayer, rightPlayer;
    @FXML
    GridPane tiles;
    @FXML
    AnchorPane characterCards,turnState;

    @FXML
    VBox gameChat;
    @FXML
    Button toggleHandButton;
    @FXML
    ToolBar assistantCards;
    @FXML
    Label currentPhase,currentPlayer;
    private final ArrayList<VBox> otherPlayers=new ArrayList<>();
    private final ArrayList<IslandPane> visibleIslands=new ArrayList<>();
    private final ArrayList<CloudPane> visibleClouds=new ArrayList<>();
    public GamePane() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Assets/GUI/fxml/Game.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
            for (Node node : tiles.getChildren()) {
                if (node.getId()!=null) {
                    if (node.getId().contains("Island")) {
                        visibleIslands.add((IslandPane) node);
                    }
                    if (node.getId().contains("Cloud")) {
                        visibleClouds.add((CloudPane) node);
                    }
                }
            }
            for (IslandPane island : visibleIslands) {
                island.prefHeightProperty().bind(tiles.heightProperty().subtract(100).divide(4));
                island.prefWidthProperty().bind(tiles.widthProperty().subtract(100).divide(4));
            }
            for (CloudPane cloud : visibleClouds) {
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

    public void addChatMessage(String sender, String message, MsgType type) {
        Label chatLabel=new Label(sender+": "+message);
        switch (type) {
            case ERROR:
                chatLabel.setStyle("-fx-text-fill: red");
                break;
            case DISPLAY:
                chatLabel.setStyle("-fx-text-fill: blue");
                break;
            default:
                chatLabel.setStyle("-fx-text-fill: orange");
                break;
        }
        this.gameChat.getChildren().add(chatLabel);
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
        for (IslandPane island : visibleIslands) {
            island.setupButtons(toServer,sender);
        }
    }
    public void updateIslands(ObjectOutputStream toServer, String sender, ArrayList<ReducedIsland> reducedIslands) throws UpdateGUIException {
        int distance;
        ReducedIsland motherNatureIsland = null;
        for (ReducedIsland reducedIsland : reducedIslands) {
            if (reducedIsland.hasMotherNature()) {
                motherNatureIsland = reducedIsland;
                break;
            }
        }
        if (motherNatureIsland == null) {
            throw new UpdateGUIException("Error updating islands");
        }
        for (IslandPane island : visibleIslands) {
            for (ReducedIsland reducedIsland : reducedIslands) {
                if (island.getId().equals(reducedIsland.getID())) {
                    distance = reducedIslands.indexOf(motherNatureIsland) - reducedIslands.indexOf(reducedIsland);
                    island.update(toServer, sender, reducedIsland, distance < 0 ? reducedIslands.size() + distance : distance);
                    break;
                }
                //hides merged islands
                island.setVisible(false);
            }
        }
        visibleIslands.removeIf(islandPane -> !islandPane.isVisible());
    }

    public void setupClouds(ObjectOutputStream toServer, String sender) {
        for (CloudPane cloud : visibleClouds) {
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
        for (CloudPane cloud : visibleClouds) {
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
        if (assistantCards.getContextMenu()!=null) {
            assistantCards.getContextMenu().getItems().clear();
        }
        for (ReducedAssistantCard card : cards) {
            System.out.println("SETTING CARD");
            ImageView assistantCard=new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/Assets/GUI/Cards/Assistants/" + card.getId() + "_LQ.png")).toExternalForm()));
            assistantCard.fitHeightProperty().bind(assistantCards.heightProperty());
            assistantCard.fitWidthProperty().bind(assistantCard.fitHeightProperty().divide(3).multiply(2));
            assistantCard.addEventHandler(MouseEvent.MOUSE_PRESSED,event->{
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
        currentPhase.setText("Current phase: "+model.getCurrentPhase().getDescription()+(model.getCurrentPhase().equals(TurnManager.TurnState.MOVE_STUDENTS_PHASE)?"(Remaining moves: "+model.getRemainingMoves()+")":""));
        currentPlayer.setText("Current player:"+model.getCurrentPlayer());
    }
    public void updateCharacterCards(ObjectOutputStream toServer, String sender, ArrayList<ReducedCharacterCard> cards) {

    }
}
