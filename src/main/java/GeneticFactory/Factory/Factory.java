package GeneticFactory.Factory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

/**
 * this class is a representation of a potential factory layout.  It has the ability to produce a "valid" factory based
 * on the rules sent by a FactoryBuilder or equivalent.  It is also able to scored by the metrics of each machine.
 */
public class Factory implements Comparable<Factory>{
    private int score;
    private Machine[][] layout;
    private HashMap<Tiles,Integer> rules;
    private HashMap<Tiles,ArrayList<Machine>> currentMachines = new HashMap<>();
    private final int TOTAL_TILES;

    /**
     * constructs a basic factory
     * @param length length of the factory layout
     * @param width width of the factory layout
     * @param rules a HashMap that states the number of machines of each Tile type that are allowed to exist.
     */
    public Factory (int length, int width, HashMap<Tiles,Integer> rules) {
        this.rules = new HashMap<>(rules);
        layout = new Machine[length][width];

        //populate the HashMap
        for(Tiles t: Tiles.values()){
            currentMachines.put(t,new ArrayList<>());
        }

        //Add all members to the HashMap's ArrayLists
        for (int i = 0; i < layout.length; i ++) {
            for (int j = 0; j < layout[0].length; j++) {
                layout[i][j] = new Machine(i, j, Tiles.EMPTY);
                currentMachines.get(Tiles.EMPTY).add(layout[i][j]);
            }
        }
        TOTAL_TILES = length *width;
    }

    /**
     * generates a new random layout.
     */
    void generateNewLayout(){
        ThreadLocalRandom r = ThreadLocalRandom.current();

        //use the rules and currentMachines to reduce calculations
        for(HashMap.Entry<Tiles,Integer> entry: rules.entrySet()){
            for(int i = 0; i < entry.getValue(); i++){
                int x = r.nextInt(currentMachines.get(Tiles.EMPTY).size());
                Machine m = currentMachines.get(Tiles.EMPTY).get(x);
                m.setName(entry.getKey());
                currentMachines.get(entry.getKey()).add(m);
                currentMachines.get(Tiles.EMPTY).remove(m);
            }
        }
    }

    /**
     * this creates a new factory by breeding it with another factory
     * @param otherFactory the other "parent" for breeding
     * @return the new child factory.
     */
    Factory crossBreed(Factory otherFactory){
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Factory child = new Factory(layout.length,layout[0].length, rules);
        ArrayList<Machine> nonMatchingMachines = new ArrayList<>();

        //get matching tiles (keep these)
        for(int i = 0; i < layout.length;  i++){
            for(int j = 0; j < layout[0].length; j++){
                if(layout[i][j].compareTo(otherFactory.getMachine(i,j)) == 0){
                    child.setMachine(i,j,layout[i][j]);
                }
                else{
                    nonMatchingMachines.add(layout[i][j]);
                }
            }
        }

        //randomly pick between remaining tiles.
        for(Machine m: nonMatchingMachines){
            boolean useThisParent = random.nextBoolean();
            if(useThisParent){
                child.setMachine(m.x,m.y,m);
            }
            else{
                child.setMachine(m.x,m.y,otherFactory.getMachine(m.x,m.y));
            }
        }

        //count each machine type.
        child.enforceRules();

        //evaluate performance
        evaluateLayout();

        return child;
    }

    /**
     * this method enforces the rules of the genetic algorithm to prevent 'rampant' changes
     */
    private void enforceRules(){
        ThreadLocalRandom random = ThreadLocalRandom.current();

        //remove extra machines
        currentMachines.forEach((k,v)-> {
                //remove too random too high one.
                if (!k.equals(Tiles.EMPTY)) {
                    while (v.size() > rules.get(k)) {
                        int r = random.nextInt(v.size());
                        v.get(r).setName(Tiles.EMPTY);
                        currentMachines.get(Tiles.EMPTY).add(v.get(r));
                        v.remove(r);
                    }
                }
            });

        //fill in for missing machines
        currentMachines.forEach((k,v)->{
            if(!k.equals(Tiles.EMPTY)){
                //add to random low member
                while (v.size() < rules.get(k)) {
                    int x = random.nextInt(currentMachines.get(Tiles.EMPTY).size());
                    Machine m = currentMachines.get(Tiles.EMPTY).get(x);
                    m.setName(k);
                    currentMachines.get(k).add(m);
                    currentMachines.get(Tiles.EMPTY).remove(m);
                }
            }
        });
    }

