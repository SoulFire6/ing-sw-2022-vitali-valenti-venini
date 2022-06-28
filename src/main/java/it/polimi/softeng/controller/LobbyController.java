package it.polimi.softeng.controller;

import it.polimi.softeng.exceptions.*;
import it.polimi.softeng.model.*;
import it.polimi.softeng.model.ReducedModel.*;
import it.polimi.softeng.network.message.command.*;
import it.polimi.softeng.network.message.*;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;

import java.io.*;
import java.lang.Exception;
import java.util.*;

public class LobbyController {

    /**
     Controller class that handles a game lobby
     */
    private final Game game;
    private final String lobbyName;
    private final TurnManager turnManager;
    private final CharCardController charCardController;
    private final AssistantCardController assistantCardController=new AssistantCardController();
    private final TileController tileController=new TileController();
    private final PlayerController playerController=new PlayerController();
    private final ObjectOutputStream saveFileStream;

    //Constructor for new game
    /**
     * This method is the constructor of the LobbyController class.
     * @param playerNames ArrayList<String> names of the players of current game
     * @param expertMode boolean set to false if the game is in normal mode, set to true if the game is in expert mode
     * @param lobbyName String used to identify the lobby when creating messages to send to clients
     * @param saveFile File where the game state is saved
     * @exception InvalidPlayerNumException when player number is outside the valid range (2-4)
     */
    public LobbyController(ArrayList<String> playerNames, boolean expertMode, String lobbyName, File saveFile) throws InvalidPlayerNumException {
        this.lobbyName=lobbyName;
        this.saveFileStream=setupFileStream(saveFile);
        this.charCardController=expertMode?new CharCardController():null;
        this.game=createGame(playerNames,expertMode);
        saveGame();
        this.turnManager = new TurnManager(game.getPlayers(),game.getClouds().get(0).getMaxSlots());
        saveGame();
    }

    /**
     * This method is the constructor of the LobbyController class when it's requested to load an existing game
     * @param lobbyName String used to identify the lobby
     * @param saveFile File from which the state of the game is loaded
     * @exception GameLoadException when there is any error with the loading of the state of the game from the file where it was saved
     */
    //Constructor for loading a game
    public LobbyController(String lobbyName, File saveFile) throws GameLoadException {
        this.lobbyName=lobbyName;
        ReducedGame reducedGame=loadGame(saveFile);
        this.game=convertSaveFile(reducedGame);
        saveGame();
        this.saveFileStream=setupFileStream(saveFile);
        this.charCardController=game!=null && game.isExpertMode()?new CharCardController():null;
        this.turnManager=game!=null?new TurnManager(game.getPlayers(),game.getClouds().get(0).getMaxSlots(),reducedGame):null;
    }

    /**
     * This method initializes the output stream to save the game
     * @param saveFile File where the state of the game is saved
     */
    private ObjectOutputStream setupFileStream(File saveFile) {
        if (saveFile==null) {
            return null;
        }
        ObjectOutputStream objectOutputStream;
        try {
            if (saveFile.isDirectory()) {
                objectOutputStream=new ObjectOutputStream(new ObjectOutputStream(new FileOutputStream(saveFile.getPath()+lobbyName+"_"+game.getPlayers().size()+"_"+(game.isExpertMode()?"expert":"normal"))));
            } else {
                objectOutputStream=new ObjectOutputStream(new FileOutputStream(saveFile));
            }
        }
        catch (IOException io) {
            objectOutputStream=null;
        }
        return objectOutputStream;
    }

