package GeneticFactory.Gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        int a = 2;
        Parent root = FXMLLoader.load(getClass().getResource("src/main/Gui/Parallel.fxml"));
        primaryStage.setTitle("Parallel Factory");
        primaryStage.setScene(new Scene(root, 600, 350));
        primaryStage.show();
    }




    public static void main(String[] args) {
        launch(args);
    }
}
