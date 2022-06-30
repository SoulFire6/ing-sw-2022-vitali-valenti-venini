package it.polimi.softeng.exceptions;
/**
     * This class defines the exception that occurs when a player tries to make a move when it's not their turn
 */
public class NotYourTurnException extends Exception{
    /**
     * Constructor of the class
     * @param message String that contains the error message
     */
    public NotYourTurnException(String message)
    {
        super(message);
    }
}
