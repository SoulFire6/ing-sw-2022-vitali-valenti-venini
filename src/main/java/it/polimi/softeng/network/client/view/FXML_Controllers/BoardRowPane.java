package it.polimi.softeng.network.client.view.FXML_Controllers;

import javafx.beans.NamedArg;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Objects;

/**
 * Controller class for custom fxml component BoardRowPane, which represents a row in a BoardPane
 */

public class BoardRowPane extends AnchorPane {

    /**
     * Default constructor for BoardRowPane
     * @param diskSrc the image to use for student disks
     * @param profSrc the image to use for the professor
     * @exception RuntimeException when fxml file is not loaded correctly
     */
    public BoardRowPane(@NamedArg("disk-src")String diskSrc, @NamedArg("prof-src")String profSrc) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Assets/GUI/fxml/BoardRow.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
            Image diskImage=new Image(Objects.requireNonNull(getClass().getResource("/Assets/GUI/Icons/" + diskSrc)).toExternalForm());
            Image profImage=new Image(Objects.requireNonNull(getClass().getResource("/Assets/GUI/Icons/" + profSrc)).toExternalForm());
            for (Node node : getChildren()) {
                ((ImageView)node).setImage(node.getId().contains("prof")?profImage:diskImage);
                node.setVisible(false);
            }
        }
        catch (IOException io) {
            io.printStackTrace();
            throw new RuntimeException(io);
        }
    }

    /**
     * This method updates the BoardRowPane components
     * @param num the number of student disk in the player's dining room of this colour
     * @param prof boolean representing whether the player has this colour's professor
     */
    public void updateRow(int num, boolean prof) {
        for (Node node : getChildren()) {
            if (node.getId().contains("prof")) {
                node.setVisible(prof);
            } else {
                node.setVisible(Integer.parseInt(node.getId())<=num);
            }
        }
    }
}
