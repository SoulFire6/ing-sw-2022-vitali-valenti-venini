package it.polimi.softeng.model;

import it.polimi.softeng.controller.LobbyController;
import it.polimi.softeng.exceptions.GameIsOverException;
import it.polimi.softeng.exceptions.MoveNotAllowedException;

import java.util.EnumMap;
import java.util.Optional;
import java.util.stream.Stream;

public enum CharID {
    MONK(MemType.INTEGER_COLOUR_MAP, 4) {
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
            Optional<Island_Tile> island=controller.getGame().getIslands().stream().filter(island_tile -> island_tile.getTileID().equalsIgnoreCase(charArgs[0]) || island_tile.getTileID().equalsIgnoreCase("Island_"+charArgs[0])).findFirst();
            if (island.isEmpty()) {
                throw new MoveNotAllowedException("Could not find island with id "+charArgs[1]);
            }
            island.get().getContents().put(c,island.get().getContents().get(c)+1);
            mem.put(c,mem.get(c)-1);
            getMemType().refillFromBag(controller.getGame().getBag());
        }
    },
    HERALD(MemType.NONE, null) {
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
        @Override
        public void activateCard(Player p, String[] charArgs, LobbyController controller) {
            //DOES NOTHING
        }
    },
    JESTER(MemType.INTEGER_COLOUR_MAP, 6) {
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
        @Override
        public void activateCard(Player p, String[] charArgs, LobbyController controller) {
            //DOES NOTHING
        }
    },
    SHROOM_VENDOR(MemType.BOOLEAN_COLOUR_MAP, null) {
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

    CharID(MemType memType, Integer limit) {
        this.memType = memType;
        if (memType.equals(MemType.INTEGER)) {
            memType.memory=limit;
        }
        this.memType.limit = limit;
    }

    public enum MemType {
        NONE(),
        INTEGER(0),
        INTEGER_COLOUR_MAP(Colour.genIntegerMap()),
        BOOLEAN_COLOUR_MAP(Colour.genBooleanMap()),
        PLAYER_COLOUR_MAP(Colour.genStringMap());

        MemType(Object memory) {
            this.memory = memory;
        }

        MemType() {
            this.memory = null;
        }

        private Object memory;
        private Integer limit;

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

    public abstract void activateCard(Player p, String[] charArgs, LobbyController controller) throws MoveNotAllowedException, GameIsOverException;

    private final MemType memType;

    public MemType getMemType() {
        return this.memType;
    }

    public Integer getLimit() {
        return this.memType.limit;
    }

    public Integer getMemory() {
        try {
            return (Integer) this.memType.memory;
        } catch (ClassCastException cce) {
            return null;
        }
    }

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

    public Object getUncastedMemory() {
        return this.memType.memory;
    }

    public void setMemory(Object memory) {
        this.memType.memory = memory;
    }
}
