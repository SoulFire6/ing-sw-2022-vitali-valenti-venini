package it.polimi.softeng.network.client.view.FXML_Controllers;

import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.ReducedModel.ReducedIsland;
import it.polimi.softeng.model.Team;
import it.polimi.softeng.network.message.MsgType;
import javafx.beans.NamedArg;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;


import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Objects;
import java.util.ResourceBundle;

public class IslandPane extends AnchorPane implements Initializable {
    @FXML
    Button yellow, blue, green, red, purple;
    @FXML
    ImageView motherNature,towerImage;
    @FXML
    Label towerNum;
    private Integer distance=0;

    private final EnumMap<Team,Image> teamTowers=new EnumMap<>(Team.class);
    private MessageSender messageSender;

    public IslandPane(@NamedArg("img-src") String imgSrc) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Assets/GUI/fxml/Island.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
            setStyle("-fx-background-image: url('/Assets/GUI/Tiles/"+imgSrc+"');-fx-background-size: cover");
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
        teamTowers.put(Team.WHITE,new Image(Objects.requireNonNull(getClass().getResource("/Assets/GUI/Icons/White_Tower.png")).toExternalForm()));
        teamTowers.put(Team.BLACK,new Image(Objects.requireNonNull(getClass().getResource("/Assets/GUI/Icons/Black_Tower.png")).toExternalForm()));
        teamTowers.put(Team.GREY,new Image(Objects.requireNonNull(getClass().getResource("/Assets/GUI/Icons/Grey_Tower.png")).toExternalForm()));
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
        yellow.setText(island.getContents().get(Colour.YELLOW).toString());
        blue.setText(island.getContents().get(Colour.BLUE).toString());
        green.setText(island.getContents().get(Colour.GREEN).toString());
        red.setText(island.getContents().get(Colour.RED).toString());
        purple.setText(island.getContents().get(Colour.PURPLE).toString());
        towerNum.setText(String.valueOf(island.getTowers()));
        if (island.getTeam()!=null) {
            towerImage.setImage(teamTowers.get(island.getTeam()));
        }
        this.distance=distance;
    }
}
