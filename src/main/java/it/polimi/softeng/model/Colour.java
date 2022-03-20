package it.polimi.softeng.model;

import java.util.EnumMap;
import java.util.Random;

public enum Colour {
    YELLOW,BLUE,GREEN,RED,PURPLE;

    public static EnumMap<Colour,Integer> genStudentMap() {
        EnumMap<Colour,Integer> studentMap=new EnumMap<>(Colour.class);
        for (Colour c: Colour.values()) {
            studentMap.put(c,0);
        }
        return studentMap;
    }

    public static EnumMap<Colour,Boolean> genProfessorMap() {
        EnumMap<Colour,Boolean> professorMap=new EnumMap<>(Colour.class);
        for (Colour c: Colour.values()) {
            professorMap.put(c,false);
        }
        return professorMap;
    }

    public static Integer currentlyFilledSlots(EnumMap<Colour,Integer> contents) {
        Integer res=0;
        for(Colour c: Colour.values()) {
            res+=contents.get(c);
        }
        return res;
    }

    public static Colour parseChosenColour(String input) {
        try {
            return Enum.valueOf(Colour.class,input.toUpperCase());
        }
        catch(Exception e) {
            System.out.println("Invalid input");
            return null;
        }
    }

    public static Colour getRandomColour() {
        Colour[] values=Colour.values();
        Random rand=new Random();
        return values[rand.nextInt(values.length)];
    }
}
