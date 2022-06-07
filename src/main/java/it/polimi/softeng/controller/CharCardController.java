package it.polimi.softeng.controller;

import it.polimi.softeng.exceptions.CharacterCardNotFoundException;
import it.polimi.softeng.exceptions.GameIsOverException;
import it.polimi.softeng.exceptions.InsufficientResourceException;
import it.polimi.softeng.exceptions.MoveNotAllowedException;
import it.polimi.softeng.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Objects;
import java.util.Random;

public class CharCardController {
    private static final String CARD_DATA_PATH="/CardData/CharacterCards.csv";
    public void activateCard(Player p,String cardID, String charArgs, LobbyController controller) throws CharacterCardNotFoundException, InsufficientResourceException, MoveNotAllowedException, GameIsOverException {
        CharacterCard playedCard = null;
        for (CharacterCard card : controller.getGame().getCharacterCards()) {
            if (card.getCardID().equals(cardID)) {
                playedCard = card;
                break;
            }
        }
        if (playedCard == null) {
            throw new CharacterCardNotFoundException("Character with id " + cardID + " is not in play");
        }
        if (p.getSchoolBoard().getCoins() < playedCard.getCost()) {
            throw new InsufficientResourceException("Not enough coins (" + p.getSchoolBoard().getCoins() + "/" + playedCard.getCost() + ")");
        }
        if (playedCard.isActive()) {
            throw new MoveNotAllowedException("Card is already active");
        }
        playCard(p,playedCard,charArgs.split(" "),controller);
        p.getSchoolBoard().setCoins(p.getSchoolBoard().getCoins()-playedCard.getCost());
        playedCard.setActive(true);
        playedCard.incrementCost();
    }
    //Deactivates all cards (to be used at end of turn when effect for all cards ends)
    public void deactivateAllCards(ArrayList<CharacterCard> cards, ArrayList<Player> players) {
        for (CharacterCard card : cards) {
            card.setActive(false);
            switch (card.getCharacter().getMemType()) {
                case BOOLEAN_COLOUR_MAP:
                    card.getCharacter().setMemory(Colour.genBooleanMap());
                    break;
                case PLAYER_COLOUR_MAP:
                    if (players!=null) {
                        EnumMap<Colour,Player> mem=(EnumMap<Colour, Player>) card.getCharacter().getMemory();
                        for (Colour c : Colour.values()) {
                            for (Player p: players) {
                                p.getSchoolBoard().setProfessor(c, p.equals(mem.get(c)));
                            }
                        }
                    }
                    card.getCharacter().setMemory(Colour.genPlayerMap());
                    break;
                default:
                    break;
            }
        }
    }

