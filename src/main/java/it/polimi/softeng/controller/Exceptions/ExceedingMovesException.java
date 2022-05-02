package it.polimi.softeng.controller.Exceptions;

public class ExceedingMovesException extends Exception{
    public ExceedingMovesException(String message)
    {
        super(message);
    }
}
