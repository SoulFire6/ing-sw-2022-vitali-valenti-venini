package it.polimi.softeng.network.client.view.FXML_Controllers;

import it.polimi.softeng.controller.TurnManager;
import it.polimi.softeng.exceptions.UpdateGUIException;
import it.polimi.softeng.model.CharID;
import it.polimi.softeng.model.ReducedModel.*;
import it.polimi.softeng.network.message.MsgType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class GamePane extends BorderPane implements Initializable {
    @FXML
    VBox you, oppositePlayer, leftPlayer, rightPlayer, gameChat;
    @FXML
    GridPane tiles,characterCards;
    @FXML
    Button toggleHandButton;
    @FXML
    ToolBar assistantCards;
    @FXML
    Label currentPhase,currentPlayer;
    private final ArrayList<VBox> otherPlayers=new ArrayList<>();
    private final ArrayList<IslandPane> visibleIslands=new ArrayList<>();
    private final ArrayList<CloudPane> visibleClouds=new ArrayList<>();
    private MessageSender messageSender;

    private final Properties characterInfo=new Properties();
    public GamePane() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Assets/GUI/fxml/Game.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        }
        catch (IOException io) {
            io.printStackTrace();
            throw new RuntimeException(io);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
        toggleHandButton.setOnAction(event->assistantCards.setVisible(!assistantCards.isVisible()));
        otherPlayers.addAll(Arrays.asList(oppositePlayer,rightPlayer,leftPlayer));
        try {
            characterInfo.load(getClass().getResourceAsStream("/CardData/CharacterCards.properties"));
        }
        catch (IOException ignored) {

        }
    }

    public void addChatMessage(String sender, String message, MsgType type) {
        Label chatLabel=new Label(sender+": "+message);
        switch (type) {
            case CLIENT_NUM:
            case ERROR:
                chatLabel.setStyle("-fx-text-fill: red");
                break;
            default:
                chatLabel.setStyle("-fx-text-fill: black");
                break;
        }
        this.gameChat.getChildren().add(chatLabel);
    }

    public void setupGame(ReducedGame model) throws UpdateGUIException {
        updateIslands(model.getIslands());
        updateClouds(model.getClouds());
        for (ReducedPlayer player : model.getPlayers()) {
            updatePlayer(player);
        }
        updateTurnState(model);
    }

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender=messageSender;
        for (IslandPane island : visibleIslands) {
            island.setMessageSender(messageSender);
        }
        for (CloudPane cloud : visibleClouds) {
            cloud.setMessageSender(messageSender);
        }
    }
    public void updateIslands(ArrayList<ReducedIsland> reducedIslands) throws UpdateGUIException {
        Optional<ReducedIsland> motherNatureIsland=reducedIslands.stream().filter(ReducedIsland::hasMotherNature).findFirst();
        if (motherNatureIsland.isEmpty()) {
            throw new UpdateGUIException("Could not find mother nature island");
        }
        int distance;
        for (IslandPane islandPane : visibleIslands) {
            Optional<ReducedIsland> reducedIsland=reducedIslands.stream().filter(island -> island.getID().equals(islandPane.getId())).findFirst();
            if (reducedIsland.isPresent()) {
                distance=reducedIslands.indexOf(reducedIsland.get())-reducedIslands.indexOf(motherNatureIsland.get());
                islandPane.update(reducedIsland.get(), distance < 0 ? reducedIslands.size() + distance : distance);
            } else {
                islandPane.setVisible(false);
            }
        }
        visibleIslands.removeIf(islandPane -> !islandPane.isVisible());
    }

    public void updateClouds(ArrayList<ReducedCloud> reducedClouds) {
        boolean found;
        for (CloudPane cloud : visibleClouds) {
            found=false;
            for (ReducedCloud reducedCloud : reducedClouds) {
                if (cloud.getId().equals(reducedCloud.getId())) {
                    found=true;
                    cloud.update(reducedCloud);
                    break;
                }
            }
            //hides unused clouds
            cloud.setVisible(found);
        }
        visibleClouds.removeIf(cloudPane -> !cloudPane.isVisible());
    }

    public void updatePlayer(ReducedPlayer player) {
        if (player.getName().equals(messageSender.getSender())) {
            //Update interactive elements
            updateHand(player.getSchoolBoard().getHand());
            updateSchoolBoard(player.getSchoolBoard(),true);
        } else {
            //Update passive elements
            updateSchoolBoard(player.getSchoolBoard(),false);
        }
    }

    public void updateHand(ArrayList<ReducedAssistantCard> cards) {
        assistantCards.getItems().clear();
        if (assistantCards.getContextMenu()!=null) {
            assistantCards.getContextMenu().getItems().clear();
        }
        for (ReducedAssistantCard card : cards) {
            //TODO add zoom when mouse is hovered over it so it can be seen better
            ImageView assistantCard=new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/Assets/GUI/Cards/Assistants/" + card.getId() + "_LQ.png")).toExternalForm()));
            assistantCard.fitHeightProperty().bind(assistantCards.heightProperty());
            assistantCard.fitWidthProperty().bind(assistantCard.fitHeightProperty().divide(4).multiply(3));
            assistantCard.addEventHandler(MouseEvent.MOUSE_PRESSED,event-> messageSender.sendMessage(MsgType.PLAYASSISTCARD,card.getId(),card.getId()));
            assistantCard.setAccessibleText("Card id: "+card.getId()+"\nTurn value: "+card.getTurnValue()+"\nMother nature value: "+card.getMotherNatureValue());
            assistantCards.getItems().add(assistantCard);
        }
        assistantCards.prefWidthProperty().setValue(assistantCards.getChildrenUnmodifiable().size()*((ImageView)assistantCards.getChildrenUnmodifiable().get(0)).fitWidthProperty().get());
    }

    public void updateSchoolBoard(ReducedSchoolBoard schoolBoard, boolean personalBoard) {
        //TODO implement (if not personalBoard do not send message to server when clicked)
    }
    public void updateTurnState(ReducedGame model) {
        currentPhase.setText("Current phase: "+model.getCurrentPhase().getDescription()+(model.getCurrentPhase().equals(TurnManager.TurnState.MOVE_STUDENTS_PHASE)?"(Remaining moves: "+model.getRemainingMoves()+")":""));
        currentPlayer.setText("Current player:"+model.getCurrentPlayer());
    }
    public void updateCharacterCards(ArrayList<ReducedCharacterCard> cards) {
        characterCards.getChildren().clear();
        for (ReducedCharacterCard card : cards) {
            System.out.println(card.getId()+" : "+card.getCharID());
            Node charCard;
            switch (CharID.MemType.valueOf(card.getMemoryType())) {
                //TODO implement different visuals for cards
                case PLAYER_COLOUR_MAP:
                case INTEGER:
                case INTEGER_COLOUR_MAP:
                case BOOLEAN_COLOUR_MAP:
                case NONE:
                default:
                    charCard=new Button();
                    charCard.setStyle("-fx-background-image: url('/Assets/GUI/Cards/Characters/"+card.getCharID()+"')");
                    charCard.addEventHandler(MouseEvent.MOUSE_PRESSED,event->messageSender.sendMessage(MsgType.PLAYCHARCARD,"",card.getCharID()));
                    break;
            }
            characterCards.getChildren().add(charCard);
        }
    }
    public void updateCoins(int coins) {
        //TODO implement
    }
    public void updateBag(ReducedBag bag) {
        //TODO implement
    }
}