    public ArrayList<CharacterCard> genNewCharacterCards(int num, Bag_Tile bag) {
        Random rand=new Random();
        ArrayList<CharacterCard> res=new ArrayList<>();
        String line;
        String[] card;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(CARD_DATA_PATH))));
            //Skipping header of csv file
            reader.readLine();
            while((line=reader.readLine())!=null){
                card=line.split(",");
                try {
                    res.add(new CharacterCard(card[0],Integer.parseInt(card[1]),CharID.valueOf(card[2])));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //Removing excess cards
            while (res.size()>num) {
                res.remove(res.get(rand.nextInt(res.size())));
            }
            if (res.size()!=num) {
                return null;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bag!=null) {
            setUpCards(bag, res);
        }
        return res;
    }
    public void setUpCards(Bag_Tile b, ArrayList<CharacterCard> cards) {
        for (CharacterCard card: cards) {
            switch (card.getCharacter().getMemType()) {
                case INTEGER:
                    card.getCharacter().setMemory(card.getCharacter().getLimit());
                    break;
                case INTEGER_COLOUR_MAP:
                    card.getCharacter().setMemory(b.drawStudents(card.getCharacter().getLimit()));
                    break;
                default:
                    break;
            }
        }
    }

    public boolean getActiveStatus(CharID id, ArrayList<CharacterCard> cards) {
        for (CharacterCard card : cards) {
            if (card.getCharacter().equals(id)) {
                return card.isActive();
            }
        }
        return false;
    }
    public void playCard(Player p, CharacterCard card, String[] charArgs, LobbyController controller) throws MoveNotAllowedException, GameIsOverException {
        Colour c1,c2;
        EnumMap<Colour,Integer> mem1,mem2;
        switch (card.getCharacter()) {
            case MONK:
                if (charArgs.length<2) {
                    throw new MoveNotAllowedException("Must specify colour and island tile");
                }
                try {
                    c1=Colour.valueOf(charArgs[0]);
                    EnumMap<Colour,Integer> cardContents=(EnumMap<Colour, Integer>) card.getCharacter().getMemory();
                    if (cardContents.get(c1)==0) {
                        throw new MoveNotAllowedException("No "+c1.name().toLowerCase()+" students on card");
                    }
                    for (Island_Tile island : controller.getGame().getIslands()) {
                        if (island.getTileID().equalsIgnoreCase(charArgs[1]) || island.getTileID().equalsIgnoreCase("Island_"+charArgs[1])) {
                            island.getContents().put(c1,island.getContents().get(c1)+1);
                            cardContents.put(c1,cardContents.get(c1)-1);
                            return;
                        }
                    }
                    throw new MoveNotAllowedException("Could not find island with id "+charArgs[1]);
                }
                catch (IllegalArgumentException iae) {
                    throw new MoveNotAllowedException(charArgs[0]+" is not a valid colour");
                }
            case HERALD:
                if (charArgs.length==0) {
                    throw new MoveNotAllowedException("Did not specify island tile");
                }
                for (Island_Tile island : controller.getGame().getIslands()) {
                    if (island.getTileID().equalsIgnoreCase(charArgs[1]) || island.getTileID().equalsIgnoreCase("Island_"+charArgs[1])) {
                        controller.getTileController().calculateInfluence(p,island,controller.getGame().getPlayers(),this,controller.getGame().getCharacterCards(),controller.getPlayerController());
                        return;
                    }
                }
                throw new MoveNotAllowedException("Could not find island tile with id "+charArgs[0]);
            case MAGIC_POSTMAN:
                AssistantCard lastUsedCard=p.getSchoolBoard().getLastUsedCard();
                p.getSchoolBoard().setLastUsedCard(new AssistantCard(lastUsedCard.getCardID(), lastUsedCard.getTurnValue(), lastUsedCard.getMotherNatureValue()+2));
                break;
            case GRANDMA_HERBS:
                if (charArgs.length==0) {
                    throw new MoveNotAllowedException("Did not specify island tile");
                }
                if ((Integer)card.getCharacter().getMemory()==0) {
                    throw new MoveNotAllowedException("No more no entry tiles on "+card.getCardID());
                }
                for (Island_Tile island : controller.getGame().getIslands()) {
                    if (island.getTileID().equalsIgnoreCase(charArgs[0]) || island.getTileID().equalsIgnoreCase("Island_"+charArgs[0])) {
                        if (island.getNoEntry()) {
                            throw new MoveNotAllowedException("Island "+charArgs[0]+" already has a no entry tile");
                        }
                        island.setNoEntry(true);
                        card.getCharacter().setMemory((Integer)card.getCharacter().getMemory()-1);
                        return;
                    }
                }
                throw new MoveNotAllowedException("Could not find island tile with id "+charArgs[0]);
            case JESTER:
                if (charArgs.length<2 || charArgs.length%2!=0 || charArgs.length>6) {
                    throw new MoveNotAllowedException("Must specify up to three pairs of students to swap between this card and your entrance");
                }
                //MEM 1 is card memory clone and MEM 2 is entrance clone
                mem1=((EnumMap<Colour, Integer>) card.getCharacter().getMemory()).clone();
                mem2=p.getSchoolBoard().getContents().clone();
                for (int i=0; i< charArgs.length; i++) {
                    c1=Colour.parseChosenColour(charArgs[i++]);
                    c2=Colour.parseChosenColour(charArgs[i]);
                    if (c1==null || c2==null) {
                        throw new MoveNotAllowedException("Wrong format for colour: "+(c1==null?charArgs[0]:charArgs[1]));
                    }
                    if (!(mem1.get(c1)>0 && mem2.get(c2)>0)) {
                        throw new MoveNotAllowedException("Not enough disks to swap "+c1.name().toLowerCase()+" student from card with "+c2.name().toLowerCase()+" student from entrance");
                    }
                    mem1.put(c1,mem1.get(c1)-1);
                    mem2.put(c1,mem2.get(c1)+1);
                    mem1.put(c2,mem2.get(c2)+1);
                    mem2.put(c2,mem2.get(c2)-1);
                }
                card.getCharacter().setMemory(mem1);
                p.getSchoolBoard().setContents(mem2);
                break;
            case SHROOM_VENDOR:
                if (charArgs.length==0) {
                    throw new MoveNotAllowedException("Did not specify colour");
                }
                c1=Colour.parseChosenColour(charArgs[0]);
                if (c1==null) {
                    throw new MoveNotAllowedException(charArgs[0]+" is not a valid colour");
                }
                EnumMap<Colour,Boolean> contents=(EnumMap<Colour, Boolean>) card.getCharacter().getMemory();
                if (contents.get(c1)) {
                    throw new MoveNotAllowedException("Colour already disabled");
                }
                contents.put(c1,true);
                break;
            case MINSTREL:
                if (!(charArgs.length==2 || charArgs.length==4)) {
                    throw new MoveNotAllowedException("Must swap up to 2 pairs between entrance and dining room");
                }
                //MEM1 is entrance clone, MEM2 is dining room clone
                mem1=p.getSchoolBoard().getContents().clone();
                mem2=p.getSchoolBoard().getDiningRoom().clone();
                for (int i=0; i< charArgs.length; i++) {
                    c1=Colour.parseChosenColour(charArgs[i++]);
                    c2=Colour.parseChosenColour(charArgs[i]);
                    if (c1==null || c2==null) {
                        throw new MoveNotAllowedException("Wrong format for colour: "+(c1==null?charArgs[0]:charArgs[1]));
                    }
                    if (!(mem1.get(c1)>0 && mem2.get(c2)>0)) {
                        throw new MoveNotAllowedException("Not enough disks to swap "+c1.name().toLowerCase()+" student from card with "+c2.name().toLowerCase()+" student from entrance");
                    }
                    mem1.put(c1,mem1.get(c1)-1);
                    mem2.put(c1,mem2.get(c1)+1);
                    mem1.put(c2,mem1.get(c2)+1);
                    mem2.put(c2,mem2.get(c2)-1);
                }
                p.getSchoolBoard().setContents(mem1);
                card.getCharacter().setMemory(mem2);
                break;
            case SPOILED_PRINCESS:
                if (charArgs.length==0) {
                    throw new MoveNotAllowedException("Must specify colour");
                }
                try {
                    c1=Colour.valueOf(charArgs[0]);
                    //MEM1 and MEM2 have direct access to contents of card and dining room
                    mem1=(EnumMap<Colour, Integer>) card.getCharacter().getMemory();
                    mem2=p.getSchoolBoard().getDiningRoom();
                    if (mem1.get(c1)==0) {
                        throw new MoveNotAllowedException("Not enough "+c1.name().toLowerCase()+" students on card "+card.getCardID());
                    }
                    if (mem2.get(c1)==10) {
                        throw new MoveNotAllowedException("Dining room has reached capacity for colour "+c1.name().toLowerCase());
                    }
                    mem1.put(c1,mem1.get(c1)-1);
                    mem2.put(c1,mem2.get(c1)+1);
                }
                catch (IllegalArgumentException iae) {
                    throw new MoveNotAllowedException(charArgs[0]+" is not a valid colour");
                }
                break;
            case THIEF:
                try {
                    c1=Colour.valueOf(charArgs[0]);
                    for (Player player : controller.getGame().getPlayers()) {
                        player.getSchoolBoard().getDiningRoom().put(c1,Math.max(0,player.getSchoolBoard().getDiningRoom().get(c1)-3));
                    }
                }
                catch (IllegalArgumentException iae) {
                    throw new MoveNotAllowedException(charArgs[0]+" is not a valid colour");
                }
                break;
            case FARMER:
                EnumMap<Colour,Player> mem3=Colour.genPlayerMap();
                for (Colour c : Colour.values()) {
                    for (Player player : controller.getGame().getPlayers()) {
                        if (player.getSchoolBoard().getProfessor(c)) {
                            if (mem3.get(c)!=null) {
                                throw new MoveNotAllowedException("Error: multiple players have the professor of colour "+c.name().toLowerCase());
                            }
                            mem3.put(c,player);
                        }
                    }
                }
                break;
            default:
                //CENTAUR, KNIGHT,only get activated
                break;
        }
    }
}
