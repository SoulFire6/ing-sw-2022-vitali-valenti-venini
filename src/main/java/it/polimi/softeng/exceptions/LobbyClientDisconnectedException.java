package it.polimi.softeng.exceptions;
/**
 * This class defines the exception that occurs when a player does disconnect
 */
public class LobbyClientDisconnectedException extends Exception {
    /**
     * Constructor of the class
     * @param message String that contains the error message
     */
    public LobbyClientDisconnectedException(String message) {
        super(message);
    }
}
