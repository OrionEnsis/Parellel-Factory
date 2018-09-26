package GeneticFactory.Gui;

import GeneticFactory.Factory.Factory;
import GeneticFactory.Factory.FactoryBuilder;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import GeneticFactory.Factory.Tiles;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Controller {
    @FXML
    private TextField lengthField;
    @FXML
    private TextField widthField;
    @FXML
    private TextField aField;
    @FXML
    private TextField bField;
    @FXML
    private TextField cField;
    @FXML
    private TextField dField;
    @FXML
    private TextField eField;
    @FXML
    private TextField generationField;
    @FXML
    private TextField exchangeField;
    @FXML
    private ImageView factoryLayout;
    @FXML
    private Label scoreListed;
    @FXML
    private Button buildButton;

    private int length;
    private int width;
    private HashMap<Tiles,Integer> machines;
    private ArrayList<Factory> factories;
    private double currentHighScore = Double.MIN_VALUE;
    public static Controller instance;
    private int exchanges;
    private int generations;

    @FXML
    public void onBuildFactory(){
        instance = this;
        boolean success = true;
        currentHighScore = 0;
        scoreListed.setText("0");


        int sum = 0;
        try{
            length = Integer.parseInt(lengthField.getText());
            try{
                width = Integer.parseInt(widthField.getText());
                int MINIMUM_MACHINES = 32;
                try{
                    machines = new HashMap<>();
                    try{
                        machines.put(Tiles.A,Integer.parseInt(aField.getText()));
                        try{
                            machines.put(Tiles.B,Integer.parseInt(bField.getText()));
                            try{
                                machines.put(Tiles.C,Integer.parseInt(cField.getText()));
                                try{
                                    machines.put(Tiles.D,Integer.parseInt(dField.getText()));
                                    try{
                                        machines.put(Tiles.E,Integer.parseInt(eField.getText()));
                                        try{
                                            exchanges = Integer.parseInt(exchangeField.getText());
                                            try{
                                                generations = Integer.parseInt(generationField.getText());
                                            }
                                            catch(NumberFormatException nfe){
                                                error(generationField,"The number of generations needs to be an integer.");
                                                success = false;
                                            }
                                        }
                                        catch(NumberFormatException nfe){
                                            error(exchangeField,"The number of exchanges needs to be an integer.");
                                            success = false;
                                        }

                                    }
                                    catch(NumberFormatException nfe){
                                        error(eField,"The number of E machines needs to be an integer.");
                                        success = false;
                                    }
                                }
                                catch(NumberFormatException nfe){
                                    error(dField,"The number of D machines needs to be an integer.");
                                    success = false;
                                }
                            }
                            catch(NumberFormatException nfe){
                                error(cField,"The number of C machines needs to be an integer.");
                                success = false;
                            }
                        }
                        catch(NumberFormatException nfe){
                            error(bField,"The number of B machines needs to be an integer.");
                            success = false;
                        }
                    }
                    catch(NumberFormatException nfe){
                        error(aField,"The number of A machines needs to be an integer.");
                        success = false;
                    }
                    for(int i: machines.values()){
                        sum += i;
                    }
                    if(sum < MINIMUM_MACHINES || sum > length * width)
                        throw new ArithmeticException();
                }
                catch(ArithmeticException ae){
                    error(null, "The total number of machines must be at least " + MINIMUM_MACHINES +
                            ".  And less or equal to the area (" + (length * width) + ").");
                    success = false;
                }
            }
            catch(NumberFormatException nfe){
                error(widthField,"Your width needs to be an integer.");
                success = false;
            }
        }
        catch(NumberFormatException nfe){
            error(lengthField,"Your length needs to be an integer.");
            success = false;
        }

        if (success){
            Thread thread = new Thread(this::createFactory);
            thread.start();
        }
    }

    public synchronized void setImage(Image i,double score){
        Platform.runLater(()->{
            if (score >= currentHighScore) {
                currentHighScore = score;
                factoryLayout.setImage(i);
                scoreListed.setText("" + currentHighScore);
            }
        });

    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void createFactory()  {
        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        ArrayList<FactoryBuilder> factoryBuilders = new ArrayList<>();
        buildButton.setDisable(true);
        for(int i = 0; i < threads; i ++){
            factoryBuilders.add(new FactoryBuilder(length,width,machines,exchanges,generations));
            executor.execute(factoryBuilders.get(i));
        }
        executor.shutdown();
        try{
            int x = 0;
            while(!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                x++;
                System.out.println("Threads still processing " + x);
            }
        }
        catch(InterruptedException ie){
            System.out.println("Threads interrupted for some reason.");
        }
        factories = new ArrayList<>();
        factoryBuilders.forEach(f -> factories.add(f.getBestFactory()));
        factories.forEach(Factory::evaluateLayout);
        factories.sort((f1,f2)-> -f1.compareTo(f2));

        System.out.println("Done!");
        buildButton.setDisable(false);
    }

    private void error(TextField focus, String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error");
        alert.setContentText(message);
        alert.showAndWait();
        if(focus != null){
            focus.requestFocus();
        }
    }
}