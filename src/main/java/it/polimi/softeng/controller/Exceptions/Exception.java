package it.polimi.softeng.controller.Exceptions;

public class Exception extends Throwable{
    boolean CLI;                            //TODO: CLI/GUI
    public Exception(String message)
    {
        if(CLI)
            System.out.println(message);
        else{}                              //TODO: Error message on GUI
    }
}
