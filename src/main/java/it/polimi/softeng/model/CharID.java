package it.polimi.softeng.model;

import it.polimi.softeng.controller.LobbyController;
import it.polimi.softeng.exceptions.GameIsOverException;
import it.polimi.softeng.exceptions.MoveNotAllowedException;

import java.util.EnumMap;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * This enumeration class defines the different types of character cards and their behaviours
 */

public enum CharID {
    MONK(MemType.INTEGER_COLOUR_MAP, 4) {
        /**
         * This method is responsible to activate the MONK card
         * @param p the player wanting to activate the card
         * @param charArgs, charArgs[0] specifies the Colour, charArgs[1] specifies the Island_Tile ID
         * @param controller the LobbyController of the current game
         * @throws MoveNotAllowedException if an error occurs during card's memory reading, the student disk of the specified colour isn't found or when the Args given are invalid
         */

        @Override
        public void activateCard(Player p, String[] charArgs, LobbyController controller) throws MoveNotAllowedException {
            Colour c;
            if (controller==null) {
                throw new MoveNotAllowedException("Missing controller");
            }
            if (charArgs.length<2) {
                throw new MoveNotAllowedException("Must specify colour and island tile");
            }
            c=Colour.parseChosenColour(charArgs[0]);
            if (c==null) {
                throw new MoveNotAllowedException(charArgs[0]+" is not a valid colour");
            }
            EnumMap<Colour,Integer> mem=getMemory(Integer.class);
            if (mem==null) {
                throw new MoveNotAllowedException("Error reading card memory");
            }
            if (mem.get(c)==0) {
                throw new MoveNotAllowedException("No "+c.name().toLowerCase()+" students on card");
            }
            Optional<Island_Tile> island=controller.getGame().getIslands().stream().filter(island_tile -> island_tile.getTileID().equalsIgnoreCase(charArgs[1]) || island_tile.getTileID().equalsIgnoreCase("Island_"+charArgs[1])).findFirst();
            if (island.isEmpty()) {
                throw new MoveNotAllowedException("Could not find island with id "+charArgs[1]);
            }
            island.get().getContents().put(c,island.get().getContents().get(c)+1);
            mem.put(c,mem.get(c)-1);
            getMemType().refillFromBag(controller.getGame().getBag());
        }
    },
    HERALD(MemType.NONE, null) {
        /**
         * This method is responsible to activate the HERALD card
         * @param p the player wanting to activate the card
         * @param charArgs contains the island Tile ID or number
         * @param controller the LobbyController of the current game
         * @throws MoveNotAllowedException when the island tile isn't specified/can't be found
         * @throws GameIsOverException when the game ends after this card is played
         */
        @Override
        public void activateCard(Player p, String[] charArgs, LobbyController controller) throws MoveNotAllowedException,GameIsOverException {
            if (charArgs.length==0) {
                throw new MoveNotAllowedException("Did not specify island tile");
            }
            Optional<Island_Tile> island=controller.getGame().getIslands().stream().filter(island_tile -> island_tile.getTileID().equalsIgnoreCase(charArgs[0]) || island_tile.getTileID().equalsIgnoreCase("Island_"+charArgs[0])).findFirst();
            if (island.isEmpty()) {
                throw new MoveNotAllowedException("Could not find island with id "+charArgs[0]);
            }
            controller.getTileController().calculateInfluence(p,island.get(),controller.getGame().getPlayers(),controller.getCharCardController(),controller.getGame().getCharacterCards(), controller.getPlayerController());
        }
    },
    MAGIC_POSTMAN(MemType.NONE, null) {
        /**
         * This method is responsible to activate the MAGIC_POSTMAN card
         * @param p the player wanting to activate the card
         * @param charArgs null
         * @param controller the LobbyController of the current game
         * @throws MoveNotAllowedException if the player tries to activate this card without having played an assistant card before
         */

        @Override
        public void activateCard(Player p, String[] charArgs, LobbyController controller) throws MoveNotAllowedException {
            AssistantCard lastUsedCard=p.getSchoolBoard().getLastUsedCard();
            if (lastUsedCard==null) {
                throw new MoveNotAllowedException("No card on school board");
            }
            p.getSchoolBoard().setLastUsedCard(new AssistantCard(lastUsedCard.getCardID(), lastUsedCard.getTurnValue(), lastUsedCard.getMotherNatureValue()+2));
        }
    },
    GRANDMA_HERBS(MemType.INTEGER, 4) {
        /**
         * This method is responsible to activate the GRANDMA_HERBS card
         * @param p the player wanting to activate the card
         * @param charArgs charArgs[0] contains the Island_Tile ID
         * @param controller the LobbyController of the current game
         * @throws MoveNotAllowedException if the specified Island already has a no entry tile, there aren't more no entry tiles available or the player didn't specify the island tile correctly
         */
        @Override
        public void activateCard(Player p, String[] charArgs, LobbyController controller) throws MoveNotAllowedException {
            if (charArgs.length==0) {
                throw new MoveNotAllowedException("Did not specify island tile");
            }
            if (getMemory().equals(0)) {
                throw new MoveNotAllowedException("No more no entry tiles on card");
            }
            Optional<Island_Tile> island=controller.getGame().getIslands().stream().filter(island_tile -> island_tile.getTileID().equalsIgnoreCase(charArgs[0]) || island_tile.getTileID().equalsIgnoreCase("Island_"+charArgs[0])).findFirst();
            if (island.isEmpty()) {
                throw new MoveNotAllowedException("Could not find island tile with id "+charArgs[0]);
            }
            if (island.get().getNoEntry()) {
                throw new MoveNotAllowedException("Island "+charArgs[0]+" already has a no entry tile");
            }
            island.get().setNoEntry(true);
            setMemory(getMemory()-1);
        }
    },
    CENTAUR(MemType.NONE, null) {
        /**
         * This method is responsible to activate the CENTAUR card
         * @param p the player wanting to activate the card
         * @param charArgs null
         * @param controller the LobbyController of the current game
         */
        @Override
        public void activateCard(Player p, String[] charArgs, LobbyController controller) {
            //DOES NOTHING
        }
    },
    JESTER(MemType.INTEGER_COLOUR_MAP, 6) {
        /**
         * This method is responsible to activate the JESTER card
         * @param p the player wanting to activate the card
         * @param charArgs contains up to three pairs of Strings representing student disk colours to swap between the player board's entrance and the card
         * @param controller the LobbyController of the current game
         * @throws MoveNotAllowedException if an error occurs during card's memory reading, charArgs parameter isn't well formatted, or when there aren't enough student disks to swap
         */
        @Override
        public void activateCard(Player p, String[] charArgs, LobbyController controller) throws MoveNotAllowedException {
            EnumMap<Colour,Integer> cardMem, entranceMem;
            Colour c1,c2;
            if (charArgs.length<2 || charArgs.length%2!=0 || charArgs.length>6) {
                throw new MoveNotAllowedException("Must specify up to three pairs of students to swap between this card and your entrance");
            }
            cardMem=getMemory(Integer.class);
            if (cardMem==null) {
                throw new MoveNotAllowedException("Error reading card memory, it cannot be used");
            }
            cardMem=cardMem.clone();
            entranceMem=p.getSchoolBoard().getContents().clone();
            for (int i=0; i<charArgs.length; i++) {
                c1=Colour.parseChosenColour(charArgs[i++]);
                c2=Colour.parseChosenColour(charArgs[i]);
                if (c1==null || c2==null) {
                    throw new MoveNotAllowedException("Wrong format for colour: "+(c1==null?charArgs[0]:charArgs[1]));
                }
                if (!(cardMem.get(c1)>0 && entranceMem.get(c2)>0)) {
                    throw new MoveNotAllowedException("Not enough disks to swap "+c1.name().toLowerCase()+" student from card with "+c2.name().toLowerCase()+" student from entrance");
                }
                cardMem.put(c1,cardMem.get(c1)-1);
                entranceMem.put(c1,entranceMem.get(c1)+1);
                cardMem.put(c2,cardMem.get(c2)+1);
                entranceMem.put(c2,entranceMem.get(c2)-1);
            }
            setMemory(cardMem);
            p.getSchoolBoard().setContents(entranceMem);
        }
    },
    KNIGHT(MemType.NONE, null) {
        /**
         * This method is responsible to activate the KNIGHT card
         * @param p the player wanting to activate the card
         * @param charArgs null
         * @param controller the LobbyController of the current game
         */
        @Override
        public void activateCard(Player p, String[] charArgs, LobbyController controller) {
            //DOES NOTHING
        }
    },
    SHROOM_VENDOR(MemType.BOOLEAN_COLOUR_MAP, null) {
        /**
         * This method is responsible to activate the SHROOM_VENDOR card
         * @param p the player wanting to activate the card
         * @param charArgs charArgs[0] specifies the colour that will be disabled towards the calculation of the influence during this turn
         * @param controller the LobbyController of the current game
         * @throws MoveNotAllowedException if an error occurs during card's memory reading, charArgs[0] isn't specified, isn't a valid colour or if the colour has already been disabled
         */
        @Override
        public void activateCard(Player p, String[] charArgs, LobbyController controller) throws MoveNotAllowedException {
            if (charArgs.length==0) {
                throw new MoveNotAllowedException("Did not specify colour");
            }
            Colour c=Colour.parseChosenColour(charArgs[0]);
            if (c==null) {
                throw new MoveNotAllowedException(charArgs[0]+" is not a valid colour");
            }
            EnumMap<Colour,Boolean> mem=getMemory(Boolean.class);
            if (mem==null) {
                throw new MoveNotAllowedException("Error reading memory, card cannot be used");
            }
            if (mem.get(c)) {
                throw new MoveNotAllowedException("Colour already disabled");
            }
            mem.put(c,true);
        }
    },
    MINSTREL(MemType.NONE, null) {
        /**
         * This method is responsible to activate the MINSTREL card
         * @param p the player wanting to activate the card
         * @param charArgs contains up to two pairs of Strings representing student disk colours to swap between the player's entrance and dining room
         * @param controller the LobbyController of the current game
         * @throws MoveNotAllowedException bad charArgs formatting or not enough student disks to swap
         */
        @Override
        public void activateCard(Player p, String[] charArgs, LobbyController controller) throws MoveNotAllowedException {
            Colour c1,c2;
            if (!(charArgs.length==2 || charArgs.length==4)) {
                throw new MoveNotAllowedException("Must swap up to 2 pairs between entrance and dining room");
            }
            EnumMap<Colour,Integer> entranceMem=p.getSchoolBoard().getContents().clone();
            EnumMap<Colour,Integer> diningMem=p.getSchoolBoard().getDiningRoom().clone();
            for (int i=0; i<charArgs.length; i++) {
                c1=Colour.parseChosenColour(charArgs[i++]);
                c2=Colour.parseChosenColour(charArgs[i]);
                if (c1==null || c2==null) {
                    throw new MoveNotAllowedException("Wrong format for colour: "+(c1==null?charArgs[0]:charArgs[1]));
                }
                if (!(entranceMem.get(c1)>0 && diningMem.get(c2)>0)) {
                    throw new MoveNotAllowedException("Not enough disks to swap "+c1.name().toLowerCase()+" student from card with "+c2.name().toLowerCase()+" student from entrance");
                }
                entranceMem.put(c1,entranceMem.get(c1)-1);
                diningMem.put(c1,diningMem.get(c1)+1);
                entranceMem.put(c2,entranceMem.get(c2)+1);
                diningMem.put(c2,diningMem.get(c2)-1);
            }
            p.getSchoolBoard().setContents(entranceMem);
            p.getSchoolBoard().setDiningRoom(diningMem);
        }
    },
    SPOILED_PRINCESS(MemType.INTEGER_COLOUR_MAP, 4) {
        /**
         * This method is responsible to activate the SPOILED_PRINCESS card
         * @param p the player wanting to activate the card
         * @param charArgs charArgs[0] contains the Colour of the student disk that the player p wants to move from the card to his dining room
         * @param controller the LobbyController of the current game
         * @throws MoveNotAllowedException if an error occurs during card's memory reading, Args[0] isn't valid, if dining room has reached max. capacity for the specified colour or if the card hasn't any student disk of the specified colour on it
         */
        @Override
        public void activateCard(Player p, String[] charArgs, LobbyController controller) throws MoveNotAllowedException {
            if (charArgs.length==0) {
                throw new MoveNotAllowedException("Must specify colour");
            }
            Colour c=Colour.parseChosenColour(charArgs[0]);
            EnumMap<Colour,Integer> cardMem,diningMem;
            if (c==null) {
                throw new MoveNotAllowedException(charArgs[0]+" is not a valid colour");
            }
            cardMem=getMemory(Integer.class);
            if (cardMem==null) {
                throw new MoveNotAllowedException("Error reading card memory, cannot be used");
            }
            diningMem=p.getSchoolBoard().getDiningRoom();
            if (cardMem.get(c)==0) {
                throw new MoveNotAllowedException("Not enough "+c.name().toLowerCase()+" students on card ");
            }
            if (diningMem.get(c)==10) {
                throw new MoveNotAllowedException("Dining room has reached capacity for colour "+c.name().toLowerCase());
            }
            cardMem.put(c,cardMem.get(c)-1);
            diningMem.put(c,diningMem.get(c)+1);
            getMemType().refillFromBag(controller.getGame().getBag());
        }
    },
    THIEF(MemType.NONE, null) {
        /**
         * This method is responsible to activate the THIEF card
         * @param p the player wanting to activate the card
         * @param charArgs charArgs[0] specifies the Colour of the student disks that will get removed from players dining rooms
         * @param controller the LobbyController of the current game
         * @throws MoveNotAllowedException if charArgs[0] doesn't specify a Colour
         */
        @Override
        public void activateCard(Player p, String[] charArgs, LobbyController controller) throws MoveNotAllowedException {
            if (charArgs.length==0) {
                throw new MoveNotAllowedException("Need to specify colour");
            }
            Colour c=Colour.parseChosenColour(charArgs[0]);
            if (c==null) {
                throw new MoveNotAllowedException(charArgs[0]+" is not a colour");
            }
            Bag_Tile bag=controller.getGame().getBag();
            for (Player player : controller.getGame().getPlayers()) {
                int removedAmount=Math.min(3,player.getSchoolBoard().getDiningRoomAmount(c));
                bag.getContents().put(c,bag.getContents().get(c)+removedAmount);
                player.getSchoolBoard().getDiningRoom().put(c,player.getSchoolBoard().getDiningRoom().get(c)-removedAmount);
            }
        }
    },
    FARMER(MemType.PLAYER_COLOUR_MAP, null) {
        /**
         * This method is responsible to activate the FARMER card
         * @param p the player wanting to activate the card
         * @param charArgs null
         * @param controller the LobbyController of the current game
         * @throws MoveNotAllowedException if an error occurs during card's memory reading or if multiple players have the professor of the specified colour
         */
        @Override
        public void activateCard(Player p, String[] charArgs, LobbyController controller) throws MoveNotAllowedException {
            EnumMap<Colour,Player> mem=getMemory(Player.class);
            if (mem==null) {
                throw new MoveNotAllowedException("Error reading memory, card cannot be played");
            }
            for (Colour c : Colour.values()) {
                Stream<Player> professorPlayers=controller.getGame().getPlayers().stream().filter(player -> player.getSchoolBoard().getProfessor(c));
                if (professorPlayers.count()>1) {
                    throw new MoveNotAllowedException("Error: multiple players have the professor of colour "+c.name().toLowerCase());
                }
                if (professorPlayers.findFirst().isPresent()) {
                    mem.put(c,professorPlayers.findFirst().get());
                }
            }
        }
    };

