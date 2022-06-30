package it.polimi.softeng.exceptions;
/**
 * This class defines the exception that occurs when a tile isn't found
 */
public class TileNotFoundException extends Exception {
    /**
     * Constructor of the class
     * @param message String that contains the error message
     */
    public TileNotFoundException(String message) {
        super(message);
    }
}
