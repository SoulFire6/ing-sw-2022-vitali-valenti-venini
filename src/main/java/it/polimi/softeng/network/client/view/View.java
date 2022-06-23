package it.polimi.softeng.network.client.view;

import it.polimi.softeng.network.message.MsgType;

import java.beans.PropertyChangeListener;
import java.io.ObjectInputStream;

public interface View extends Runnable, PropertyChangeListener {
    ObjectInputStream setUpConnection(String[] args);
    void closeConnection();
    void display(String message, String context, MsgType displayType);
}
