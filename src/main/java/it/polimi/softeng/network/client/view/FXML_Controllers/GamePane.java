package it.polimi.softeng.network.client.view.FXML_Controllers;

import it.polimi.softeng.controller.TurnManager;
import it.polimi.softeng.exceptions.UpdateGUIException;
import it.polimi.softeng.model.CharID;
import it.polimi.softeng.model.Colour;
import it.polimi.softeng.network.reducedModel.*;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * This class is a custom FXML component controller that represents a whole game
 */
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
    GridPane tiles;
    @FXML
    HBox characterCards;
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

    /**
     * Default constructor that loads the fxml file
     */
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

    /**
     * Inherited initialize method that sets up fxml components
     * @param url unused default url
     * @param resourceBundle unused default resource
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Tooltip tooltip;
        for (Node node : tiles.getChildren()) {
            if (node.getId()!=null) {
                if (node.getId().contains("Island")) {
                    visibleIslands.add((IslandPane) node);
                }
                if (node.getId().contains("Cloud")) {
                    visibleClouds.add((CloudPane) node);
                }
                tooltip=new Tooltip(node.getId());
                tooltip.setStyle("-fx-font-size: 15");
                Tooltip.install(node,tooltip);
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

    /**
     * This method adds a message to the chat window
     * @param msg the message to add
     */
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

    /**
     * This method sets up the game pane with model data
     * @param model the model to load
     * @throws UpdateGUIException if there is an error updating part of the GUI
     */
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
        if (model.isExpertMode()) {
            updateCharacterCards(model.getCharacterCards());
        }
        updateTurnState(model);
    }

    /**
     * Setter for messageSender, also sets it for all child nodes that need it like IslandPanes, CloudPanes and PlayerPanes
     * @param messageSender the messageSender to set
     */
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

    /**
     * This method updates the IslandPanes with info from the model islands
     * @param reducedIslands the model islands
     * @throws UpdateGUIException if mother nature island cannot be found
     */
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

    /**
     * This method updates the cloud tiles from model clouds
     * @param reducedClouds the model clouds
     */
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

    /**
     * this method updates a single PlayerPane
     * @param player the player to load into PlayerPane
     */
    public void updatePlayer(ReducedPlayer player) {
        playerPanes.get(player.getName()).updatePlayer(player);
        if (player.getName().equals(messageSender.getSender())) {
            updateHand(player.getSchoolBoard().getHand());
        }
    }
    /**
     * this method updates the player's hand
     * @param cards the cards to load
     */
    public void updateHand(ArrayList<ReducedAssistantCard> cards) {
        assistantCards.getItems().clear();
        if (assistantCards.getContextMenu()!=null) {
            assistantCards.getContextMenu().getItems().clear();
        }
        for (ReducedAssistantCard card : cards) {
            Tooltip tooltip=new Tooltip("Card id: "+card.getId()+"\nTurn value: "+card.getTurnValue()+"\nMother nature value: "+card.getMotherNatureValue());
            tooltip.setStyle("-fx-font-size: 15");
            ImageView assistantCard=new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/Assets/GUI/Cards/Assistants/" + card.getId() + "_LQ.png")).toExternalForm()));
            assistantCard.fitHeightProperty().setValue(80);
            assistantCard.fitWidthProperty().setValue(60);
            assistantCard.preserveRatioProperty().setValue(true);
            assistantCard.addEventHandler(MouseEvent.MOUSE_PRESSED,event-> messageSender.sendMessage(MsgType.PLAYASSISTCARD,card.getId(),card.getId()));
            Tooltip.install(assistantCard,tooltip);
            assistantCards.getItems().add(assistantCard);
        }
        assistantCards.setPrefWidth(assistantCards.getChildrenUnmodifiable().size()*62);
    }
    /**
     * this method updates the turnstate, but also the bag tile and the game coins
     * @param model the model to load
     */
    public void updateTurnState(ReducedGame model) {
        updateBag(model.getBag());
        updateCoins(model.getCoins());
        currentPhase.setText("Current phase: "+model.getCurrentPhase().getDescription()+(model.getCurrentPhase().equals(TurnManager.TurnState.MOVE_STUDENTS_PHASE)?"(Remaining moves: "+model.getRemainingMoves()+")":""));
        currentPlayer.setText("Current player:"+model.getCurrentPlayer());
    }
    /**
     * this method updates the character cards
     * @param cards the cards to load
     */
    public void updateCharacterCards(ArrayList<ReducedCharacterCard> cards) {
        characterCards.getChildren().clear();
        EnumMap<Colour,?> memory=null;
        Tooltip tooltip;
        Button characterCard;
        for (ReducedCharacterCard card : cards) {
            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append(card.getId()).append("\nCost: ").append(card.getCost()).append(characterInfo.getProperty(card.getCharID() + "_SETUP")).append("\n").append(characterInfo.getProperty(card.getCharID() + "_EFFECT"));
            characterCard = new Button();
            characterCard.setStyle("-fx-background-image: url('Assets/GUI/Cards/Characters/" + card.getId().replace(" ", "").concat(".png")+"');-fx-background-size: cover");
            characterCard.setPrefWidth(50);
            characterCard.setPrefHeight(80);
            tooltip=new Tooltip();
            tooltip.setStyle("-fx-font-size: 15");
            switch (CharID.MemType.valueOf(card.getMemoryType())) {
                case INTEGER:
                    stringBuilder.append("\nNo entry tiles: ").append(card.getMemory());
                    break;
                case BOOLEAN_COLOUR_MAP:
                    memory=card.getMemory(Boolean.class);
                    break;
                case INTEGER_COLOUR_MAP:
                    memory=card.getMemory(Integer.class);
                    break;
                case PLAYER_COLOUR_MAP:
                case NONE:
                    break;
                default:
                    new Alert(Alert.AlertType.ERROR,"This should not be reachable").showAndWait();
                    break;
            }
            if (memory!=null) {
                for (Colour c : Colour.values()) {
                    stringBuilder.append(c.name()).append(": ").append(memory.get(c)).append("\n");
                }
            }
            tooltip=new Tooltip(stringBuilder.toString());

            Tooltip.install(characterCard,tooltip);
            switch (CharID.valueOf(card.getCharID())) {
                //Choose a colour
                case SHROOM_VENDOR:
                case SPOILED_PRINCESS:
                case THIEF:
                    characterCard.setOnAction(event-> new Alert(Alert.AlertType.INFORMATION,"Right click to choose colour").showAndWait());
                    ContextMenu colourContextMenu=new ContextMenu();
                    for (Colour c : Colour.values()) {
                        MenuItem menuItem=new MenuItem(c.name().toLowerCase());
                        menuItem.setOnAction(event-> messageSender.sendMessage(MsgType.PLAYCHARCARD,card.getCharID(),c.getName()));
                        colourContextMenu.getItems().add(menuItem);
                    }
                    characterCard.setContextMenu(colourContextMenu);
                    break;
                //Choose island
                case HERALD:
                case GRANDMA_HERBS:
                    characterCard.setOnAction(event-> new Alert(Alert.AlertType.INFORMATION,"Right click to choose island").showAndWait());
                    ContextMenu islandContextMenu=new ContextMenu();
                    for (IslandPane islandPane : visibleIslands) {
                        MenuItem menuItem=new MenuItem(islandPane.getId());
                        menuItem.setOnAction(event-> messageSender.sendMessage(MsgType.PLAYCHARCARD,card.getCharID(),menuItem.getText()));
                        islandContextMenu.getItems().add(menuItem);
                    }
                    characterCard.setContextMenu(islandContextMenu);
                    break;
                case MONK:
                    characterCard.setOnAction(event-> new Alert(Alert.AlertType.INFORMATION,"Right click to choose colour, then again for colour").showAndWait());
                    ContextMenu outerColourContextMenu=new ContextMenu();
                    for (Colour c : Colour.values()) {
                        MenuItem menuItem=new MenuItem(c.name().toLowerCase());
                        Button innerCharacterCard = characterCard;
                        menuItem.setOnAction(event-> {
                            ContextMenu innerColourContextMenu=new ContextMenu();
                            for (IslandPane islandPane : visibleIslands) {
                                MenuItem innerMenuItem=new MenuItem(menuItem.getText()+" "+islandPane.getId());
                                innerMenuItem.setOnAction(innerEvent-> {
                                    innerCharacterCard.setContextMenu(outerColourContextMenu);
                                    messageSender.sendMessage(MsgType.PLAYCHARCARD,card.getCharID(),menuItem.getText());
                                });
                                innerColourContextMenu.getItems().add(innerMenuItem);
                            }
                            innerCharacterCard.setContextMenu(innerColourContextMenu);
                        });
                        outerColourContextMenu.getItems().add(menuItem);
                    }
                    characterCard.setContextMenu(outerColourContextMenu);
                    break;
                //Colour swap
                case JESTER:
                case MINSTREL:
                    characterCard.setOnAction(event-> new Alert(Alert.AlertType.INFORMATION,"Right click to choose colour, then again to concatenate colours for up to "+(card.getCharID().equalsIgnoreCase("minstrel")?2:3)+" pairs").showAndWait());
                    characterCard.setContextMenu(generateInnerContextMenu(characterCard,card.getCharID().equalsIgnoreCase("minstrel")?4:6,"",card.getCharID()));
                    break;
                //No args
                case MAGIC_POSTMAN:
                case CENTAUR:
                case KNIGHT:
                case FARMER:
                    characterCard.setOnAction(event->messageSender.sendMessage(MsgType.PLAYCHARCARD,card.getId(),""));
                    break;
                default:
                    new Alert(Alert.AlertType.ERROR,"This should not be reachable").showAndWait();
                    break;
            }
            characterCards.getChildren().add(characterCard);
        }
    }

    /**
     * This method generates the contextmenu for swap colour swap cards (Minstrel and Jester)
     * @param card the card the contextMenu must be set to
     * @param maxLength the max amount of colours in the sequence before it gets automatically sent
     * @param string the string generated by the concatenation of colour, it will be sent to server as options for play character card message
     * @param charID the id of the card that will be played
     * @return ContextMenu the new menu that will replace the old one
     */
    private ContextMenu generateInnerContextMenu(Button card, int maxLength, String string, String charID) {
        ContextMenu innerMenu=new ContextMenu();
        if (string!=null && string.split(" ").length==maxLength) {
            card.setContextMenu(generateInnerContextMenu(card,maxLength,null,charID));
            new Alert(Alert.AlertType.INFORMATION,"Max length reached, args sent: "+string).showAndWait();
            messageSender.sendMessage(MsgType.PLAYCHARCARD,charID,string);
        }

        for (Colour c : Colour.values()) {
            MenuItem menuItem=new MenuItem(c.name().toLowerCase());
            menuItem.setOnAction(event-> card.setContextMenu(generateInnerContextMenu(card,maxLength,(string!=null?string+" ":"")+menuItem.getText(),charID)));
            innerMenu.getItems().add(menuItem);
        }
        MenuItem sendItem=new MenuItem("Send: "+string);
        sendItem.setOnAction(event->{
            card.setContextMenu(generateInnerContextMenu(card,maxLength,"",charID));
            messageSender.sendMessage(MsgType.PLAYCHARCARD,charID,string);
        });
        innerMenu.getItems().add(sendItem);
        MenuItem resetItem=new MenuItem("Reset");
        resetItem.setOnAction(event-> card.setContextMenu(generateInnerContextMenu(card,maxLength,"",charID)));
        innerMenu.getItems().add(resetItem);
        return innerMenu;
    }
    /**
     * this method updates the game coins
     * @param num the current value of game coins
     */
    public void updateCoins(int num) {
        Tooltip tooltip=new Tooltip("Game coins: "+num);
        tooltip.setStyle("-fx-font-size: 15");
        Tooltip.install(coins,tooltip);
    }
    /**
     * this method updates the bag
     * @param bag the bag to load
     */
    public void updateBag(ReducedBag bag) {
        StringBuilder stringBuilder=new StringBuilder();
        for (Colour c : Colour.values()) {
            stringBuilder.append(c.name()).append(": ").append(bag.getContents().get(c)).append("-");
        }
        Tooltip tooltip=new Tooltip(stringBuilder.substring(0,stringBuilder.length()-1).replace("-","\n"));
        tooltip.setStyle("-fx-font-size: 15");
        Tooltip.install(this.bag,tooltip);
    }
}
