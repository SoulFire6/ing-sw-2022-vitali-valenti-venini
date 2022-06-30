package it.polimi.softeng.exceptions;
/**
 * This class defines the exception that occurs when a non-empty tile is getting refilled
 */
public class TileNotEmptyException extends Exception {
    /**
     * Constructor of the class
     * @param message String that contains the error message
     */
    public TileNotEmptyException(String message) {
        super(message);
    }
}
