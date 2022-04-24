package it.polimi.softeng.model;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class AssistantCard {
    private String cardID;
    private int turnValue;
    private int motherNatureValue;

    public AssistantCard(String id, int turn, int motherNature) {
        this.cardID=id;
        this.turnValue=turn;
        this.motherNatureValue=motherNature;
    }
    public String getCardID() {
        return this.cardID;
    }
    public int getTurnValue() {
        return this.turnValue;
    }
    public int getMotherNatureValue() {
        return this.motherNatureValue;
    }
}
