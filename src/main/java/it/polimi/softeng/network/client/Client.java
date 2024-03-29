package it.polimi.softeng.network.client;

import it.polimi.softeng.network.reducedModel.ReducedGame;
import it.polimi.softeng.network.client.view.CLI;
import it.polimi.softeng.network.client.view.GUI;
import it.polimi.softeng.network.client.view.View;
import it.polimi.softeng.network.message.Message;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;
import it.polimi.softeng.network.message.load.*;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Main class for client instances
 */
public class Client {
    private ReducedGame model;
    private final View view;
    private final ObjectInputStream fromServer;

    /**
     * Default constructor
     * @param args args to pass to set up connection
     */
    public Client(String[] args) {
        this.view=(args[3].equals("GUI")?new GUI():new CLI());
        new Thread(view).start();
        try {
            Thread.sleep(500);
        }
        catch (InterruptedException ignored) {
        }
        this.fromServer=view.setUpConnection(args);
    }

    /**
     * Main method that runs for client, it updates the model, but also sends messages to display on the view
     */
    public void start() {
        if (fromServer == null) {
            System.out.println("Error: did not initialise client first");
            return;
        }
        Message inMessage = null;
        try {
            while ((inMessage = (Message) fromServer.readObject()) != null && inMessage.getSubType() != MsgType.DISCONNECT) {
                parseMessageFromServer(inMessage);
            }
            fromServer.close();
        } catch (ClassNotFoundException | ClassCastException ce) {
            ce.printStackTrace();
            view.display(MessageCenter.genMessage(MsgType.ERROR,"","","Error reading message from server"));
        } catch (IOException io) {
            view.display(MessageCenter.genMessage(MsgType.ERROR,"","","Abrupt disconnection"));
        }
        if (inMessage != null && inMessage.getSubType() == MsgType.DISCONNECT) {
            view.display(inMessage);
        }
        view.closeConnection();
    }

    /**
     * This method parses message received and decides what to do with them
     * @param message the message to parse
     */
    public void parseMessageFromServer(Message message) {
        switch (message.getType()) {
            case INFO:
                view.display(message);
                break;
            case LOAD:
                switch (message.getSubType()) {
                    case GAME:
                        this.model=((Game_Load_Msg)message).getLoad();
                        this.model.addPropertyChangeListener(this.view);
                        this.model.notifyGameLoaded();
                        break;
                    case ISLANDS:
                        this.model.setIslands(((Island_Load_Msg)message).getLoad());
                        break;
                    case CLOUDS:
                        this.model.setClouds(((Cloud_Load_Msg)message).getLoad());
                        break;
                    case BAG:
                        this.model.setBag(((Bag_Load_Msg)message).getLoad());
                        break;
                    case PLAYER:
                        this.model.setPlayer(((Player_Load_Msg)message).getLoad());
                        break;
                    case PLAYERS:
                        this.model.setPlayers(((Players_Load_Msg)message).getLoad());
                        break;
                    case CHARACTERCARDS:
                        this.model.setCharacterCards(((CharCard_Load_Msg)message).getLoad());
                        break;
                    case COINS:
                        this.model.setCoins(((Coin_Load_Msg)message).getLoad());
                    case TURNSTATE:
                        if (this.model!=null)  {
                            this.model.setTurnState(((TurnState_Load_Msg)message).getLoad());
                        }
                        view.display(message);
                        break;
                }
                break;
            default:
                view.display(MessageCenter.genMessage(MsgType.ERROR,"ERROR: ","Error","Could not read server message"));
                break;
        }
    }
}
