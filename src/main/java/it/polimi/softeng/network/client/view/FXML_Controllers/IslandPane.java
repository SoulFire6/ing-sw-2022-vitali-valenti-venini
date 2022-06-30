package it.polimi.softeng.network.client.view.FXML_Controllers;

import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.ReducedModel.ReducedIsland;
import it.polimi.softeng.model.Team;
import it.polimi.softeng.network.message.MsgType;
import javafx.beans.NamedArg;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;


import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller class for custom fxml component IslandPane
 */
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
    /**
     * Default constructor for IslandPanes
     * @param imgSrc the image to use as the IslandPane's background
     * @exception RuntimeException when fxml file is not loaded correctly
     */
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
    }

    /**
     * Inherited method initialize to set up fxml components and other attributes
     * @param url default unused value
     * @param resourceBundle default unused value
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        yellow.setOnAction(event->sendDiskToIsland(Colour.YELLOW));
        blue.setOnAction(event->sendDiskToIsland(Colour.BLUE));
        green.setOnAction(event->sendDiskToIsland(Colour.GREEN));
        red.setOnAction(event->sendDiskToIsland(Colour.RED));
        purple.setOnAction(event->sendDiskToIsland(Colour.PURPLE));
        teamTowers.put(Team.WHITE,new Image(Objects.requireNonNull(getClass().getResource("/Assets/GUI/Icons/White_Tower.png")).toExternalForm()));
        teamTowers.put(Team.BLACK,new Image(Objects.requireNonNull(getClass().getResource("/Assets/GUI/Icons/Black_Tower.png")).toExternalForm()));
        teamTowers.put(Team.GREY,new Image(Objects.requireNonNull(getClass().getResource("/Assets/GUI/Icons/Grey_Tower.png")).toExternalForm()));
        this.addEventHandler(MouseEvent.MOUSE_PRESSED,event->moveMN());
    }
    /**
     * Setter for MessageSender
     * @param messageSender message sender to set
     */
    public void setMessageSender(MessageSender messageSender) {
        this.messageSender=messageSender;
    }
    /**
     * This method is used as an action performed by the coloured buttons, it sends a move disk to island message
     * @param c the button's colour
     */
    private void sendDiskToIsland(Colour c) {
        messageSender.sendMessage(MsgType.DISKTOISLAND,getId(),c);
    }
    /**
     * This method is used as an action performed when clicking on an island, it sends a move mother nature message based on distance
     */
    private void moveMN() {
        messageSender.sendMessage(MsgType.MOVEMN,"Move mn to "+getId(),distance);
    }
    /**
     * This method updates the IslandPane components
     * @param island the ReducedIsland this IslandPane represents
     * @param distance the distance to the ReducedIsland with mother nature
     */
    public void update(ReducedIsland island, int distance) {
        yellow.setText(island.getContents().get(Colour.YELLOW).toString());
        blue.setText(island.getContents().get(Colour.BLUE).toString());
        green.setText(island.getContents().get(Colour.GREEN).toString());
        red.setText(island.getContents().get(Colour.RED).toString());
        purple.setText(island.getContents().get(Colour.PURPLE).toString());
        towerNum.setText(String.valueOf(island.getTowers()));
        for (Button button : Arrays.asList(yellow,blue,green,red,purple)) {
            Tooltip.install(button,new Tooltip(button.getText()));
        }
        if (island.getTeam()!=null) {
            towerImage.setImage(teamTowers.get(island.getTeam()));
        }
        this.distance=distance;
        motherNature.setVisible(this.distance==0);
    }
}
