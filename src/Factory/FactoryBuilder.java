package Factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.Exchanger;

public class FactoryBuilder implements Runnable{
    public Factory bestFactory;
    private HashMap<Tiles,Integer> rules;
    private ArrayList<Factory> factories;
    private final int SIZE = 32;
    private final int MAX_GENERATIONS = 128;
    int x;
    int y;

    public FactoryBuilder(int x, int y,HashMap<Tiles,Integer> rules){
        this.rules = rules;
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
        Collections.sort(factories,Collections.reverseOrder());
        factories.subList(SIZE/2,factories.size()).clear();
        int size  = factories.size();
        for(int i = 0; i < size-1; i ++){
            factories.add(factories.get(i).crossBreed(factories.get(i+1)));
            if(i == 0){
                factories.add(factories.get(0).crossBreed(factories.get(2)));
            }
        }
    }
    public Factory getBestFactory(){
        return factories.get(0);
    }
}
