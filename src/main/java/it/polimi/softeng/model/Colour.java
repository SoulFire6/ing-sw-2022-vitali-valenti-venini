package it.polimi.softeng.model;

import java.util.EnumMap;
import java.util.Random;

public enum Colour {
    YELLOW,BLUE,GREEN,RED,PURPLE;

    public static EnumMap<Colour,Integer> genIntegerMap() {
        EnumMap<Colour,Integer> integerMap=new EnumMap<>(Colour.class);
        for (Colour c: Colour.values()) {
            integerMap.put(c,0);
        }
        return integerMap;
    }

    public static EnumMap<Colour,Boolean> genBooleanMap() {
        EnumMap<Colour,Boolean> booleanMap=new EnumMap<>(Colour.class);
        for (Colour c: Colour.values()) {
            booleanMap.put(c,false);
        }
        return booleanMap;
    }

    public static EnumMap<Colour,String> genStringMap() {
        EnumMap<Colour,String> stringMap=new EnumMap<>(Colour.class);
        for (Colour c: Colour.values()) {
            stringMap.put(c,null);
        }
        return stringMap;
    }
    public static Colour parseChosenColour(String input) {
        try {
            return Enum.valueOf(Colour.class,input.toUpperCase());
        }
        catch(IllegalArgumentException e) {
            //System.out.println("Invalid input");
            return null;
        }
    }

    public static Colour getRandomColour() {
        Colour[] values=Colour.values();
        Random rand=new Random();
        return values[rand.nextInt(values.length)];
    }
}
