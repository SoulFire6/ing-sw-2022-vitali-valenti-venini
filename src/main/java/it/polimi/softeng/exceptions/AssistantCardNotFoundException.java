package it.polimi.softeng.exceptions;

/**
 * This class defines the exception that occurs when a player tries to play an assistant card that isn't in his hand
 */

public class AssistantCardNotFoundException extends Exception {
    /**
     * Constructor of the class
     * @param message String that contains the ID of the assistant card and the error message
     */
    public AssistantCardNotFoundException(String message)
    {
        super(message);
    }
}
