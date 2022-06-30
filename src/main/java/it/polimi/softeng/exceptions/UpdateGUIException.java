package it.polimi.softeng.exceptions;
/**
 * This class defines the exception that occurs when there is an error in the GUI update
 */
public class UpdateGUIException extends Exception {
    /**
     * Constructor of the class
     * @param message String that contains the error message
     */
    public UpdateGUIException(String message) {
        super(message);
    }
}
