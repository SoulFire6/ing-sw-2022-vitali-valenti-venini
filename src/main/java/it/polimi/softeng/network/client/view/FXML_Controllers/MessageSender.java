package it.polimi.softeng.network.client.view.FXML_Controllers;

import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
    This class provides a method to send messages for all fxml controller classes, to avoid duplicate code
 */

public class MessageSender {

    private final String sender;
    private final ObjectOutputStream toServer;

    public MessageSender(String sender, ObjectOutputStream toServer) {
        this.sender=sender;
        this.toServer=toServer;
    }
    public void sendMessage(MsgType type, String context, Object load) {
        try {
            toServer.writeObject(MessageCenter.genMessage(type, sender, context, load));
        } catch (IOException io) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error sending message to server");
            alert.showAndWait();
        } catch (NullPointerException npe) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Did not setup server connection properly");
            alert.showAndWait();
        }
    }

    public String getSender() {
        return this.sender;
    }
    public void closeConnection() throws IOException {
            this.toServer.close();
    }
}
