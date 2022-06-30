package it.polimi.softeng.exceptions;
/**
 * This class defines the exception that occurs when there is an error during the creation of the server
 */
public class ServerCreationException extends Exception {
    /**
     * Constructor of the class
     * @param message String that contains the error message
     */
    public ServerCreationException(String message) {
        super(message);
    }
}
