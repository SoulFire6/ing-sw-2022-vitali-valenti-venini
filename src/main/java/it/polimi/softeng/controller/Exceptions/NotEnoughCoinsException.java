package it.polimi.softeng.controller.Exceptions;

public class NotEnoughCoinsException extends Exception{
    public NotEnoughCoinsException(String message)
    {
        super(message);
    }
}
