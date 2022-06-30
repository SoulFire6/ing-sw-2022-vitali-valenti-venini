package it.polimi.softeng.model;

import java.util.EnumMap;
import java.util.Random;

/**
 * This Enum Class defines the different colours of the student disks/professor pawns.
 */
public enum Colour {
    YELLOW("Yellow"),BLUE("Blue"),GREEN("Green"),RED("Red"),PURPLE("Purple");
    private final String name;

    /**
     * This is the constructor of the class.
     * @param name specifies the colour name
     */
    Colour(String name) {
        this.name=name;
    }

    /**
     * @return String the name of the colour as a String
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return an IntegerMap with every Colour as key and 0 as value for each Colour.
     */
    public static EnumMap<Colour,Integer> genIntegerMap() {
        EnumMap<Colour,Integer> integerMap=new EnumMap<>(Colour.class);
        for (Colour c: Colour.values()) {
            integerMap.put(c,0);
        }
        return integerMap;
    }

    /**
     * @return a BooleanMap with every Colour as key and false as value for each Colour
     */
    public static EnumMap<Colour,Boolean> genBooleanMap() {
        EnumMap<Colour,Boolean> booleanMap=new EnumMap<>(Colour.class);
        for (Colour c: Colour.values()) {
            booleanMap.put(c,false);
        }
        return booleanMap;
    }

    /**
     * @return a StringMap with every Colour as key and null as value for each Colour
     */
    public static EnumMap<Colour,String> genStringMap() {
        EnumMap<Colour,String> stringMap=new EnumMap<>(Colour.class);
        for (Colour c: Colour.values()) {
            stringMap.put(c,null);
        }
        return stringMap;
    }

    /**
     * This method accepts a string and returns the equivalent Colour
     * @param input String representing the colour to parse
     * @return Colour equivalent colour of the input String, null if the input isn't valid
     */
    public static Colour parseChosenColour(String input) {
        try {
            return Enum.valueOf(Colour.class,input.toUpperCase());
        }
        catch(IllegalArgumentException e) {
            //System.out.println("Invalid input");
            return null;
        }
    }

    /**
     * This methods return a random colour from the Colour enum values
     * @return Colour randomly chosen colour
     */
    public static Colour getRandomColour() {
        Colour[] values=Colour.values();
        Random rand=new Random();
        return values[rand.nextInt(values.length)];
    }
}
