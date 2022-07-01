package it.polimi.softeng.network.client.view;

import it.polimi.softeng.network.message.Message;

import java.beans.PropertyChangeListener;
import java.io.ObjectInputStream;

/**
 * This class defines a common interface for view classes CLI and GUI
 */
public interface View extends Runnable, PropertyChangeListener {
    /**
     * This method sets up a Socket connection
     * @param args default args to pass
     * @return ObjectInputStream the stream to receive messages from
     */
    ObjectInputStream setUpConnection(String[] args);

    /**
     * This method closes the Socket
     */
    void closeConnection();

    /**
     * This method parses message and displays it for view
     * @param message the message to parse
     */
    void display(Message message);
}
