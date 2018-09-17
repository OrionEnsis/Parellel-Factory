package GeneticFactory.Gui;

import GeneticFactory.Factory.Factory;
import GeneticFactory.Factory.FactoryBuilder;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
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
    private ImageView factoryLayout;
    @FXML
    private Label scoreListed;

    private int length;
    private int width;
    private HashMap<Tiles,Integer> machines;
    private ArrayList<Factory> factories;
    private final int MINIMUM_MACHINES = 32;
    private double currentHighScore;
    public static Controller instance;

    @FXML
    public void onBuildFactory(ActionEvent event){
        instance = this;
        boolean success = true;
        currentHighScore = 0;
        scoreListed.setText("0");

        int sum = 0;
        try{
            length = Integer.parseInt(lengthField.getText());
            try{
                width = Integer.parseInt(widthField.getText());
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
            //createFactory();

        }
    }

    public void setImage(Image i,double score){
        Platform.runLater(()->{
            if (score > currentHighScore) {
                currentHighScore = score;
                factoryLayout.setImage(i);
                scoreListed.setText("" + currentHighScore);
            }
        });

    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void createFactory()  {
        int threads = 32;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        ArrayList<FactoryBuilder> factoryBuilders = new ArrayList<>();
        for(int i = 0; i < threads; i ++){
            factoryBuilders.add(new FactoryBuilder(length,width,machines));
            executor.execute(factoryBuilders.get(i));
        }
        executor.shutdown();
        try{
            while(!executor.awaitTermination(1, TimeUnit.SECONDS))
                System.out.println("Threads still processing");
        }
        catch(InterruptedException ie){
            System.out.println("Threads interrupted for some reason.");
        }
        factories = new ArrayList<>();
        factoryBuilders.forEach(f -> factories.add(f.getBestFactory()));
        factories.forEach(f->f.evaluateLayout());
        factories.sort((f1,f2)-> -f1.compareTo(f2));

        System.out.println("Done!");
        //makeImage(factories.get(0));
    }
/*
    void makeImage(Factory f){
        final int SCALE = 10;
        int imageHeight = length * SCALE;
        int imageWidth = width * SCALE;
        WritableImage image = new WritableImage(imageWidth,imageHeight);
        PixelWriter pixelWriter = image.getPixelWriter();
        for(int i = 0; i < image.getWidth(); i++){
            for(int j = 0; j < image.getHeight(); j++){
                Tiles t = f.getMachine(i/SCALE,j/SCALE).getName();
                Color c = determineColor(t);
                pixelWriter.setColor(i,j,c);
            }
        }
        factoryLayout.setImage(image);
    }
    private Color determineColor(Tiles t){
        Color c = Color.ALICEBLUE;
        switch(t){
            case EMPTY:
                c = Color.WHITE;
                break;
            case E:
                c = Color.RED;
                break;
            case D:
                c = Color.BLUE;
                break;
            case C:
                c = Color.GREEN;
                break;
            case B:
                c = Color.YELLOW;
                break;
            case A:
                c = Color.ORANGE;
        }
        return c;
    }*/
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