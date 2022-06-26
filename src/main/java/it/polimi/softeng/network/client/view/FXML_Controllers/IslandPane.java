package it.polimi.softeng.network.client.view.FXML_Controllers;

import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.ReducedModel.ReducedIsland;
import it.polimi.softeng.network.message.MessageCenter;
import it.polimi.softeng.network.message.MsgType;
import javafx.beans.NamedArg;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import jdk.jfr.Name;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class IslandPane extends GridPane {
    @FXML
    ButtonWithLabel yellow, blue, green, red, purple;

    @FXML
    ImageView motherNature;

    private final EnumMap<Colour,ButtonWithLabel> colourButtons=new EnumMap<>(Colour.class);

    public IslandPane(@NamedArg("img-src") String imgSrc) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Assets/GUI/fxml/Island.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
            Image islandImage=new Image((Objects.requireNonNull(getClass().getResource("/Assets/GUI/Tiles/" + imgSrc)).toExternalForm()));
            BackgroundSize imageSize=new BackgroundSize(1.0,1.0,true,true,false,false);
            this.setBackground(new Background(new BackgroundImage(islandImage,null,null,null, imageSize)));
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
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setupButtons(ObjectOutputStream toServer, String sender) {
        for (Colour c : Colour.values()) {
            colourButtons.get(c).setOnAction(event->{
                try {
                    toServer.writeObject(MessageCenter.genMessage(MsgType.DISKTOISLAND,sender,getId(), c));
                }
                catch (IOException io) {
                    Alert alert=new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Error sending message to server");
                    alert.showAndWait();
                }
            });
            colourButtons.get(c).setLabelText("0");
        }
    }
    public void update(ObjectOutputStream toServer, String sender, ReducedIsland island, int distance) {
        motherNature.setVisible(distance==0);
        this.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED,event->{
            try {
                toServer.writeObject(MessageCenter.genMessage(MsgType.MOVEMN,sender,getId(), distance));
            }
            catch (IOException io) {
                Alert alert=new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Error sending message to server");
                alert.showAndWait();
            }
        });
        for (Colour c : Colour.values()) {
            colourButtons.get(c).setLabelText(island.getContents().get(c).toString());
        }
    }
}
