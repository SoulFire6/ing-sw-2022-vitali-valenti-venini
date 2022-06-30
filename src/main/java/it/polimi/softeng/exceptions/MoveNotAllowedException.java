package it.polimi.softeng.exceptions;
/**
 * This class defines the exception that occurs when a player tries to perform a move that is not allowed in the current state of the game
 */
public class MoveNotAllowedException extends Exception{
    /**
     * Constructor of the class
     * @param message String that contains the error message
     */
    public MoveNotAllowedException(String message)
    {
        super(message);
    }


}
