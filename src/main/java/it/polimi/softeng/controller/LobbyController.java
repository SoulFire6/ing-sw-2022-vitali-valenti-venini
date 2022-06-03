package it.polimi.softeng.controller;

import it.polimi.softeng.exceptions.*;
import it.polimi.softeng.model.*;
import it.polimi.softeng.network.message.command.*;
import it.polimi.softeng.network.message.*;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;

import java.lang.Exception;
import java.util.*;

public class LobbyController {
    private final Game game;
    private final String lobbyName;
    private final TurnManager turnManager;
    private final CharCardController charCardController;
    private final AssistantCardController assistantCardController;
    private final TileController tileController;
    private final PlayerController playerController;

    public LobbyController(ArrayList<String> playerNames, boolean expertMode, String lobbyName) throws InvalidPlayerNumException {
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
    public Game createGame(ArrayList<String> playerNames,boolean expertMode) throws InvalidPlayerNumException {
        Game game;
        Bag_Tile bag=new Bag_Tile(24);
        ArrayList<Player> players;
        players = playerController.genPlayers(playerNames);
        for (Player p: players) {
            p.setSchoolBoard(new SchoolBoard_Tile(p.getName(),7+2*(playerNames.size()%2),8-2*(playerNames.size()-2),8,assistantCardController.genHand(),expertMode?1:0));
            p.getSchoolBoard().fillEntrance(bag);
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
    public ArrayList<Message> parseMessage(Message inMessage) {
        Player currentPlayer=null;
        ArrayList<Message> response=new ArrayList<>();
        try {
            for (Player p: game.getPlayers()) {
                if (p.getName().equals(inMessage.getSender())) {
                    currentPlayer=p;
                    break;
                }
            }
            if (currentPlayer==null) {
                throw new PlayerNotFoundException("Player not found");
            }
            switch (inMessage.getType()) {
                case INFO:
                    switch (inMessage.getSubType()) {
                        case WHISPER:
                            if (inMessage.getContext().equals(currentPlayer.getName())) {
                                throw new IllegalArgumentException("Can't send message to self");
                            }
                            String recipient=null;
                            for (Player p : game.getPlayers()) {
                                if (inMessage.getContext().equals(p.getName())) {
                                    recipient=inMessage.getContext();
                                    break;
                                }
                            }
                            if (recipient==null) {
                                throw new IllegalArgumentException("No player with name "+inMessage.getContext());
                            }
                            response.add(MessageCenter.genMessage(MsgType.WHISPER,currentPlayer.getName(),recipient,((Info_Message)inMessage).getInfo()));
                            break;
                        case DISCONNECT:
                            //this message is for the lobby and disconnecting client only
                            response.add(MessageCenter.genMessage(MsgType.DISCONNECT,lobbyName,currentPlayer.getName(),null));
                            //this message informs the other players of the disconnect
                            response.add(MessageCenter.genMessage(MsgType.TEXT,lobbyName,currentPlayer.getName()+" has disconnected",null));
                            break;
                    }
                    break;
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
                            assistantCardController.playAssistantCard(currentPlayer,((AssistCard_Cmd_Msg)inMessage).getAssistID(),turnManager.getPlayerOrder());
                            turnManager.nextAction();
                            response.add(MessageCenter.genMessage(MsgType.PLAYER,lobbyName,"Play assist card",currentPlayer));
                            response.add(MessageCenter.genMessage(MsgType.TEXT,lobbyName,"Play assist card",currentPlayer.getName()+" has played "+((AssistCard_Cmd_Msg)inMessage).getAssistID()));
                            break;
                        case DISKTOISLAND:
                            if (turnManager.getTurnState()!=TurnManager.TurnState.MOVE_STUDENTS_PHASE) {
                                throw new WrongPhaseException("Cannot move student disk during "+turnManager.getTurnState());
                            }
                            tileController.moveStudentsToIsland(currentPlayer,((DiskToIsland_Cmd_Msg)inMessage).getColour(),inMessage.getContext(),game.getIslands());
                            turnManager.nextAction();
                            response.add(MessageCenter.genMessage(MsgType.ISLANDS,lobbyName,inMessage.getContext(),game.getIslands()));
                            response.add(MessageCenter.genMessage(MsgType.PLAYER,lobbyName,inMessage.getSender(),currentPlayer));
                            response.add(MessageCenter.genMessage(MsgType.TEXT,lobbyName,"Disk to Island",currentPlayer.getName()+" has moved a "+((DiskToIsland_Cmd_Msg)inMessage).getColour()+" to "+inMessage.getContext()));
                            break;
                        case DISKTODININGROOM:
                            if (turnManager.getTurnState()!=TurnManager.TurnState.MOVE_STUDENTS_PHASE) {
                                throw new WrongPhaseException("Cannot move student disk during "+turnManager.getTurnState());
                            }
                            playerController.moveStudentToDiningRoom(currentPlayer,game.getPlayers(),((DiskToDiningRoom_Cmd_Msg)inMessage).getColour(),game.isExpertMode());
                            turnManager.nextAction();
                            response.add(MessageCenter.genMessage(MsgType.PLAYER,lobbyName,currentPlayer.getName()+" has moved a "+((DiskToDiningRoom_Cmd_Msg)inMessage).getColour()+" to their dining room",currentPlayer));
                            response.add(MessageCenter.genMessage(MsgType.TEXT,lobbyName,"Moved disk to dining room",currentPlayer.getName()+" has moved a "+((DiskToDiningRoom_Cmd_Msg)inMessage).getColour()+" to their dining room"));
                            break;
                        case MOVEMN:
                            if (turnManager.getTurnState()!=TurnManager.TurnState.MOVE_MOTHER_NATURE_PHASE) {
                                throw new WrongPhaseException("Cannot move mother nature during "+turnManager.getTurnState());
                            }
                            boolean updatePlayers=tileController.moveMotherNature(currentPlayer,((MoveMotherNature_Cmd_Msg)inMessage).getMoveAmount(),charCardController,game.getPlayers(),game.getCharacterCards(),game.getIslands(),playerController);
                            turnManager.nextAction();
                            response.add(MessageCenter.genMessage(MsgType.ISLANDS,lobbyName,"Moved mother nature",game.getIslands()));
                            if (updatePlayers) {
                                response.add(MessageCenter.genMessage(MsgType.PLAYERS,lobbyName,"Swapped team",game.getPlayers()));
                            }
                            response.add(MessageCenter.genMessage(MsgType.TEXT,lobbyName,"Move mother nature",currentPlayer.getName()+" has moved mother nature by "+((MoveMotherNature_Cmd_Msg)inMessage).getMoveAmount()+" spaces"));
                            break;
                        case CHOOSECLOUD:
                            if (turnManager.getTurnState()!=TurnManager.TurnState.CHOOSE_CLOUD_TILE_PHASE) {
                                throw new WrongPhaseException("Cannot choose cloud during  "+turnManager.getTurnState());
                            }
                            tileController.refillEntranceFromCloud(currentPlayer,((ChooseCloud_Cmd_Msg)inMessage).getCloudID(),game.getClouds());
                            if (currentPlayer==turnManager.getLastPlayer()) {
                                if (currentPlayer.getSchoolBoard().getHand().size()==0) {
                                    throw new GameIsOverException("No assistant cards left to play another round");
                                }
                                if (game.getBag().getFillAmount()<game.getClouds().get(0).getMaxSlots()*game.getClouds().size()) {
                                    throw new GameIsOverException("Student disks finished, not enough to fill clouds");
                                }
                                tileController.refillClouds(game.getClouds(),game.getBag());
                            }
                            turnManager.nextAction();
                            response.add(MessageCenter.genMessage(MsgType.CLOUDS,lobbyName,inMessage.getContext(),game.getClouds()));
                            response.add(MessageCenter.genMessage(MsgType.PLAYER,lobbyName,currentPlayer.getName(),currentPlayer));
                            response.add(MessageCenter.genMessage(MsgType.TEXT,lobbyName,"Chose cloud",currentPlayer.getName()+" has chosen "+inMessage.getContext()));
                            break;
                        case PLAYCHARCARD:
                            if (turnManager.getTurnState()==TurnManager.TurnState.ASSISTANT_CARDS_PHASE) {
                                throw new WrongPhaseException("Cannot play character cards  "+turnManager.getTurnState());
                            }
                            charCardController.activateCard(currentPlayer,((CharCard_Cmd_Msg)inMessage).getCharID(),game);
                            //TODO add immediate effects for specific cards
                            response.add(MessageCenter.genMessage(MsgType.CHARACTERCARDS,lobbyName,currentPlayer.getName()+" played "+((CharCard_Cmd_Msg)inMessage).getCharID(),game.getCharacterCards()));
                            response.add(MessageCenter.genMessage(MsgType.TEXT,lobbyName,"Played character card",currentPlayer.getName()+" played "+((CharCard_Cmd_Msg)inMessage).getCharID()));
                        default:
                            throw new MoveNotAllowedException("This should not be reachable");
                    }
                    break;
                default:
                    throw new ClassNotFoundException("Invalid message type");
            }
            response.add(MessageCenter.genMessage(MsgType.TEXT,lobbyName,"Switching turn state","Current phase: "+turnManager.getTurnState()+((turnManager.getTurnState().equals(TurnManager.TurnState.MOVE_STUDENTS_PHASE)?" (Moves left "+turnManager.getRemainingMoves()+")":"")+"\nCurrent player: "+turnManager.getCurrentPlayer().getName())));
        }
        catch (GameIsOverException gameOver) {
            Team winningTeam=calculateWinningTeam();
            System.out.println("["+lobbyName+"] GAME OVER - Team "+winningTeam+" has won");
            response.add(MessageCenter.genMessage(MsgType.GAMEOVER,lobbyName,"GAME OVER","Game is over: Team "+winningTeam+" has won"));
            response.add(MessageCenter.genMessage(MsgType.DISCONNECT,lobbyName,"GAME OVER",null));
        }
        catch (Exception e) {
            System.out.println("["+lobbyName+"] "+currentPlayer.getName()+"'s action has thrown "+e.getClass().getSimpleName());
            System.out.println(e.getMessage());
            e.printStackTrace();
            response.add(MessageCenter.genMessage(MsgType.ERROR,lobbyName,e.getClass().toString(),e.getMessage()));
        }
        return response;
    }
    public Team calculateWinningTeam() {
        EnumMap<Team,Integer> teamTowers=new EnumMap<>(Team.class);
        EnumMap<Team,Integer> teamProfessors=new EnumMap<>(Team.class);
        for (Team t : game.getTeams()) {
            teamTowers.put(t,0);
            teamProfessors.put(t,0);
        }
        for (Player p: game.getPlayers()) {
            teamTowers.put(p.getTeam(),teamTowers.get(p.getTeam())+p.getSchoolBoard().getTowers());
            for (Colour c: Colour.values()) {
                teamProfessors.put(p.getTeam(),teamProfessors.get(p.getTeam())+(p.getSchoolBoard().getProfessor(c)?1:0));
            }
        }
        Team winningTowers=Collections.min(teamTowers.entrySet(), Map.Entry.comparingByValue()).getKey();
        Team winningProfessors=Collections.min(teamProfessors.entrySet(), Map.Entry.comparingByValue()).getKey();
        for (Team t : game.getTeams()) {
            if (t!=winningTowers && teamTowers.get(t).intValue()==teamTowers.get(winningTowers).intValue()) {
                return winningProfessors;
            }
        }
       return winningTowers;
    }
}
