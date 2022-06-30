package it.polimi.softeng.exceptions;

/**
 * This class defines the exception that occurs when a player tries to play an assistant card that they already played
 */
public class AssistantCardAlreadyPlayedException extends Exception {
    /**
     * Constructor of the class
     * @param message String that contains the ID of the assistant card and the error message
     */
    public AssistantCardAlreadyPlayedException(String message) {
        super(message);
    }
}