    /**
     * This is the class constructor.
     * @param memType Character card memory type
     * @param limit Integer number of maximum student disks that can be stored in the card's memory
     * @see MemType
     */

    CharID(MemType memType, Integer limit) {
        this.memType = memType;
        if (memType.equals(MemType.INTEGER)) {
            memType.memory=limit;
        }
        this.memType.limit = limit;
    }

    /**
     * This is an enumeration defining the different types of memory a character card can have
     *
     */
    public enum MemType {
        NONE(),
        INTEGER(0),
        INTEGER_COLOUR_MAP(Colour.genIntegerMap()),
        BOOLEAN_COLOUR_MAP(Colour.genBooleanMap()),
        PLAYER_COLOUR_MAP(Colour.genStringMap());

        /**
         * Constructor of the Enum Class MemType
         * @param memory Colour map that can be INTEGER_COLOUR_MAP,BOOLEAN_COLOUR_MAP or PLAYER_COLOUR_MAP
         */

        MemType(Object memory) {
            this.memory = memory;
        }

        /**
         * Default constructor
         */
        MemType() {
            this.memory = null;
        }

        private Object memory;
        private Integer limit;

        /**
         * This method adds student disks from the bag to the character card memory
         * @param bag the Bag_Tile from which to draw the student disks
         */
        public void refillFromBag(Bag_Tile bag) {
            int fillAmount=0;
            if (this.equals(MemType.INTEGER_COLOUR_MAP)) {
                @SuppressWarnings("unchecked")
                EnumMap<Colour,Integer> memory=(EnumMap<Colour, Integer>) this.memory;
                for (Colour c : Colour.values()) {
                    fillAmount+=memory.get(c);
                }
                EnumMap<Colour,Integer> drawnStudents=bag.drawStudents(limit-fillAmount);
                for (Colour c : Colour.values()) {
                    memory.put(c,memory.get(c)+drawnStudents.get(c));
                }
            }
        }
    }