    /**
     * this method determines the score of the factory by comparing each Machine with its neighbors, and by simulating
     * the factory running and counting the amount of goods produced.
     */

    void evaluateLayout(){
        score = 0;

        //count the adjacency score
        for(int i = 0; i < layout.length; i++){
            for(int j = 0; j < layout[0].length; j++){
                HashSet<Machine> machines = getNeighbors(i,j,3);
                for(Machine m: machines){
                    score += scoreInt(layout[i][j],m);
                }
            }
        }
        //the simulation must be run a separate time for each iteration to determine the amount produced by the end machines
        currentMachines.get(Tiles.A).forEach(Machine::makeProduct);
        currentMachines.get(Tiles.B).forEach(m->getNeighbors(m.x,m.y,1).forEach(m::getPreProductFromInputMachine));
        currentMachines.get(Tiles.B).forEach(Machine::makeProduct);

        currentMachines.get(Tiles.A).forEach(Machine::makeProduct);
        currentMachines.get(Tiles.B).forEach(m->getNeighbors(m.x,m.y,1).forEach(m::getPreProductFromInputMachine));
        currentMachines.get(Tiles.B).forEach(Machine::makeProduct);
        currentMachines.get(Tiles.C).forEach(m->getNeighbors(m.x,m.y,1).forEach(m::getPreProductFromInputMachine));
        currentMachines.get(Tiles.C).forEach(Machine::makeProduct);

        currentMachines.get(Tiles.A).forEach(Machine::makeProduct);
        currentMachines.get(Tiles.B).forEach(m->getNeighbors(m.x,m.y,1).forEach(m::getPreProductFromInputMachine));
        currentMachines.get(Tiles.B).forEach(Machine::makeProduct);
        currentMachines.get(Tiles.C).forEach(m->getNeighbors(m.x,m.y,1).forEach(m::getPreProductFromInputMachine));
        currentMachines.get(Tiles.C).forEach(Machine::makeProduct);
        currentMachines.get(Tiles.D).forEach(m->getNeighbors(m.x,m.y,1).forEach(m::getPreProductFromInputMachine));
        currentMachines.get(Tiles.D).forEach(Machine::makeProduct);

        currentMachines.get(Tiles.A).forEach(Machine::makeProduct);
        currentMachines.get(Tiles.B).forEach(m->getNeighbors(m.x,m.y,1).forEach(m::getPreProductFromInputMachine));
        currentMachines.get(Tiles.B).forEach(Machine::makeProduct);
        currentMachines.get(Tiles.C).forEach(m->getNeighbors(m.x,m.y,1).forEach(m::getPreProductFromInputMachine));
        currentMachines.get(Tiles.C).forEach(Machine::makeProduct);
        currentMachines.get(Tiles.D).forEach(m->getNeighbors(m.x,m.y,1).forEach(m::getPreProductFromInputMachine));
        currentMachines.get(Tiles.D).forEach(Machine::makeProduct);
        currentMachines.get(Tiles.E).forEach(m->getNeighbors(m.x,m.y,1).forEach(m::getPreProductFromInputMachine));
        currentMachines.get(Tiles.E).forEach(Machine::makeProduct);

        //get the score and reset for next tick
        currentMachines.get(Tiles.E).forEach(m->score +=m.getProduct(10000));
        currentMachines.forEach((k,v)->v.forEach(Machine::resetMachine));
    }

