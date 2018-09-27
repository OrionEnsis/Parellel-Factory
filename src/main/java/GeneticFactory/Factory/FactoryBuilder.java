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

/**
 * this class builds a series of factories and then employs a genetic algorithm in order to produce an ideal factory.
 * it additionally occasionally exchanges factories with other FactoryBuilder objects.
 */
public class FactoryBuilder implements Runnable{
    private static Exchanger<Factory> exchanger = new Exchanger<>();
    private ArrayList<Factory> factories;
    private final int SIZE = 64;
    private final int EXCHANGES;
    private final int MAX_GENERATIONS;
    private int length;
    private int width;
    private HashMap<Tiles,Integer> rules;

    /**
     * builds a new FactoryBuilder
     * @param length the length of the Factories.
     * @param width the width of the Factories
     * @param rules the number of each type of Machine that is permitted for an acceptable machine.
     * @param exchanges the number of times the FactoryBuilder will attempt to exchange a Factory during operation.
     * @param generations the number of generations that will be iterated on in between exchanges.
     */
    public FactoryBuilder(final int length, final int width, final HashMap<Tiles,Integer> rules,
                          final int exchanges, final int generations){
        this.length = length;
        this.width = width;
        this.rules = rules;
        factories = new ArrayList<>();
        EXCHANGES = exchanges;
        MAX_GENERATIONS = generations;

        //create a new set of factories to genetically build.
        for (int i = 0; i < SIZE; i ++) {
            Factory f = new Factory(length,width,rules);
            f.generateNewLayout();
            f.evaluateLayout();
            factories.add(f);
        }
    }

    /**
     * this method runs the genetic algorithm for the factory.
     */
    @Override
    public void run() {
        for(int x = 0; x < EXCHANGES; x++) {
            for (int i = 0; i < MAX_GENERATIONS; i++) {
                newGeneration();
            }
            //try to have an exchange take place.
            try {
                //prep for exchange
                factories.sort(Collections.reverseOrder());
                Factory f = new Factory(this.length,this.width,rules);
                f.copyLayout(factories.get(0).getLayout());

                //exchange
                f = exchanger.exchange(f,10, TimeUnit.SECONDS);
                factories.add(f);
                System.out.println("Exchange Occurred");
            } catch (InterruptedException e) {
                System.out.println("Exchange Interrupted.");
                e.printStackTrace();
            } catch (TimeoutException e) {
                System.out.println("Exchanger Timeout");
            }
            System.out.println("Cycle " + (x + 1));
        }
        System.out.println("Thread done.");
    }

    /**
     * this method handles the creation of new factories for a generation iteration of the genetic iteration.
     */
    private void newGeneration(){
        factories.sort(Collections.reverseOrder());
        makeImage(factories.get(0));

        //breed the children.
        for(int i = 0; i < SIZE; i ++){
            factories.add(factories.get(i).crossBreed(factories.get(i+1)));
            if(i == 0){
                factories.add(factories.get(0).crossBreed(factories.get(2)));
            }
        }
        //mutate.
        mutate();

        //determine the darwin survivors
        factories.sort(Collections.reverseOrder());
        factories.subList(SIZE,factories.size()).clear();
        //TODO consider switching order for mutation before culling.

    }

    /**
     * this thread handles mutation for each Factory in the FactoryBuilder.
     */
    private void mutate(){
        for(int i = 0; i < SIZE; i ++){
            factories.get(i).mutate();
        }
    }

    /**
     * this method creates an image based of off a factory and attempts to send it to the GUI.
     * @param f the factory the image shall be based in.
     */
    private void makeImage(final Factory f){
        final int SCALE = 50;
        int imageHeight = length * SCALE;
        int imageWidth = width * SCALE;
        WritableImage image = new WritableImage(imageWidth,imageHeight);
        PixelWriter pixelWriter = image.getPixelWriter();

        //create the image.
        for(int i = 0; i < image.getWidth(); i++){
            for(int j = 0; j < image.getHeight(); j++){
                Tiles t = f.getMachine(i/SCALE,j/SCALE).getName();
                Color c = determineColor(t);
                pixelWriter.setColor(i,j,c);
            }
        }
        Controller.getInstance().setImage(image,f.getScore());
    }

    /**
     * this method determines the color to be printed based on the Tile type given.
     * @param t The tile that determines the color.
     * @return the color of the tile.
     */
    private Color determineColor(Tiles t){
        Color c = Color.ALICEBLUE;
        switch(t){
            case EMPTY:
                c = Color.WHITE;
                break;
            case E:
                c = Color.BLUE;
                break;
            case D:
                c = Color.GREEN;
                break;
            case C:
                c = Color.YELLOW;
                break;
            case B:
                c = Color.ORANGE;
                break;
            case A:
                c = Color.RED;
        }
        return c;
    }
}
