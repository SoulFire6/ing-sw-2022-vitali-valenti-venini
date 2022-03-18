package it.polimi.softeng;

import java.util.*;

public enum Colour {
    YELLOW,BLUE,GREEN,RED,PURPLE;
    public static EnumMap<Colour,Integer> genStudentMap() {
        EnumMap<Colour,Integer> studentMap=new EnumMap<>(Colour.class);
        studentMap.put(Colour.YELLOW,0);
        studentMap.put(Colour.BLUE,0);
        studentMap.put(Colour.GREEN,0);
        studentMap.put(Colour.RED,0);
        studentMap.put(Colour.PURPLE,0);
        return studentMap;
    }
    public static EnumMap<Colour,Boolean> genProfessorMap() {
        EnumMap<Colour,Boolean> professorMap=new EnumMap<>(Colour.class);
        professorMap.put(Colour.YELLOW,false);
        professorMap.put(Colour.BLUE,false);
        professorMap.put(Colour.GREEN,false);
        professorMap.put(Colour.RED,false);
        professorMap.put(Colour.PURPLE,false);
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
