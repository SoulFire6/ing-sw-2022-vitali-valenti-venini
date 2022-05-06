package it.polimi.softeng.controller;

import it.polimi.softeng.exceptions.*;
import it.polimi.softeng.model.*;
import it.polimi.softeng.network.message.command.*;
import it.polimi.softeng.network.message.*;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;
import it.polimi.softeng.network.server.LobbyClient;

import java.lang.Exception;
import java.util.ArrayList;
import java.util.HashMap;

public class LobbyController {
    private final Game game;
    private final String lobbyName;
    private final TurnManager turnManager;
    private final CharCardController charCardController;
    private final AssistantCardController assistantCardController;
    private final TileController tileController;
    private final PlayerController playerController;

    public LobbyController(ArrayList<String> playerNames, boolean expertMode, String lobbyName) {
        this.lobbyName=lobbyName;
        this.assistantCardController = new AssistantCardController();
        this.tileController = new TileController();
        this.playerController = new PlayerController();
        if(expertMode) {
            this.charCardController = new CharCardController();
        } else {
            this.charCardController = null;
        }
        this.game=createGame(playerNames,expertMode);
        this.turnManager = new TurnManager(game.getPlayers(),game.getClouds().get(0).getMaxSlots());
    }
    public Game createGame(ArrayList<String> playerNames,boolean expertMode) {
        Game game;
        Bag_Tile bag=new Bag_Tile(24);
        ArrayList<Player> players;
        try {
            players = playerController.genPlayers(playerNames);
        }
        catch (InvalidPlayerNumException ipne) {
            return null;
        }
        for (Player p: players) {
            p.setSchoolBoard(new SchoolBoard_Tile(p.getName(),7+2*(playerNames.size()%2),8-2*(playerNames.size()-2),8,assistantCardController.genHand(null),expertMode?1:0));
        }
        ArrayList<Team> teams = playerController.getTeams(players);
        ArrayList<Island_Tile> islands = tileController.genIslands(12,new Bag_Tile(2));
        ArrayList<Cloud_Tile> clouds = tileController.genClouds(playerNames.size(),3+playerNames.size()%2,bag);
        game = new Game("Game",players,teams,bag,clouds,islands,expertMode,expertMode?20:0,expertMode?charCardController.genNewCharacterCards(3):null);
        return game;
    }
    public Game getGame() {
        return this.game;
    }
    public TurnManager getTurnManager() {
        return this.turnManager;
    }
    public AssistantCardController getAssistantCardController() {
        return this.assistantCardController;
    }
    public CharCardController getCharCardController() {
        return this.charCardController;
    }
    public Message parseMessage(Message inMessage) {
        Player currentPlayer=null;
        boolean playerFound=false;
        try {
            for (Player p: game.getPlayers()) {
                if (p.getName().equals(inMessage.getSender())) {
                    currentPlayer=p;
                    playerFound=true;
                    break;
                }
            }
            if (!playerFound) {
                throw new PlayerNotFoundException("Player not found");
            }
            switch (inMessage.getType()) {
                case INFO:
                    switch (inMessage.getSubType()) {
                        case WHISPER:
                            return MessageCenter.genMessage(MsgType.WHISPER,currentPlayer.getName(),inMessage.getContext(),((Info_Message)inMessage).getInfo());
                        case DISCONNECT:
                            return MessageCenter.genMessage(MsgType.DISCONNECT,lobbyName,currentPlayer.getName()+" has disconnected",null);

                    }
                case LOAD:
                    throw new MoveNotAllowedException("Cannot load objects on server");
                case COMMAND:
                    if (currentPlayer!=turnManager.getCurrentPlayer()) {
                        throw new NotYourTurnException("Not your turn, waiting for "+turnManager.getCurrentPlayer().getName());
                    }
                    switch (inMessage.getSubType()) {
                        case PLAYASSISTCARD:
                            if (turnManager.getTurnState()!=TurnManager.TurnState.ASSISTANT_CARDS_PHASE) {
                                throw new WrongPhaseException("Cannot use asssistant card during "+turnManager.getTurnState());
                            }
                            assistantCardController.playAssistantCard(currentPlayer,((AssistCard_Cmd_Msg)inMessage).getAssistID(),turnManager);
                            //TODO: replace with reduced model
                            return MessageCenter.genMessage(MsgType.ASSISTANTCARD,lobbyName,currentPlayer.getName()+"has played "+((AssistCard_Cmd_Msg)inMessage).getAssistID(),currentPlayer.getSchoolBoard().getHand());
                        case DISKTOISLAND:
                            if (turnManager.getTurnState()!=TurnManager.TurnState.MOVE_STUDENTS_PHASE) {
                                throw new WrongPhaseException("Cannot move student disk during "+turnManager.getTurnState());
                            }
                            tileController.moveStudentsToIsland(currentPlayer,((DiskToIsland_Cmd_Msg)inMessage).getColour(),inMessage.getContext(),game.getIslands(),turnManager);
                            break;
                        case DISKTODININGROOM:
                            if (turnManager.getTurnState()!=TurnManager.TurnState.MOVE_STUDENTS_PHASE) {
                                throw new WrongPhaseException("Cannot move student disk during "+turnManager.getTurnState());
                            }
                            playerController.moveStudentToDiningRoom(currentPlayer,game.getPlayers(),((DiskToDiningRoom_Cmd_Msg)inMessage).getColour(),game.isExpertMode());
                            break;
                        case MOVEMN:
                            if (turnManager.getTurnState()!=TurnManager.TurnState.MOVE_MOTHER_NATURE_PHASE) {
                                throw new WrongPhaseException("Cannot move mother nature during "+turnManager.getTurnState());
                            }
                            tileController.moveMotherNature(currentPlayer,((MoveMotherNature_Cmd_Msg)inMessage).getMoveAmount(),charCardController,game.getPlayers(),game.getCharacterCards(),game.getIslands(),playerController);
                            break;
                        case CHOOSECLOUD:
                            if (turnManager.getTurnState()!=TurnManager.TurnState.CHOOSE_CLOUD_TILE_PHASE) {
                                throw new WrongPhaseException("Cannot choose cloud during  "+turnManager.getTurnState());
                            }
                            tileController.refillEntranceFromCloud(currentPlayer,((ChooseCloud_Cmd_Msg)inMessage).getCloudID(),game.getClouds());
                            if (currentPlayer==turnManager.getLastPlayer()) {
                                tileController.refillClouds(game.getClouds(),game.getBag());
                            }
                            break;
                        case PLAYCHARCARD:
                            if (turnManager.getTurnState()==TurnManager.TurnState.ASSISTANT_CARDS_PHASE) {
                                throw new WrongPhaseException("Cannot play character cards  "+turnManager.getTurnState());
                            }
                            charCardController.activateCard(currentPlayer,((CharCard_Cmd_Msg)inMessage).getCharID(),game);
                            //TODO add immediate effects for specific cards, if the effect is passive
                            break;
                        default:
                            throw new MoveNotAllowedException("This should not be reachable");
                    }
            }
        }
        catch (Exception e) {
            System.out.println("["+lobbyName+"] "+currentPlayer+"'s action has thrown "+e.getCause());
            return MessageCenter.genMessage(MsgType.ERROR,lobbyName,e.getMessage(),null);
        }
        //TODO remove
        return null;
    }
}