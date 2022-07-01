package it.polimi.softeng.network.client.view.FXML_Controllers;

import it.polimi.softeng.model.Colour;
import it.polimi.softeng.network.reducedModel.ReducedCloud;
import it.polimi.softeng.network.message.MsgType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Controller class for custom fxml component CloudPane
 */

public class CloudPane extends AnchorPane implements Initializable {

    @FXML
    ImageView first, second, third, fourth;

    private final EnumMap<Colour, Image> disks=new EnumMap<>(Colour.class);

    private final ArrayList<ImageView> slots=new ArrayList<>();


    private MessageSender messageSender;

    /**
     * Default constructor for CloudPanes
     * @exception RuntimeException when fxml file is not loaded correctly
     */
    public CloudPane() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Assets/GUI/fxml/Cloud.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
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
        for (Colour c : Colour.values()) {
            disks.put(c,(new Image(Objects.requireNonNull(getClass().getResource("/Assets/GUI/Icons/" + c.getName() + "_Disk.png")).toExternalForm())));
        }
        slots.addAll(Arrays.asList(first,second,third,fourth));
        this.addEventHandler(MouseEvent.MOUSE_PRESSED,event-> messageSender.sendMessage(MsgType.CHOOSECLOUD,getId(),getId()));
    }

    /**
     * Setter for MessageSender
     * @param messageSender message sender to set
     */

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender=messageSender;
    }

    /**
     * This method updates CloudPane components
     * @param cloud the ReducedCloud this CloudPane represents
     */

    public void update(ReducedCloud cloud) {
        int idx=0;
        for (Colour c : Colour.values()) {
            for (int j=0; j<cloud.getContents().get(c); j++,idx++) {
                slots.get(idx).setVisible(true);
                slots.get(idx).setImage(disks.get(c));
            }
        }
        for (int i=idx; i<slots.size(); i++) {
            slots.get(i).setVisible(false);
        }
    }
}
