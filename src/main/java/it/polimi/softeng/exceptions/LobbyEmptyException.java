package it.polimi.softeng.exceptions;
/**
 * This class defines the exception that occurs when the lobby is waiting for players to join the game, but no players are present in the lobby
 */
public class LobbyEmptyException extends Exception {
    /**
     * Constructor of the class
     * @param message String that contains the error message
     */
    public LobbyEmptyException(String message) {
        super(message);
    }
}
