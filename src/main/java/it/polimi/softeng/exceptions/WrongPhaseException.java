package it.polimi.softeng.exceptions;
/**
 * This class defines the exception that occurs when a player tries to perform an operation not allowed in the current phase of the game
 */
public class WrongPhaseException extends Exception{
    /**
     * Constructor of the class
     * @param message String that contains the error message
     */
    public WrongPhaseException(String message) {
        super(message);
    }
}
