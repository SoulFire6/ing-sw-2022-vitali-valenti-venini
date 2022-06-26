package it.polimi.softeng.network.client.view.FXML_Controllers;

import javafx.beans.NamedArg;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

import java.io.IOException;

public class ButtonWithLabel extends StackPane {
    @FXML
    private Button button;
    @FXML
    private Label label;

    private final Background buttonBackground;

    public ButtonWithLabel(@NamedArg("label-text") String label, @NamedArg("button-style") String style) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Assets/GUI/fxml/ButtonWithLabel.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
            this.label.setText(label);
            this.label.setFont(new Font(15));
            this.button.setStyle(style);
            this.buttonBackground=button.getBackground();
        }
        catch (IOException io) {
            throw new RuntimeException(io);
        }
    }

    public void setLabelText(String label) {
        this.label.setText(label);
    }

    public void setSize(DoubleBinding value) {
        button.prefHeightProperty().bind(value);
        button.prefWidthProperty().bind(value);
        //button.setStyle("-fx-background-size: contain");
        setBackgroundVisible(true);
    }
    public void setBackgroundVisible(boolean visible) {
        if (visible) {
            button.setBackground(buttonBackground);
        } else {
            button.setBackground(null);
        }
    }
    public void setOnAction(javafx.event.EventHandler<ActionEvent> action) {
        this.button.setOnAction(action);
    }
}
