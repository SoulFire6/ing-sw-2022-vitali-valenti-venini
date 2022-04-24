package it.polimi.softeng.model;

public class CharacterCard {
    private final String cardID;
    private int cost;
    protected Object memory;

    public CharacterCard(String id,int cost) {
        this.cardID=id;
        this.cost=cost;
        this.memory=null;
    }
    public CharacterCard(String id, int cost, Object memory) {
        this(id,cost);
        this.memory=memory;
    }
    public String getCardID() {
        return this.cardID;
    }
    public int getCost() {
        return this.cost;
    }
    public void incrementCost() {
        this.cost+=1;
    }
    public Object getMemory() {
        System.out.println("Card has no memory");
        return null;
    }
    public void setMemory(Object mem) {
        this.memory=mem;
    }
}
