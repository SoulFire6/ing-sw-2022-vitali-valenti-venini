package it.polimi.softeng.network.client.view.FXML_Controllers;

import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.Team;
import it.polimi.softeng.network.message.MsgType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * This class is an fxml component controller defining a player's school board
 */
public class BoardPane extends AnchorPane implements Initializable {

    @FXML
    AnchorPane entrance;
    @FXML
    GridPane towers;
    @FXML
    BoardRowPane yellowRow,blueRow,greenRow,redRow,purpleRow;

    private final ArrayList<Button> entranceSlots=new ArrayList<>();

    private final EnumMap<Colour, BoardRowPane> rows=new EnumMap<>(Colour.class);

    private final EnumMap<Colour, String> disks=new EnumMap<>(Colour.class);

    private MessageSender messageSender;

    /**
     * Default constructor that loads fxml file
     */

    public BoardPane() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Assets/GUI/fxml/Board.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        }
        catch (IOException io) {
            throw new RuntimeException(io);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * inherited method initialize for setting up fxml components
     * @param url default unused url
     * @param resourceBundle default unused resource
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (Node node : entrance.getChildren()) {
            entranceSlots.add((Button) node);
        }
        rows.put(Colour.YELLOW,yellowRow);
        rows.put(Colour.BLUE,blueRow);
        rows.put(Colour.GREEN,greenRow);
        rows.put(Colour.RED,redRow);
        rows.put(Colour.PURPLE,purpleRow);
        for (Colour c : Colour.values()) {
            disks.put(c,"-fx-background-image: url('Assets/GUI/Icons/"+c.name().charAt(0) + c.name().substring(1).toLowerCase()+"_Disk.png"+"');-fx-background-size: contain");
        }
    }

    /**
     * Setter for message sender
     * @param messageSender class that sends messages to server
     */
    public void setMessageSender(MessageSender messageSender) {
        this.messageSender=messageSender;
    }

    /**
     * This method sets up the tower on the BoardPane, while also loading the correct image corresponding to the player's team
     * @param num the amount of towers to set
     * @param team the player's team which defines tower colour
     */
    public void setupTowers(int num, Team team) {
        String teamName=team.name().charAt(0)+team.name().substring(1).toLowerCase();
        Image towerImage=new Image(Objects.requireNonNull(getClass().getResource("/Assets/GUI/Icons/" + teamName + "_Tower.png")).toExternalForm());
        for (Node node : towers.getChildren()) {
            ((ImageView)node).setImage(towerImage);
        }
        updateTowers(num);
    }

    /**
     * This method updates the disks in the BoardPane entrance
     * @param entrance the disks in the entrance
     * @param interactive whether the disks can be interacted with to add to dining room
     */

    public void updateEntrance(EnumMap<Colour,Integer> entrance, boolean interactive) {
        int idx=0;
        for (Colour c : Colour.values()) {
            for (int j=0; j<entrance.get(c); j++,idx++) {
                entranceSlots.get(idx).setVisible(true);
                entranceSlots.get(idx).setStyle(disks.get(c));
                if (interactive) {
                    entranceSlots.get(idx).setOnAction(event->messageSender.sendMessage(MsgType.DISKTODININGROOM, c + "to dining room", c));
                }
            }
        }
        for (int i=idx; i<entranceSlots.size(); i++) {
            entranceSlots.get(i).setVisible(false);
        }
    }

    /**
     * This method updates the tower num on the board
     * @param num the amount to set
     */
    public void updateTowers(int num) {
        for (Node node : towers.getChildren()) {
            node.setVisible(Integer.parseInt(node.getId())<=num);
        }
    }

    /**
     * This method updates the dining room of the corresponding colour c
     * @param c the row colour to update
     * @param num the amount of student disks in that row
     * @param prof the presence of the professor pawn
     */
    public void updateRow(Colour c, int num, boolean prof) {
        rows.get(c).updateRow(num,prof);
    }
}
