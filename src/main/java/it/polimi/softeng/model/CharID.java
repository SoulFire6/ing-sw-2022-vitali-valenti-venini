package it.polimi.softeng.model;

public enum CharID {
    MONK(MemType.INTEGER_COLOUR_MAP,4),
    HERALD(MemType.NONE,null),
    MAGIC_POSTMAN(MemType.NONE,null),
    GRANDMA_HERBS(MemType.INTEGER,4),
    CENTAUR(MemType.NONE,null),
    JESTER(MemType.INTEGER_COLOUR_MAP,6),
    KNIGHT(MemType.NONE,null),
    SHROOM_VENDOR(MemType.BOOLEAN_COLOUR_MAP,null),
    MINSTREL(MemType.NONE,null),
    SPOILED_PRINCESS(MemType.INTEGER_COLOUR_MAP,4),
    THIEF(MemType.NONE,null),
    FARMER(MemType.PLAYER_COLOUR_MAP,null);

    CharID(MemType memType, Integer limit) {
        this.memType=memType;
        this.limit=limit;
    }

    public enum MemType {
        NONE(null),
        INTEGER(0),
        INTEGER_COLOUR_MAP(Colour.genIntegerMap()),
        BOOLEAN_COLOUR_MAP(Colour.genBooleanMap()),
        PLAYER_COLOUR_MAP(Colour.genPlayerMap());
        MemType(Object memory) {
            this.memory=memory;
        }
        private Object memory;
    }
    private final MemType memType;
    private final Integer limit;

    public MemType getMemType() {
        return this.memType;
    }
    public Integer getLimit() {
        return this.limit;
    }
    public Object getMemory() {
        return memType.memory;
    }
    public void setMemory(Object memory) {
        try {
            if (memory==null) {
                throw new IllegalArgumentException("Argument is null");
            }
            this.memType.memory=this.memType.memory.getClass().cast(memory);
        }
        catch (ClassCastException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
