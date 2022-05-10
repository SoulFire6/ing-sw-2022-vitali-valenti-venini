package it.polimi.softeng.network.client.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class JavaFX_GUI extends Application {
    private static Stage mainStage;
    private static Scene mainScene;
    private static Pane root;

    @Override
    public void start(Stage stage) {
        mainStage=new Stage();
        mainStage.setTitle("Eriantys");
        Button button=new Button();
        button.setText("Press me");
        button.setOnAction(event -> System.out.println("Test"));

        root = new StackPane();
        root.getChildren().add(button);
        mainScene = new Scene(root,200,200);
        mainStage.setScene(mainScene);
        mainStage.show();
    }

}
