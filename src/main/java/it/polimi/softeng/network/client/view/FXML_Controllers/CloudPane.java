package it.polimi.softeng.network.client.view.FXML_Controllers;

import it.polimi.softeng.model.Colour;
import it.polimi.softeng.model.ReducedModel.ReducedCloud;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Objects;

public class CloudPane extends AnchorPane {

    @FXML
    ImageView cloudImage;

    @FXML
    Label first, second, third, fourth;

    private final EnumMap<Colour, Background> backgrounds=new EnumMap<>(Colour.class);

    private final ArrayList<Label> slots=new ArrayList<>();
    public CloudPane() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Assets/GUI/fxml/Cloud.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
            cloudImage.fitHeightProperty().bind(this.prefHeightProperty());
            cloudImage.fitWidthProperty().bind(this.prefWidthProperty());
            for (Colour c : Colour.values()) {
                backgrounds.put(c,new Background(new BackgroundImage(new Image(Objects.requireNonNull(getClass().getResource("/Assets/GUI/Icons/" + c.getName() + "_Disk.png")).toExternalForm()),null,null, BackgroundPosition.CENTER,null)));
            }
            slots.addAll(Arrays.asList(first,second,third,fourth));
            for (int i=0; i<slots.size(); i++) {
                slots.get(i).prefHeightProperty().bind(this.heightProperty().divide(i%2==0?2:3).multiply(i%2==0?1:i/2.0));
                slots.get(i).prefHeightProperty().bind(this.heightProperty().divide(i%2==0?3:2).multiply(i%2==0?i/2.0:1));
            }
        }
        catch (IOException io) {
            io.printStackTrace();
            throw new RuntimeException(io);
        }

    }
    public void update(ReducedCloud cloud) {
        int idx=0;
        for (Colour c : Colour.values()) {
            for (int j=0; j<cloud.getContents().get(c); j++) {
                slots.get(idx).setVisible(true);
                slots.get(idx).setBackground(backgrounds.get(c));
            }
        }
        for (int i=idx; i<slots.size(); i++) {
            slots.get(i).setVisible(false);
        }
    }
}
