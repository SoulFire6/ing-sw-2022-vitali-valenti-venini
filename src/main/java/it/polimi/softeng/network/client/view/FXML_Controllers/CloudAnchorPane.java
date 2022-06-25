package it.polimi.softeng.network.client.view.FXML_Controllers;

import javafx.beans.NamedArg;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class CloudAnchorPane extends AnchorPane {
    private final String cloudID;
    public CloudAnchorPane(@NamedArg("cloud-id") String id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Assets/GUI/fxml/Cloud.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
            this.cloudID=id;
        }
        catch (IOException io) {
            throw new RuntimeException(io);
        }

    }
    public String getCloudID() {
        return this.cloudID;
    }
}
