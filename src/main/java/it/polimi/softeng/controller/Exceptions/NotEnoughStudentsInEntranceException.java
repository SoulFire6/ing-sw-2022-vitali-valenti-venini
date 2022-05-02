package it.polimi.softeng.controller.Exceptions;

public class NotEnoughStudentsInEntranceException extends Exception{
    public NotEnoughStudentsInEntranceException(String message)
    {
        super(message);
    }
}
