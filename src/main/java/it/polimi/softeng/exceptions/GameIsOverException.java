package it.polimi.softeng.exceptions;
/**
 * This class defines the exception that is raised when the game is over
 */
public class GameIsOverException extends Exception {
    /**
     * Constructor of the class
     * @param message String that contains the reason why the game is over
     */
    public GameIsOverException(String message) {
        super(message);
    }
}