    /**
     * This method is used to close the Stream with the file where the state of the game is saved
     */
    public void closeFileStream() {
        if (saveFileStream!=null) {
            try {
                saveFileStream.close();
                System.out.println("Closed stream");
            }
            catch (IOException io) {
                System.out.println("Error closing stream");
            }

        }
    }
    /**
     * This method creates a new Game object representing the actual Game
     * @param playerNames ArrayList<String> containing the names of the players
     * @param expertMode boolean set to 0 if the game isn't in expert mode, set to 1 if it is
     * @exception InvalidPlayerNumException when player number is outside the valid range (2-4)
     * @return Game returns the new Game object
     */
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
        return new Game("Game",players,teams,bag,clouds,islands,expertMode,expertMode?20:0,expertMode?charCardController.genNewCharacterCards(3,bag):null);
    }

    /**
     * This method is used to save the state of the Game to the File represented by saveFileStream
     */
    public void saveGame() {
        if (saveFileStream!=null) {
            System.out.println("SAVING GAME...");
            try {
                saveFileStream.writeObject(new ReducedGame(this.game,this.turnManager));
            }
            catch (IOException io) {
                System.out.println("COULD NOT SAVE GAME");
                io.printStackTrace();
            }
        }
    }
    /**
     * This method is used to load an existing game saved on a File
     * @param saveFile File from which the state of the game is loaded
     * @exception GameLoadException when there's any exception regarding the load of the game
     * @return ReducedGame a reduced Game model typically used by the clients
     * */
    private ReducedGame loadGame(File saveFile) throws GameLoadException {
        try {
            ObjectInputStream objectInputStream=new ObjectInputStream(new FileInputStream(saveFile));
            ReducedGame reducedGame=(ReducedGame) objectInputStream.readObject();
            objectInputStream.close();
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(new FileOutputStream(saveFile));
            objectOutputStream.writeObject(reducedGame);
            objectOutputStream.close();
            return reducedGame;
        }
        catch (IOException io) {
            io.printStackTrace();
            throw new GameLoadException(io.getMessage());
        }
        catch (ClassNotFoundException cnfe) {
            throw new GameLoadException("Class not found");
        }
    }

    /**
     * This method converts a ReducedGame object to a Game object representing the same game
     * @param reducedGame the ReducedGame to convert
     * @exception GameLoadException when there's any exception regarding the load of the game from the file where it was saved
     * @return Game the Game which represents the same game of the ReducedGame
     */
    private Game convertSaveFile(ReducedGame reducedGame) throws GameLoadException {
        if (reducedGame==null) {
            return null;
        }
        ArrayList<Player> players=new ArrayList<>();
        Bag_Tile bag=new Bag_Tile(0);
        ArrayList<Cloud_Tile> clouds=new ArrayList<>();
        ArrayList<Island_Tile> islands=new ArrayList<>();
        ArrayList<CharacterCard> characterCards=null;
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
            ReducedAssistantCard lastUsedCard=reducedPlayer.getSchoolBoard().getLastUsedCard();
            player.getSchoolBoard().setLastUsedCard(lastUsedCard==null?null:new AssistantCard(lastUsedCard.getId(),lastUsedCard.getTurnValue(), lastUsedCard.getMotherNatureValue()));
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
        //LOAD CHARACTER CARDS
        if (charCardController!=null) {
            characterCards=new ArrayList<>();
            CharacterCard card;
            CharID character;
            for (ReducedCharacterCard reducedCharacterCard : reducedGame.getCharacterCards()) {
                try {
                    character=CharID.valueOf(reducedCharacterCard.getCharID());
                    character.setMemory(reducedCharacterCard.getMemory());
                    characterCards.add(new CharacterCard(reducedCharacterCard.getId(),reducedCharacterCard.getCost(),character));
                }
                catch (IllegalArgumentException iae) {
                    throw new GameLoadException("Failed to load card with id "+reducedCharacterCard.getId());
                }
            }
        }
        return new Game("Game",players,playerController.getTeams(players),bag,clouds,islands, reducedGame.isExpertMode(), reducedGame.getCoins(),characterCards);
    }

    /**
     * Simple getter for the attribute game
     * @return Game
     */
    public Game getGame() {
        return this.game;
    }
    /**
     * Simple getter for the attribute turnManager
     * @return TurnManager
     */
    public TurnManager getTurnManager() {
        return this.turnManager;
    }
    /**
     * Simple getter for the attribute assistantCardController
     * @return AssistantCardController
     */
    public AssistantCardController getAssistantCardController() {
        return this.assistantCardController;
    }
    /**
     * Simple getter for the attribute characterCardController
     * @return CharCardController
     */
    public CharCardController getCharCardController() {
        return this.charCardController;
    }
    /**
     * Simple getter for the attribute tileController
     * @return TileController
     */
    public TileController getTileController() {
        return this.tileController;
    }
    /**
     * Simple getter for the attribute playerController
     * @return PlayerController
     */
    public PlayerController getPlayerController() {
        return playerController;
    }

    /**
     * This method is used to analyze the Message received by a Player and, if it's legal, to fullfill the Player's requests
     * @param inMessage the Message sent by the Player
     * @exception LobbyClientDisconnectedException when a client disconnects
     * @exception GameIsOverException when the game is over
     * @return ArrayList<Message> response the Messages of response to the Clients from the Server
     */
    public ArrayList<Message> parseMessage(Message inMessage) throws LobbyClientDisconnectedException, GameIsOverException {
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
                            response.add(MessageCenter.genMessage(MsgType.WHISPER,lobbyName,currentPlayer.getName(),"Sent message to "+recipient));
                            response.add(MessageCenter.genMessage(MsgType.WHISPER,currentPlayer.getName(),recipient,((Info_Message)inMessage).getInfo()));
                            return response;
                        case DISCONNECT:
                            throw new LobbyClientDisconnectedException(currentPlayer.getName());
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
                                throw new WrongPhaseException("Cannot use assistant card during "+turnManager.getTurnState().getDescription());
                            }
                            assistantCardController.playAssistantCard(currentPlayer,((AssistCard_Cmd_Msg)inMessage).getAssistID(),turnManager.getPlayerOrder());
                            if (currentPlayer==turnManager.getLastPlayer()) {
                                response.add(MessageCenter.genMessage(MsgType.PLAYERS,lobbyName,"Play assist card, phase over",game.getPlayers()));
                            } else {
                                response.add(MessageCenter.genMessage(MsgType.PLAYER,lobbyName,"Play assist card",currentPlayer));
                            }
                            turnManager.nextAction();

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
                            actionMessage=currentPlayer.getName()+" has moved a "+((DiskToIsland_Cmd_Msg)inMessage).getColour().name().toLowerCase()+" student to island "+inMessage.getContext();
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
                            if (charCardController!=null) {
                                charCardController.deactivateAllCards(game.getCharacterCards(),game.getPlayers());
                            }
                            response.add(MessageCenter.genMessage(MsgType.CLOUDS,lobbyName,inMessage.getContext(),game.getClouds()));
                            response.add(MessageCenter.genMessage(MsgType.PLAYERS,lobbyName,currentPlayer.getName(),game.getPlayers()));
                            actionMessage=currentPlayer.getName()+" has refilled from cloud "+inMessage.getContext();
                            break;
                        case PLAYCHARCARD:
                            if (!game.isExpertMode()) {
                                throw new MoveNotAllowedException("Game is not set to expert mode");
                            }
                            if (turnManager.getTurnState()==TurnManager.TurnState.ASSISTANT_CARDS_PHASE) {
                                throw new WrongPhaseException("Cannot play character cards  "+turnManager.getTurnState().getDescription());
                            }
                            CharacterCard playedCard=charCardController.findAndCheckCard(currentPlayer,((CharCard_Cmd_Msg)inMessage).getCharID(),game.getCharacterCards());
                            charCardController.playCharacterCard(playedCard,currentPlayer,inMessage.getContext().split(" "),this);
                            response.add(MessageCenter.genMessage(MsgType.CHARACTERCARDS,lobbyName,currentPlayer.getName()+" played "+((CharCard_Cmd_Msg)inMessage).getCharID(),game.getCharacterCards()));
                            actionMessage=currentPlayer.getName()+" played "+((CharCard_Cmd_Msg)inMessage).getCharID();
                            break;
                        default:
                            throw new MoveNotAllowedException("This should not be reachable");
                    }
                    saveGame();
                    break;
                default:
                    throw new ClassNotFoundException("Invalid message type");
            }
            response.add(MessageCenter.genMessage(MsgType.TURNSTATE,lobbyName,actionMessage,new ReducedTurnState(turnManager)));

        }
        catch (LobbyClientDisconnectedException lcde) {
            throw new LobbyClientDisconnectedException(lcde.getMessage());
        }
        catch (GameIsOverException gioe) {
            throw new GameIsOverException(gioe.getMessage());
        }
        catch (Exception e) {
            System.out.println("["+lobbyName+"] "+currentPlayer.getName()+"'s action has thrown "+e.getClass().getSimpleName());
            System.out.println(e.getMessage());
            e.printStackTrace();
            response.add(MessageCenter.genMessage(MsgType.ERROR,lobbyName,e.getClass().toString(),e.getMessage()));
        }
        return response;
    }
    /**
     * This method is used to calculate the winner Team when a GameIsOverException is raised
     * @return Team the winning team
     */
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
