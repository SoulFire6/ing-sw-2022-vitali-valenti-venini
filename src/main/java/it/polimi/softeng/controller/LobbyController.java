package it.polimi.softeng.controller;

import it.polimi.softeng.exceptions.*;
import it.polimi.softeng.model.*;
import it.polimi.softeng.model.ReducedModel.*;
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
    private final AssistantCardController assistantCardController=new AssistantCardController();
    private final TileController tileController=new TileController();
    private final PlayerController playerController=new PlayerController();

    //Constructor for new game
    public LobbyController(ArrayList<String> playerNames, boolean expertMode, String lobbyName) throws InvalidPlayerNumException {
        this.lobbyName=lobbyName;
        if (expertMode) {
            this.charCardController = new CharCardController();
        } else {
            this.charCardController = null;
        }
        this.game=createGame(playerNames,expertMode);
        this.turnManager = new TurnManager(game.getPlayers(),game.getClouds().get(0).getMaxSlots());
    }
    //Constructor for loading a game
    public LobbyController(String lobbyName, ReducedGame save) {
        this.lobbyName=lobbyName;
        if(save.isExpertMode()) {
            this.charCardController = new CharCardController();
        } else {
            this.charCardController = null;
        }
        this.game=loadGame(save);
        this.turnManager=new TurnManager(game.getPlayers(),game.getClouds().get(0).getMaxSlots(),save);
    }
    private Game createGame(ArrayList<String> playerNames,boolean expertMode) throws InvalidPlayerNumException {
        Bag_Tile bag=new Bag_Tile(24);
        ArrayList<Player> players=playerController.genPlayers(playerNames);
        for (Player p: players) {
            p.setSchoolBoard(new SchoolBoard_Tile(p.getName(),7+2*(playerNames.size()%2),8-2*(playerNames.size()-2),8,assistantCardController.genHand(),expertMode?1:0));
            p.getSchoolBoard().fillEntrance(bag);
        }
        ArrayList<Team> teams = playerController.getTeams(players);
        ArrayList<Island_Tile> islands = tileController.genIslands(12,new Bag_Tile(2));
        ArrayList<Cloud_Tile> clouds = tileController.genClouds(playerNames.size(),3+playerNames.size()%2,bag);
        return new Game("Game",players,teams,bag,clouds,islands,expertMode,expertMode?20:0,expertMode?charCardController.genNewCharacterCards(3):null);
    }

    private void saveGame() {
        ReducedGame save=new ReducedGame(this.game,this.turnManager);
        //TODO write to file
    }
    private Game loadGame(ReducedGame reducedGame) {
        ArrayList<Player> players=new ArrayList<>();
        Bag_Tile bag=new Bag_Tile(0);
        ArrayList<Cloud_Tile> clouds=new ArrayList<>();
        ArrayList<Island_Tile> islands=new ArrayList<>();
        ArrayList<CharacterCard> characterCards;
        //LOAD PLAYERS
        for (ReducedPlayer reducedPlayer : reducedGame.getPlayers()) {
            Player player=new Player(reducedPlayer.getName(),reducedPlayer.getTeam());
            ArrayList<AssistantCard> hand=new ArrayList<>();
            for (ReducedAssistantCard card : reducedPlayer.getSchoolBoard().getHand()) {
                hand.add(new AssistantCard(card.getId(),card.getTurnValue(), card.getMotherNatureValue()));
            }
            player.setSchoolBoard(new SchoolBoard_Tile(player.getName(),7+2*(reducedGame.getPlayers().size()%2),8-2*(reducedGame.getPlayers().size()-2),8,hand,reducedPlayer.getSchoolBoard().getCoins()));
            player.getSchoolBoard().setContents(reducedPlayer.getSchoolBoard().getEntrance());
            for (Colour c: Colour.values()) {
                reducedPlayer.getSchoolBoard().getDiningRoom().put(c,reducedPlayer.getSchoolBoard().getDiningRoom().get(c));
            }
            player.getSchoolBoard().setLastUsedCard(new AssistantCard(reducedPlayer.getSchoolBoard().getLastUsedCard().getId(),reducedPlayer.getSchoolBoard().getLastUsedCard().getTurnValue(),reducedPlayer.getSchoolBoard().getLastUsedCard().getMotherNatureValue()));
            players.add(player);
        }
        //LOAD BAG
        bag.setContents(reducedGame.getBag().getContents());
        //LOAD CLOUDS
        for (ReducedCloud reducedCloud : reducedGame.getClouds()) {
            Cloud_Tile cloud=new Cloud_Tile(reducedCloud.getId(),3+reducedGame.getPlayers().size()%2);
            cloud.setContents(reducedCloud.getContents());
            clouds.add(cloud);
        }
        //LOAD ISLANDS
        for (ReducedIsland reducedIsland : reducedGame.getIslands()) {
            Island_Tile island=new Island_Tile(reducedIsland.getID());
            island.setContents(reducedIsland.getContents());
            island.setTeam(reducedIsland.getTeam());
            island.setTowers(reducedIsland.getTowers());
            island.setMotherNature(reducedIsland.hasMotherNature());
            island.setNoEntry(reducedIsland.isNoEntry());
            islands.add(island);
        }
        //Linking islands
        for (int i=0; i<islands.size()-1; i++) {
            islands.get(i).setNext(islands.get(i+1));
            islands.get(i).getNext().setPrev(islands.get(i));
        }
        islands.get(0).setPrev(islands.get(islands.size()-1));
        islands.get(0).getPrev().setNext(islands.get(0));
        //LOAD CHARACTERCARDS
        if (charCardController!=null) {
            characterCards=new ArrayList<>();
            for (ReducedCharacterCard reducedCharacterCard : reducedGame.getCharacterCards()) {
                characterCards.add(charCardController.createCharacterCard(new String[]{reducedCharacterCard.getId(),String.valueOf(reducedCharacterCard.getCost())}));
                //TODO LOAD MEMORY
            }
        }
        return new Game("Game",players,playerController.getTeams(players),bag,clouds,islands, reducedGame.isExpertMode(), reducedGame.getCoins(),null);
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
        String actionMessage=null;
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
                            response.add(MessageCenter.genMessage(MsgType.TEXT,lobbyName,null,currentPlayer.getName()+" has disconnected"));
                            throw new GameIsOverException("");
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
                                throw new WrongPhaseException("Cannot use asssistant card during "+turnManager.getTurnState().getDescription());
                            }
                            assistantCardController.playAssistantCard(currentPlayer,((AssistCard_Cmd_Msg)inMessage).getAssistID(),turnManager.getPlayerOrder());
                            turnManager.nextAction();
                            response.add(MessageCenter.genMessage(MsgType.PLAYER,lobbyName,"Play assist card",currentPlayer));
                            actionMessage=currentPlayer.getName()+" has played "+currentPlayer.getSchoolBoard().getLastUsedCard().getCardID();
                            break;
                        case DISKTOISLAND:
                            if (turnManager.getTurnState()!=TurnManager.TurnState.MOVE_STUDENTS_PHASE) {
                                throw new WrongPhaseException("Cannot move student disk during "+turnManager.getTurnState().getDescription());
                            }
                            tileController.moveStudentsToIsland(currentPlayer,((DiskToIsland_Cmd_Msg)inMessage).getColour(),inMessage.getContext(),game.getIslands());
                            turnManager.nextAction();
                            response.add(MessageCenter.genMessage(MsgType.ISLANDS,lobbyName,inMessage.getContext(),game.getIslands()));
                            response.add(MessageCenter.genMessage(MsgType.PLAYER,lobbyName,inMessage.getSender(),currentPlayer));
                            actionMessage=currentPlayer.getName()+" has moved a "+((DiskToIsland_Cmd_Msg)inMessage).getColour()+" to "+inMessage.getContext();
                            break;
                        case DISKTODININGROOM:
                            if (turnManager.getTurnState()!=TurnManager.TurnState.MOVE_STUDENTS_PHASE) {
                                throw new WrongPhaseException("Cannot move student disk during "+turnManager.getTurnState().getDescription());
                            }
                            playerController.moveStudentToDiningRoom(currentPlayer,game.getPlayers(),((DiskToDiningRoom_Cmd_Msg)inMessage).getColour(),game.isExpertMode());
                            turnManager.nextAction();
                            response.add(MessageCenter.genMessage(MsgType.PLAYERS,lobbyName,"Disk to dining room",game.getPlayers()));
                            actionMessage=currentPlayer.getName()+" has moved a "+((DiskToDiningRoom_Cmd_Msg)inMessage).getColour().name().toLowerCase()+" student to their dining room";
                            break;
                        case MOVEMN:
                            if (turnManager.getTurnState()!=TurnManager.TurnState.MOVE_MOTHER_NATURE_PHASE) {
                                throw new WrongPhaseException("Cannot move mother nature during "+turnManager.getTurnState().getDescription());
                            }
                            boolean updatePlayers=tileController.moveMotherNature(currentPlayer,((MoveMotherNature_Cmd_Msg)inMessage).getMoveAmount(),charCardController,game.getPlayers(),game.getCharacterCards(),game.getIslands(),playerController);
                            turnManager.nextAction();
                            response.add(MessageCenter.genMessage(MsgType.ISLANDS,lobbyName,"Moved mother nature",game.getIslands()));
                            if (updatePlayers) {
                                response.add(MessageCenter.genMessage(MsgType.PLAYERS,lobbyName,"Swapped team",game.getPlayers()));
                            }
                            actionMessage=currentPlayer.getName()+" has moved mother nature by "+((MoveMotherNature_Cmd_Msg)inMessage).getMoveAmount()+" spaces";
                            break;
                        case CHOOSECLOUD:
                            if (turnManager.getTurnState()!=TurnManager.TurnState.CHOOSE_CLOUD_TILE_PHASE) {
                                throw new WrongPhaseException("Cannot choose cloud during  "+turnManager.getTurnState().getDescription());
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
                            actionMessage=currentPlayer.getName()+" has chosen "+inMessage.getContext();
                            break;
                        case PLAYCHARCARD:
                            if (!game.isExpertMode()) {
                                throw new MoveNotAllowedException("Game is not set to expert mode");
                            }
                            if (turnManager.getTurnState()==TurnManager.TurnState.ASSISTANT_CARDS_PHASE) {
                                throw new WrongPhaseException("Cannot play character cards  "+turnManager.getTurnState().getDescription());
                            }
                            charCardController.activateCard(currentPlayer,((CharCard_Cmd_Msg)inMessage).getCharID(),game);
                            //TODO add immediate effects for specific cards
                            response.add(MessageCenter.genMessage(MsgType.CHARACTERCARDS,lobbyName,currentPlayer.getName()+" played "+((CharCard_Cmd_Msg)inMessage).getCharID(),game.getCharacterCards()));
                            actionMessage=currentPlayer.getName()+" played "+((CharCard_Cmd_Msg)inMessage).getCharID();
                            break;
                        default:
                            throw new MoveNotAllowedException("This should not be reachable");
                    }
                    break;
                default:
                    throw new ClassNotFoundException("Invalid message type");
            }
            response.add(MessageCenter.genMessage(MsgType.TURNSTATE,lobbyName,actionMessage,new ReducedTurnState(turnManager)));

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
        saveGame();
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
