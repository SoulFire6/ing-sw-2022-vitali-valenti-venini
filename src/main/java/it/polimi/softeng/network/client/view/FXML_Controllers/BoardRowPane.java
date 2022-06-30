package it.polimi.softeng.network.client.view.FXML_Controllers;

import javafx.beans.NamedArg;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Objects;

public class BoardRowPane extends AnchorPane {
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
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateRow(int num, boolean prof) {
        for (Node node : getChildren()) {
            System.out.println(node.getId());
            if (node.getId().contains("prof")) {
                node.setVisible(prof);
            } else {
                node.setVisible(Integer.parseInt(node.getId())<=num);
            }
        }
    }
}
