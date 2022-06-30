package it.polimi.softeng.network.client.view.FXML_Controllers;

import it.polimi.softeng.controller.TurnManager;
import it.polimi.softeng.exceptions.UpdateGUIException;
import it.polimi.softeng.model.CharID;
import it.polimi.softeng.model.Colour;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class GamePane extends BorderPane implements Initializable {
    @FXML
    VBox you, oppositePlayer, leftPlayer, rightPlayer, gameChat;
    @FXML
    GridPane tiles,characterCards;

    @FXML
    TextField chatField;

    @FXML
    ChoiceBox<String> chatPartners;

    @FXML
    BoardPane yourBoard, oppositeBoard, leftBoard, rightBoard;
    @FXML
    Button toggleHandButton;
    @FXML
    ToolBar assistantCards;
    @FXML
    Label currentPhase,currentPlayer;
    private final ArrayList<VBox> otherPlayers=new ArrayList<>();
    private final ArrayList<IslandPane> visibleIslands=new ArrayList<>();
    private final ArrayList<CloudPane> visibleClouds=new ArrayList<>();

    private final HashMap<String,BoardPane> playerBoards=new HashMap<>();
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
        ArrayList<BoardPane> opponentBoards=new ArrayList<>();
        if (model.getPlayers().size()!=3) {
            opponentBoards.add(oppositeBoard);
        }
        if (model.getPlayers().size()>2) {
            opponentBoards.add(leftBoard);
            opponentBoards.add(rightBoard);
        }
        for (BoardPane boardPane : opponentBoards) {
            boardPane.getParent().addEventHandler(MouseEvent.MOUSE_PRESSED,event->{
                //TODO show board in new window
                Stage boardStage=new Stage();
            });
        }
        for (ReducedPlayer player : model.getPlayers()) {
            if (player.getName().equals(messageSender.getSender())) {
                playerBoards.put(player.getName(),yourBoard);
            } else {
                playerBoards.put(player.getName(),opponentBoards.get(0));
                opponentBoards.remove(0);
            }
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
        yourBoard.setMessageSender(messageSender);
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
        ReducedSchoolBoard schoolBoard=player.getSchoolBoard();
        BoardPane boardPane=playerBoards.get(player.getName());
        if (player.getName().equals(messageSender.getSender())) {
            //Update interactive elements
            updateHand(player.getSchoolBoard().getHand());
            boardPane.updateEntrance(schoolBoard.getEntrance(),true);
        } else {
            boardPane.updateEntrance(schoolBoard.getEntrance(),false);
        }
        //Update passive elements
        boardPane.updateTowers(schoolBoard.getTowers());
        for (Colour c : Colour.values()) {
            boardPane.updateRow(c,schoolBoard.getDiningRoom().get(c),schoolBoard.getProfessorTable().get(c));
        }
    }

    public void updateHand(ArrayList<ReducedAssistantCard> cards) {
        assistantCards.getItems().clear();
        if (assistantCards.getContextMenu()!=null) {
            assistantCards.getContextMenu().getItems().clear();
        }
        for (ReducedAssistantCard card : cards) {
            ImageView assistantCard=new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/Assets/GUI/Cards/Assistants/" + card.getId() + "_LQ.png")).toExternalForm()));
            assistantCard.fitHeightProperty().setValue(assistantCards.getHeight());
            assistantCard.fitWidthProperty().bind(assistantCard.fitHeightProperty().divide(4).multiply(3));
            assistantCard.addEventHandler(MouseEvent.MOUSE_PRESSED,event-> messageSender.sendMessage(MsgType.PLAYASSISTCARD,card.getId(),card.getId()));
            assistantCard.addEventHandler(MouseEvent.MOUSE_ENTERED,event-> assistantCard.setFitHeight(assistantCard.getFitHeight()*2));
            assistantCard.addEventHandler(MouseEvent.MOUSE_EXITED,event-> assistantCard.setFitHeight(assistantCard.getFitHeight()/2));
            assistantCard.setAccessibleText("Card id: "+card.getId()+"\nTurn value: "+card.getTurnValue()+"\nMother nature value: "+card.getMotherNatureValue());
            assistantCards.getItems().add(assistantCard);
        }
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
