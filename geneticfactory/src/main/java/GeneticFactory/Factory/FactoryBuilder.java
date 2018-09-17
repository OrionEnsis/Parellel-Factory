package GeneticFactory.Factory;

import GeneticFactory.Gui.Controller;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class FactoryBuilder implements Runnable{
    public Factory bestFactory;
    private ArrayList<Factory> factories;
    private final int SIZE = 32;
    private final int MAX_GENERATIONS = 128;
    private int x;
    private int y;

    @SuppressWarnings("SuspiciousNameCombination")
    public FactoryBuilder(int x, int y, HashMap<Tiles,Integer> rules){
        this.x = x;
        this.y = y;
        factories = new ArrayList<>();
        for (int i = 0; i < SIZE; i ++) {
            Factory f = new Factory(x,y,rules);
            f.generateNewLayout();
            f.evaluateLayout();
            factories.add(f);
        }
    }
    @Override
    public void run() {
        for(int i = 0; i < MAX_GENERATIONS; i++){
            newGeneration();
        }
    }

    private void newGeneration(){
        factories.sort(Collections.reverseOrder());
        makeImage(factories.get(0));
        factories.subList(SIZE/2,factories.size()).clear();
        int size  = factories.size();
        for(int i = 0; i < size-1; i ++){
            factories.add(factories.get(i).crossBreed(factories.get(i+1)));
            if(i == 0){
                factories.add(factories.get(0).crossBreed(factories.get(2)));
            }
        }
    }

    private void makeImage(Factory f){
        final int SCALE = 10;
        int imageHeight = x * SCALE;
        int imageWidth = y * SCALE;
        WritableImage image = new WritableImage(imageWidth,imageHeight);
        PixelWriter pixelWriter = image.getPixelWriter();
        for(int i = 0; i < image.getWidth(); i++){
            for(int j = 0; j < image.getHeight(); j++){
                Tiles t = f.getMachine(i/SCALE,j/SCALE).getName();
                Color c = determineColor(t);
                pixelWriter.setColor(i,j,c);
            }
        }
        //Controller.instance.setScoreListed(f.getScore());
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
