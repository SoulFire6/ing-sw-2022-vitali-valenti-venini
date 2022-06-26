package it.polimi.softeng.network.client.view.FXML_Controllers;

import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.ReducedModel.ReducedIsland;
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

    private final EnumMap<Colour,ButtonWithLabel> colourButtons=new EnumMap<>(Colour.class);
    @FXML
    ImageView islandImage;
    @FXML
    ButtonWithLabel yellow, blue, green, red, purple, motherNature;

    public IslandAnchorPane(@NamedArg("img-src") String imgSrc) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Assets/GUI/fxml/Island.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
            islandImage.setImage(new Image(Objects.requireNonNull(getClass().getResource("/Assets/GUI/Tiles/")).toExternalForm()+(imgSrc.isEmpty()?"Island_1.png":imgSrc)));
            islandImage.fitHeightProperty().bind(this.widthProperty());
            islandImage.fitWidthProperty().bind(this.heightProperty());
            colourButtons.put(Colour.YELLOW,yellow);
            colourButtons.put(Colour.BLUE,blue);
            colourButtons.put(Colour.GREEN,green);
            colourButtons.put(Colour.RED,red);
            colourButtons.put(Colour.PURPLE,purple);
            //makes buttons dynamically a pentagon
            for (Colour c : Colour.values()) {
                colourButtons.get(c).setManaged(false);
                //yellow -> 5/10, blue -> 1/10, green -> 9/10, red -> 3/10, purple -> 7/10
                colourButtons.get(c).layoutXProperty().bind(this.widthProperty().divide(10).multiply(5+(c.ordinal()>0?4/Math.ceil(c.ordinal()/2.0)*(c.ordinal()%2==0?1:-1):0)));
                //yellow -> 1/10, blue/green -> 4/10, red/purple -> 9/10
                colourButtons.get(c).layoutYProperty().bind(this.heightProperty().divide(10).multiply(1+Math.ceil(c.ordinal()/2.0)*4));
                colourButtons.get(c).setSize(this.widthProperty().divide(10));
            }
            motherNature.setManaged(false);
            motherNature.layoutXProperty().bind(this.widthProperty().divide(2));
            motherNature.layoutYProperty().bind(this.heightProperty().divide(2));
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
        motherNature.setOnAction(event->{

        });
    }
    public void update(ObjectOutputStream toServer, String sender, ReducedIsland island, int distance) {
        motherNature.setBackgroundVisible(distance==0);
        motherNature.setOnAction(event->{
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
