package it.polimi.softeng.exceptions;
/**
 * This class defines the exception that occurs when a player tries to insert a student disk in a row of the dining room that is full
 */
public class DiningRoomFullException extends Exception{
    /**
     * Constructor of the class
     * @param message String that contains the colour of the student disk and the error message
     */
    public DiningRoomFullException(String message)
    {
        super(message);
    }
}
