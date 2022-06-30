package it.polimi.softeng.exceptions;
/**
 * This class defines the exception that occurs when the number of players isn't valid
 * */
public class InvalidPlayerNumException extends Exception {
    /**
     * Constructor of the class
     * @param message String that contains the error message
     */
    public InvalidPlayerNumException(String message) {
        super(message);
    }
}
