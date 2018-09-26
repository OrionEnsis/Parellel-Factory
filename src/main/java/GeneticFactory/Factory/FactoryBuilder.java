package GeneticFactory.Factory;

import GeneticFactory.Gui.Controller;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FactoryBuilder implements Runnable{
    private static Exchanger<Factory> exchanger = new Exchanger<>();
    private ArrayList<Factory> factories;
    private final int SIZE = 32;
    private final int EXCHANGES;
    private final int MAX_GENERATIONS;
    private int length;
    private int width;
    private HashMap<Tiles,Integer> rules;

    public FactoryBuilder(int length, int width, HashMap<Tiles,Integer> rules, int exchanges, int generations){
        this.length = length;
        this.width = width;
        this.rules = rules;
        factories = new ArrayList<>();
        EXCHANGES = exchanges;
        MAX_GENERATIONS = generations;
        for (int i = 0; i < SIZE; i ++) {
            Factory f = new Factory(length,width,rules);
            f.generateNewLayout();
            f.evaluateLayout();
            factories.add(f);
        }
    }
    @Override
    public void run() {
        for(int x = 0; x < EXCHANGES; x++) {
            for (int i = 0; i < MAX_GENERATIONS; i++) {
                newGeneration();
            }
            try {
                factories.sort(Collections.reverseOrder());
                Factory f = new Factory(this.length,this.width,rules);
                f.copyLayout(factories.get(0).getLayout());
                f = exchanger.exchange(f,10, TimeUnit.SECONDS);
                factories.add(f);
                System.out.println("Exchange Occurred");
            } catch (InterruptedException e) {
                System.out.println("Exchange Interrupted.");
                e.printStackTrace();
            } catch (TimeoutException e) {
                System.out.println("Exchanger Timeout");
            }
            System.out.println("Cycle " + x);
        }
        System.out.println("Thread done.");
    }

    private void newGeneration(){
        factories.sort(Collections.reverseOrder());
        makeImage(factories.get(0));

        for(int i = 0; i < SIZE; i ++){
            factories.add(factories.get(i).crossBreed(factories.get(i+1)));
            if(i == 0){
                factories.add(factories.get(0).crossBreed(factories.get(2)));
            }
        }
        factories.sort(Collections.reverseOrder());
        factories.subList(SIZE,factories.size()).clear();
        mutate();
    }

    private void mutate(){
        for(int i = 0; i < SIZE; i ++){
            factories.get(i).mutate();
        }

    }
    private void makeImage(Factory f){
        final int SCALE = 50;
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
        Controller.instance.setImage(image,f.getScore());

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
    }
    public Factory getBestFactory(){
        return factories.get(0);
    }
}
