package it.polimi.softeng.exceptions;
/**
 * This class defines the exception that occurs when a player tries to move mother nature further than what he actually can
 */
public class ExceededMaxMovesException extends Exception {
    /**
     * Constructor of the class
     * @param message String that contains the error message
     */
    public ExceededMaxMovesException(String message)
    {
        super(message);
    }
}
