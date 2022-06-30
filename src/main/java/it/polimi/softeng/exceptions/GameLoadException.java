package it.polimi.softeng.exceptions;
/**
 * This class defines the exception that occurs when there's an error while loading the game
 */
public class GameLoadException extends Exception {
    /**
     * Constructor of the class
     * @param message String that contains the error message
     */
    public GameLoadException(String message) {
        super(message);
    }
}
