package it.polimi.softeng.exceptions;
/**
 * This class defines the exception that occurs when a given player can't be found in the game's player list
 */
public class PlayerNotFoundException extends Exception {
    /**
     * Constructor of the class
     * @param message String that contains the error message
     */
    public PlayerNotFoundException(String message) {
        super(message);
    }
}