    /**
     * swap tiles to potentially create a better species
     */
    void mutate(){
        int tilesToSwap = (int)((TOTAL_TILES * 0.15));
        Factory m = new Factory(layout.length,layout[0].length,rules);
        m.copyLayout(this.layout);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        //swap the tiles around
        for (int i = 0; i < tilesToSwap; i++) {
            int x1 = random.nextInt(layout.length);
            int y1 = random.nextInt(layout[0].length);
            int x2 = random.nextInt(layout.length);
            int y2 = random.nextInt(layout[0].length);
            m.setMachine(x1,y1,layout[x2][y2]);
        }
        //we still need to follow the rules
        m.enforceRules();

        //if we have a better score replace.
        m.evaluateLayout();
        if(this.getScore() <m.getScore()){
            this.copyLayout(m.getLayout());
            this.evaluateLayout();
        }
    }

    /**
     * scores a pair of machines against each other
     * @param a the first Machine
     * @param b The second Machine
     * @return the score achieved.
     */
    private int scoreInt(Machine a, Machine b){
        return a.scoreMachine(b);
    }

    /**
     * allows neighbors to be retrieved recursively.  It also returns the base member
     * @param x starting x
     * @param y starting y
     * @param recurse distance neighbors are.
     * @return a HashSet of the neighbors found, including the base machine.
     */
    private HashSet<Machine> getNeighbors(int x, int y, int recurse){
        if( x < 0 || x >= layout.length || y < 0 || y >= layout[0].length){
            return new HashSet<>();
        }
        HashSet<Machine> machines = new HashSet<>();
        machines.add(layout[x][y]);
        if (recurse > 0) {
            for(Machine m: machines) {
                machines.addAll(getNeighbors(m.x, m.y -1, recurse - 1));
                machines.addAll(getNeighbors(m.x -1, m.y, recurse - 1));
                machines.addAll(getNeighbors(m.x +1, m.y, recurse - 1));
                machines.addAll(getNeighbors(m.x, m.y +1, recurse - 1));
            }
        }
        return machines;
    }

    /**
     * copies a machine layout to this Factory
     * @param layout the layout to copy
     */
    void copyLayout(Machine[][] layout){
        //clean currentMachines
        for(Tiles t: Tiles.values()){
            currentMachines.put(t,new ArrayList<>());
        }

        //add each machine onto the layout
        this.layout = new Machine[layout.length][];
        for(int i = 0; i < layout.length; i++){
            this.layout[i] = new Machine[layout[0].length];
            for(int j = 0; j < this.layout[0].length; j++){
                this.layout[i][j] = new Machine(layout[i][j]);
                setMachine(i,j,layout[i][j]);
            }

        }
    }

    /**
     * copy a machine onto the other
     * @param x x-coordinate
     * @param y y-coordinate
     * @param t the machine to copy
     */
    private void setMachine(int x, int y, Machine t){
        Machine m = layout[x][y];
        currentMachines.forEach((k,v)->v.remove(m));
        currentMachines.get(t.getName()).add(m);
        layout[x][y].setName(t.getName());
    }

    /**
     * return the Machine at the given coordinates
     * @param x the x coordinate of the Machine
     * @param y the y coordinate of the Machine
     * @return the Machine at the coordinates.
     */
    Machine getMachine(int x, int y){ return layout[x][y];    }

    /**
     * get the machine layout
     * @return the layout returned
     */
    Machine[][] getLayout(){
        return layout;
    }

    /**
     * return the score
     * @return the score of This Machine
     */
    int getScore(){
        return score;
    }

    /**
     * comparison with another factory
     * @param o the other Factory
     * @return the comparison
     */
    @Override
    public int compareTo(@Nonnull Factory o){
        return Double.compare(getScore(),o.getScore());
    }

    /**
     * returns the string of tiles.
     * @return get the string of the object
     */
    @SuppressWarnings({"ForLoopReplaceableByForEach", "StringConcatenationInLoop"})
    @Override
    public String toString() {
        String temp = "";

        for(int i = 0; i < layout.length; i++){
            for(int j = 0; j < layout[0].length; j++){
                temp += layout[i][j].name.toString()+" ";
            }
            temp += "\n";
        }
        return temp;
    }
}
