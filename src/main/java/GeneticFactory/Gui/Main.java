package GeneticFactory.Gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @SuppressWarnings("ConstantConditions")
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/Parallel.fxml"));
        primaryStage.setTitle("Parallel Factory");
        primaryStage.setScene(new Scene(root, 1920, 1080));
        Platform.setImplicitExit(false);
        primaryStage.setMaximized(true);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.exit(0);
            }
        });
        primaryStage.show();
    }




    public static void main(String[] args) {
        launch(args);
    }
}
