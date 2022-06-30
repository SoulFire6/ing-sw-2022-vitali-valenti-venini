package it.polimi.softeng.exceptions;
/**
 * This class defines the exception that occurs when a player tries to refill from an empty cloud
 */
public class TileEmptyException extends Exception {
    /**
     * Constructor of the class
     * @param message String that contains the error message
     */
    public TileEmptyException(String message)
    {
        super(message);
    }
}
