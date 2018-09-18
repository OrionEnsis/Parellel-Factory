package GeneticFactory.Gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @SuppressWarnings("ConstantConditions")
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/Parallel.fxml"));
        primaryStage.setTitle("Parallel Factory");
        primaryStage.setScene(new Scene(root, 600, 350));
        Platform.setImplicitExit(false);
        primaryStage.show();
    }




    public static void main(String[] args) {
        launch(args);
    }
}
