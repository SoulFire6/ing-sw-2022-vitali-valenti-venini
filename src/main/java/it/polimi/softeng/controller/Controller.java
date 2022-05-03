package it.polimi.softeng.controller;

import it.polimi.softeng.exceptions.*;
import it.polimi.softeng.model.*;

import java.util.*;

public class Controller {
    private final Game game;
    private final TurnManager turnManager;
    private final CharCardController charCardController;
    private final AssistantCardController assistantCardController;
    private final TileController tileController;
    private final PlayerController playerController;
    int remainingMoves,maxMoves;

    public Controller(ArrayList<String> playerNames, boolean expertMode) {
        this.game=createGame(playerNames,expertMode);
        this.turnManager = new TurnManager(game.getPlayers());
        this.assistantCardController = new AssistantCardController();
        this.tileController = new TileController();
        this.playerController = new PlayerController();
        if(expertMode) {
            this.charCardController = new CharCardController();
        } else {
            this.charCardController = null;
        }
        this.maxMoves = this.remainingMoves = game.getClouds().get(0).getMaxSlots();
    }
    public Game createGame(ArrayList<String> playerNames,boolean expertMode) {
        Game game;
        //TODO: complete method
        Bag_Tile bag=new Bag_Tile(26);
        ArrayList<Player> players = playerController.genPlayers(playerNames);
        for (Player p: players) {
            p.setSchoolBoard(new SchoolBoard_Tile(p.getName(),10,8-2+(playerNames.size()-2),8,assistantCardController.genHand(null),expertMode?1:0));
        }
        ArrayList<Team> teams = playerController.getTeams(players);
        ArrayList<Island_Tile> islands = tileController.genIslands(12,bag);
        ArrayList<Cloud_Tile> clouds = tileController.genClouds(playerNames.size(),3+playerNames.size()%2,bag);
        game = new Game("Game",players,teams,bag,clouds,islands,expertMode,expertMode?20:0,expertMode?charCardController.genNewCharacterCards(3):null);
        return game;
    }
    /*
    public void playAssistantCard(Player p, AssistantCard assistantCard) throws NotYourTurnException, AssistantCardNotFoundException, MoveNotAllowedException {
        assistantCardController.playAssistantCard(p,assistantCard,turnManager);
    }

    public void playCharacterCard(CharacterCard characterCard, Player p) throws CharacterCardNotFoundException, NotEnoughCoinsException {
        charCardController.activateCard(p, characterCard,game);

    }

    public void moveStudentsToIsland(Player p, EnumMap<Colour,Integer> students , Island_Tile island_tile) throws NotYourTurnException, NotEnoughStudentsInEntranceException, MoveNotAllowedException {
        tileController.moveStudentsToIsland(p,students,island_tile,turnManager,game.getClouds().get(0).getMaxSlots());
    }

    public void moveMotherNature(Player p, int n) throws NotYourTurnException, MoveNotAllowedException, MotherNatureValueException {
        tileController.moveMotherNature(p,n,turnManager, game.getIslands());
    }

    public void cloudDraw(Player p, Cloud_Tile cloud_tile) throws NotYourTurnException, EmptyCloudTileException, MoveNotAllowedException {
        playerController.cloudDraw(p,cloud_tile,turnManager);
    }

    public void moveStudentsToDiningRoom(Player p, EnumMap<Colour,Integer> students) throws NotEnoughStudentsInEntranceException, NotEnoughSpaceInDiningRoomException, NotYourTurnException, MoveNotAllowedException {
        playerController.moveStudentsToDiningRoom(p,students,turnManager,game, game.getClouds().get(0).getMaxSlots());          //last parameter is the num of student discs that one player can move per turn
    }
        TODO: condense into message parser
    */










}
