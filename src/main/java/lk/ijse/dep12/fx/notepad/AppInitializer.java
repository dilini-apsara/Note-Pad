package lk.ijse.dep12.fx.notepad;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        URL resource = getClass().getResource("/view/MainView.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        AnchorPane container = fxmlLoader.load();
        Scene scene = new Scene(container);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Untitled Document - NOTE PAD");
        primaryStage.show();
        primaryStage.centerOnScreen();


    }
}
