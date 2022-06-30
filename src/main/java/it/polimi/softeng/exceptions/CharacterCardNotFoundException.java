package it.polimi.softeng.exceptions;
/**
 * This class defines the exception that occurs when a player tries to play a character card that can't be played
 */
public class CharacterCardNotFoundException extends Exception {
    /**
     * Constructor of the class
     * @param message String that contains the ID of the character card and the error message
     */
    public CharacterCardNotFoundException(String message)
    {
        super(message);
    }
}