    /**
     * @param p player that wants to activate the card
     * @param charArgs arguments needed to the card activation, based on the specific character card
     * @param controller the LobbyController of the current game
     * @throws MoveNotAllowedException for any problem that doesn't allow the player move to be performed
     * @throws GameIsOverException when the game ends
     */
    public abstract void activateCard(Player p, String[] charArgs, LobbyController controller) throws MoveNotAllowedException, GameIsOverException;

    private final MemType memType;

    /**
     * @return the memory type of the character card
     */

    public MemType getMemType() {
        return this.memType;
    }

    /**
     * @return Integer memory limit of this character card
     */

    public Integer getLimit() {
        return this.memType.limit;
    }

    /**
     * @return Integer current memory of this character card
     */

    public Integer getMemory() {
        try {
            return (Integer) this.memType.memory;
        } catch (ClassCastException cce) {
            return null;
        }
    }

    /**
     * @param valueClass represents the V class contained in the EnumMap representing the memory
     * @param <V> the class to cast to
     * @return Colour EnumMap of V, with V based on which memType is the memory, null if valueClass isn't valid
     */
    public <V> EnumMap<Colour, V> getMemory(Class<V> valueClass) {
        try {
            @SuppressWarnings("unchecked")
            EnumMap<Colour, V> testMap = (EnumMap<Colour, V>) memType.memory;
            for (Colour c : Colour.values()) {
                if (!testMap.get(c).getClass().equals(valueClass)) {
                    throw new ClassCastException("Value was not of class " + valueClass);
                }
            }
            return testMap;
        } catch (ClassCastException cce) {
            return null;
        }
    }


    /**
     * @return the memory as an Object instance
     */
    public Object getUncastedMemory() {
        return this.memType.memory;
    }

    /**
     * @param memory memory as Object instance, which will be set as the memory of the character card
     */
    public void setMemory(Object memory) {
        this.memType.memory = memory;
    }
}
