package it.polimi.softeng.controller;

import it.polimi.softeng.controller.Exceptions.*;
import it.polimi.softeng.model.*;

import java.util.*;

public class Controller {
    private final Game game;
    private final TurnManager turnManager;
    private final CharCardController charCardController;
    private final AssistantCardController assistantCardController;
    private final TileController islandController;
    private final PlayerController playerController;
    int remainingMoves,maxMoves;

    public Controller(ArrayList<String> playerNames, boolean expertMode) {
        Random rand=new Random();
        //TODO add better random game id generation
        //this.game=new Game(rand.nextInt(10000)+"_2022");
        this.game=createGame(playerNames,expertMode);
        turnManager = new TurnManager(game.getPlayers());
        assistantCardController = new AssistantCardController();
        islandController = new TileController();
        playerController = new PlayerController();
        if(expertMode) {
            charCardController = new CharCardController();
        } else {
            charCardController = null;
        }

        maxMoves = remainingMoves = game.getClouds().get(0).getMaxSlots();
    }
    public Game createGame(ArrayList<String> playerNames,boolean expertMode) {
        //TODO: complete method
        Game g;
        final int bagFill =26;
        final int islandNum =12;
        int cloudNum;
        final int coins = 20;
        final int charCardNum = 3;
        if(playerNames.size()==2||playerNames.size()==4)
            cloudNum=3;
        else
            cloudNum=4;
        ArrayList<Player> players = PlayerController.genTeams(playerNames);
        if(expertMode)
            g = new Game("GameID", players, bagFill, cloudNum,cloudNum,islandNum,coins, charCardController.genNewCharacterCards(charCardNum) );
        else
            g = new Game("GameID", players, bagFill, cloudNum,cloudNum,islandNum);
        return g;
    }
    public void playAssistantCard(Player p, AssistantCard assistantCard) throws NotYourTurnException, AssistantCardNotFoundException, MoveNotAllowedException {
        assistantCardController.playAssistantCard(p,assistantCard,turnManager);
    }

    public void playCharacterCard(CharacterCard characterCard, Player p) throws CharacterCardNotFoundException, NotEnoughCoinsException {
        charCardController.activateCard(p, characterCard,game);

    }

    public void moveStudentsToIsland(Player p, EnumMap<Colour,Integer> students , Island_Tile island_tile) throws NotYourTurnException, NotEnoughStudentsInEntranceException, MoveNotAllowedException {
        islandController.moveStudentsToIsland(p,students,island_tile,turnManager,game.getClouds().get(0).getMaxSlots());
    }

    public void moveMotherNature(Player p, int n) throws NotYourTurnException, MoveNotAllowedException, MotherNatureValueException {
        islandController.moveMotherNature(p,n,turnManager, game.getIslands());
    }

    public void cloudDraw(Player p, Cloud_Tile cloud_tile) throws NotYourTurnException, EmptyCloudTileException, MoveNotAllowedException {
        playerController.cloudDraw(p,cloud_tile,turnManager);
    }

    public void moveStudentsToDiningRoom(Player p, EnumMap<Colour,Integer> students) throws NotEnoughStudentsInEntranceException, NotEnoughSpaceInDiningRoomException, NotYourTurnException, MoveNotAllowedException {
        playerController.moveStudentsToDiningRoom(p,students,turnManager,game, game.getClouds().get(0).getMaxSlots());          //last parameter is the num of student discs that one player can move per turn
    }

    public Game getGame()
    {
        return game;
    }










}
