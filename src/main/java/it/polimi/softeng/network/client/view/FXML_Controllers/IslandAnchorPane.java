package it.polimi.softeng.network.client.view.FXML_Controllers;

import it.polimi.softeng.model.Colour;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;
import javafx.beans.NamedArg;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.EnumMap;
import java.util.Objects;

public class IslandAnchorPane extends AnchorPane {

    private final String islandID;

    private final EnumMap<Colour,ButtonWithLabel> colourButtons=new EnumMap<>(Colour.class);
    @FXML
    ImageView islandImage;
    @FXML
    ButtonWithLabel yellow, blue, green, red, purple, motherNature;

    public IslandAnchorPane(@NamedArg("island-id") String id, @NamedArg("img-src") String imgSrc) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Assets/GUI/fxml/Island.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
            this.islandID=id;
            islandImage.setImage(new Image(Objects.requireNonNull(getClass().getResource("/Assets/GUI/Tiles/")).toExternalForm()+(imgSrc.isEmpty()?"Island_1.png":imgSrc)));
            colourButtons.put(Colour.YELLOW,yellow);
            colourButtons.put(Colour.BLUE,blue);
            colourButtons.put(Colour.GREEN,green);
            colourButtons.put(Colour.RED,red);
            colourButtons.put(Colour.PURPLE,purple);
        }
        catch (IOException io) {
            io.printStackTrace();
            throw new RuntimeException(io);
        }
    }
    public void setupButtons(ObjectOutputStream toServer, String sender) {
        for (Colour c : Colour.values()) {
            colourButtons.get(c).setOnAction(event->{
                try {
                    toServer.writeObject(MessageCenter.genMessage(MsgType.DISKTOISLAND,sender,islandID, c));
                }
                catch (IOException io) {
                    Alert alert=new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Error sending message to server");
                    alert.showAndWait();
                }
            });
            colourButtons.get(c).setLabelText("0");
        }
        motherNature.setOnAction(event->{

        });
    }
    public void setMNDistance(ObjectOutputStream toServer, String sender, int distance) {
        motherNature.setBackgroundVisible(distance==0);
        motherNature.setOnAction(event->{
            try {
                toServer.writeObject(MessageCenter.genMessage(MsgType.MOVEMN,sender,islandID, distance));
            }
            catch (IOException io) {
                Alert alert=new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Error sending message to server");
                alert.showAndWait();
            }
        });
    }
    public String getIslandID() {
        return this.islandID;
    }
}
