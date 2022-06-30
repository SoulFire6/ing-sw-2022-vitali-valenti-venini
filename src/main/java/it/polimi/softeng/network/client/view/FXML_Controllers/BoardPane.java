package it.polimi.softeng.network.client.view.FXML_Controllers;

import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.Team;
import it.polimi.softeng.network.message.MsgType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Objects;
import java.util.ResourceBundle;

public class BoardPane extends AnchorPane implements Initializable {

    @FXML
    AnchorPane entrance;
    @FXML
    GridPane towers;
    @FXML
    BoardRowPane yellowRow,blueRow,greenRow,redRow,purpleRow;

    private final ArrayList<ImageView> entranceSlots=new ArrayList<>();

    private final EnumMap<Colour, BoardRowPane> rows=new EnumMap<>(Colour.class);

    private final EnumMap<Colour, Image> disks=new EnumMap<>(Colour.class);

    private MessageSender messageSender;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (Node node : entrance.getChildren()) {
            entranceSlots.add((ImageView) node);
        }
        rows.put(Colour.YELLOW,yellowRow);
        rows.put(Colour.BLUE,blueRow);
        rows.put(Colour.GREEN,greenRow);
        rows.put(Colour.RED,redRow);
        rows.put(Colour.PURPLE,purpleRow);
        for (Colour c : Colour.values()) {
            disks.put(c,new Image(Objects.requireNonNull(getClass().getResource("/Assets/GUI/Icons/" + c.name().charAt(0) + c.name().substring(1).toLowerCase()+"_Disk.png")).toExternalForm()));
        }
    }

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender=messageSender;
    }
    public void setupTowers(int num, Team team) {
        String teamName=team.name().charAt(0)+team.name().substring(1).toLowerCase();
        Image towerImage=new Image(Objects.requireNonNull(getClass().getResource("/Assets/GUI/Icons/" + teamName + "_Tower.png")).toExternalForm());
        for (Node node : towers.getChildren()) {
            ((ImageView)node).setImage(towerImage);
        }
        updateTowers(num);
    }

    public void updateEntrance(EnumMap<Colour,Integer> entrance, boolean interactive) {
        int idx=0;
        for (Colour c : Colour.values()) {
            for (int j=0; j<entrance.get(c); j++,idx++) {
                entranceSlots.get(idx).setVisible(true);
                entranceSlots.get(idx).setImage(disks.get(c));
                if (interactive) {
                    entranceSlots.get(idx).addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                        if (mouseEvent.getClickCount()==1) {
                            messageSender.sendMessage(MsgType.DISKTODININGROOM, c + "to dining room", c);
                        }
                    });
                }
            }
        }
        for (int i=idx; i<entranceSlots.size(); i++) {
            entranceSlots.get(i).setVisible(false);
        }
    }
    public void updateTowers(int num) {
        for (Node node : towers.getChildren()) {
            node.setVisible(Integer.parseInt(node.getId())<=num);
        }
    }
    public void updateRow(Colour c, int num, boolean prof) {
        rows.get(c).updateRow(num,prof);
    }
}
