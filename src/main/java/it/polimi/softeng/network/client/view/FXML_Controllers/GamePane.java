package it.polimi.softeng.network.client.view.FXML_Controllers;

import it.polimi.softeng.controller.TurnManager;
import it.polimi.softeng.exceptions.UpdateGUIException;
import it.polimi.softeng.model.CharID;
import it.polimi.softeng.model.ReducedModel.*;
import it.polimi.softeng.network.message.Info_Message;
import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MsgType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class GamePane extends AnchorPane implements Initializable {
    @FXML
    PlayerPane you, oppositePlayer, leftPlayer, rightPlayer;
    @FXML
    VBox gameChat;
    @FXML
    ChoiceBox<String> chatPartners;
    @FXML
    TextField chatField;
    @FXML
    GridPane tiles,characterCards;
    @FXML
    ToolBar assistantCards;
    @FXML
    Label currentPhase,currentPlayer;

    @FXML
    ImageView bag, coins;
    private final ArrayList<IslandPane> visibleIslands=new ArrayList<>();
    private final ArrayList<CloudPane> visibleClouds=new ArrayList<>();

    private final HashMap<String,PlayerPane> playerPanes=new HashMap<>();
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
                Tooltip.install(node,new Tooltip(node.getId()));
            }
        }
        try {
            characterInfo.load(getClass().getResourceAsStream("/CardData/CharacterCards.properties"));
        }
        catch (IOException ignored) {
        }
        chatField.setOnAction(event-> {
            messageSender.sendMessage(MsgType.WHISPER,chatPartners.getValue(),chatField.getText());
            chatField.clear();
        });
    }

    public void addChatMessage(Message msg) {
        String labelText,labelStyle="-fx-text-fill: black";
        switch (msg.getSubType()) {
            case ERROR:
                labelText=msg.getSender()+": "+((Info_Message)msg).getInfo();
                labelStyle="-fx-text-fill: red";
                break;
            case TURNSTATE:
                labelText=msg.getSender()+" :"+msg.getContext();
                break;
            case CLIENT_NUM:
                //update chatPartners
                chatPartners.getItems().clear();
                for (String choice : Arrays.stream(msg.getContext().substring(1,msg.getContext().length()-1).replace(" ","").split(",")).filter(choice -> !choice.equals(messageSender.getSender())).toArray(String[]::new)) {
                    chatPartners.getItems().add(choice);
                }
                if (chatPartners.getItems().size()>0) {
                    chatPartners.setValue(chatPartners.getItems().get(0));
                }
            default:
                labelText=msg.getSender()+": "+((Info_Message)msg).getInfo();
                break;
        }
        Label chatLabel=new Label(labelText);
        chatLabel.setStyle(labelStyle);
        this.gameChat.getChildren().add(chatLabel);
    }

    public void setupGame(ReducedGame model) throws UpdateGUIException {
        updateIslands(model.getIslands());
        updateClouds(model.getClouds());
        ArrayList<PlayerPane> otherPlayers=new ArrayList<>();
        if (model.getPlayers().size()!=3) {
            otherPlayers.add(oppositePlayer);
        }
        if (model.getPlayers().size()>2) {
            otherPlayers.add(leftPlayer);
            otherPlayers.add(rightPlayer);
        }
        for (ReducedPlayer player : model.getPlayers()) {
            if (player.getName().equals(messageSender.getSender())) {
                playerPanes.put(player.getName(),you);
                updateHand(player.getSchoolBoard().getHand());
                you.setupPlayer(player);
            } else {
                PlayerPane playerPane=otherPlayers.get(0);
                otherPlayers.remove(playerPane);
                playerPane.setVisible(true);
                playerPane.setupPlayer(player);
                playerPanes.put(player.getName(),playerPane);
            }
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
        for (PlayerPane player : Arrays.asList(you,oppositePlayer,leftPlayer,rightPlayer)) {
            player.setMessageSender(messageSender);
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
        playerPanes.get(player.getName()).updatePlayer(player);
        if (player.getName().equals(messageSender.getSender())) {
            updateHand(player.getSchoolBoard().getHand());
        }
    }

    public void updateHand(ArrayList<ReducedAssistantCard> cards) {
        assistantCards.getItems().clear();
        if (assistantCards.getContextMenu()!=null) {
            assistantCards.getContextMenu().getItems().clear();
        }
        for (ReducedAssistantCard card : cards) {
            ImageView assistantCard=new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/Assets/GUI/Cards/Assistants/" + card.getId() + "_LQ.png")).toExternalForm()));
            assistantCard.fitHeightProperty().setValue(80);
            assistantCard.fitWidthProperty().setValue(60);
            assistantCard.preserveRatioProperty().setValue(true);
            assistantCard.addEventHandler(MouseEvent.MOUSE_PRESSED,event-> messageSender.sendMessage(MsgType.PLAYASSISTCARD,card.getId(),card.getId()));
            Tooltip.install(assistantCard,new Tooltip("Card id: "+card.getId()+"\nTurn value: "+card.getTurnValue()+"\nMother nature value: "+card.getMotherNatureValue()));
            assistantCards.getItems().add(assistantCard);
        }
        assistantCards.setPrefWidth(assistantCards.getChildrenUnmodifiable().size()*62);
    }
    public void updateTurnState(ReducedGame model) {
        currentPhase.setText("Current phase: "+model.getCurrentPhase().getDescription()+(model.getCurrentPhase().equals(TurnManager.TurnState.MOVE_STUDENTS_PHASE)?"(Remaining moves: "+model.getRemainingMoves()+")":""));
        currentPlayer.setText("Current player:"+model.getCurrentPlayer());
    }
    public void updateCharacterCards(ArrayList<ReducedCharacterCard> cards) {
        characterCards.getChildren().clear();
        for (ReducedCharacterCard card : cards) {
            //TODO remove
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
    public void updateCoins(int num) {
        Tooltip.install(coins,new Tooltip("Game coins: "+num));
    }
    public void updateBag(ReducedBag bag) {
        //TODO implement
    }
}
