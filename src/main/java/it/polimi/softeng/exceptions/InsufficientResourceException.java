package it.polimi.softeng.exceptions;
/**
 * This class defines the exception that occurs when a player isn't allowed to perform an operation due to a lack of resources
 */
public class InsufficientResourceException extends Exception {
    /**
     * Constructor of the class
     * @param message String that contains the error message
     */
    public InsufficientResourceException(String message)
    {
        super(message);
    }
}
