package it.polimi.softeng.network.client.view.FXML_Controllers;

import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.ReducedModel.ReducedIsland;
import it.polimi.softeng.network.message.MsgType;
import javafx.beans.NamedArg;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class IslandPane extends AnchorPane implements Initializable {
    @FXML
    ButtonWithLabel yellow, blue, green, red, purple;

    @FXML
    ImageView motherNature;

    private Integer distance=0;
    private MessageSender messageSender;

    public IslandPane(@NamedArg("img-src") String imgSrc) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Assets/GUI/fxml/Island.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
            URL src=getClass().getResource("/Assets/GUI/Tiles/" + (imgSrc==null?"/Island_1.png":imgSrc));
            Image islandImage=new Image(src==null?"":src.toExternalForm());
            BackgroundSize imageSize=new BackgroundSize(1.0,1.0,true,true,false,false);
            this.setBackground(new Background(new BackgroundImage(islandImage,null,null,null, imageSize)));
        }
        catch (IOException io) {
            io.printStackTrace();
            throw new RuntimeException(io);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        yellow.setOnAction(event->sendDiskToIsland(Colour.YELLOW));
        blue.setOnAction(event->sendDiskToIsland(Colour.BLUE));
        green.setOnAction(event->sendDiskToIsland(Colour.GREEN));
        red.setOnAction(event->sendDiskToIsland(Colour.RED));
        purple.setOnAction(event->sendDiskToIsland(Colour.PURPLE));
        motherNature.visibleProperty().bind(new SimpleBooleanProperty(distance==0));
        this.addEventHandler(MouseEvent.MOUSE_PRESSED,event->moveMN());
    }

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender=messageSender;
    }

    private void sendDiskToIsland(Colour c) {
        messageSender.sendMessage(MsgType.DISKTOISLAND,getId(),c);
    }
    private void moveMN() {
        messageSender.sendMessage(MsgType.MOVEMN,"Move mn to "+getId(),distance);
    }
    public void update(ReducedIsland island, int distance) {
        yellow.setLabelText(island.getContents().get(Colour.YELLOW).toString());
        blue.setLabelText(island.getContents().get(Colour.BLUE).toString());
        green.setLabelText(island.getContents().get(Colour.GREEN).toString());
        red.setLabelText(island.getContents().get(Colour.RED).toString());
        purple.setLabelText(island.getContents().get(Colour.PURPLE).toString());
        this.distance=distance;
    }
}
