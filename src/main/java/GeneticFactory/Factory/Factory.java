package GeneticFactory.Factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class Factory implements Comparable<Factory>{
    private double score;
    private Machine[][] layout;
    private HashMap<Tiles,Integer> rules;
    private HashMap<Tiles,ArrayList<Machine>> currentMachines = new HashMap<>();
    private final int TOTAL_TILES;

    public Factory (int length, int width, HashMap<Tiles,Integer> rules) {
        this.rules = new HashMap<>(rules);
        layout = new Machine[length][width];

        for(Tiles t: Tiles.values()){
            currentMachines.put(t,new ArrayList<>());
        }

        for (int i = 0; i < layout.length; i ++) {
            for (int j = 0; j < layout[0].length; j++) {
                layout[i][j] = new Machine(i, j, Tiles.EMPTY);
                currentMachines.get(Tiles.EMPTY).add(layout[i][j]);
            }
        }
        TOTAL_TILES = length *width;
    }

    void generateNewLayout(){
        Random r = new Random();
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

    Factory crossBreed(Factory otherFactory){
        Random random = new Random();
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
        child.enforceRules(this,otherFactory);

        //evaluate performance
        evaluateLayout();

        return child;
    }

    private void enforceRules(Factory factoryA, Factory factoryB){
        Random random = new Random();
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
        currentMachines.forEach((k,v)->{
            if(!k.equals(Tiles.EMPTY)){
                //add to random low member
                while (v.size() < rules.get(k)) {
                    //TODO should choose from parent for determining new element placing.
                    int x = random.nextInt(currentMachines.get(Tiles.EMPTY).size());
                    Machine m = currentMachines.get(Tiles.EMPTY).get(x);
                    m.setName(k);
                    currentMachines.get(k).add(m);
                    currentMachines.get(Tiles.EMPTY).remove(m);
                }
            }
        });
    }

    public void evaluateLayout(){
        score = 0;

        for(int i = 0; i < layout.length; i++){
            for(int j = 0; j < layout[0].length; j++){
                HashSet<Machine> machines = getNeighbors(i,j,3);
                for(Machine m: machines){
                    score += scoreInt(layout[i][j],m);
                }
            }
        }
        currentMachines.get(Tiles.A).forEach(Machine::makeProduct);
        currentMachines.get(Tiles.B).forEach(m->getNeighbors(m.x,m.y,1).forEach(n->m.getPreproductFromInputMachine(n)));
        currentMachines.get(Tiles.B).forEach(Machine::makeProduct);

        currentMachines.get(Tiles.A).forEach(Machine::makeProduct);
        currentMachines.get(Tiles.B).forEach(m->getNeighbors(m.x,m.y,1).forEach(n->m.getPreproductFromInputMachine(n)));
        currentMachines.get(Tiles.B).forEach(Machine::makeProduct);
        currentMachines.get(Tiles.C).forEach(m->getNeighbors(m.x,m.y,1).forEach(n->m.getPreproductFromInputMachine(n)));
        currentMachines.get(Tiles.C).forEach(Machine::makeProduct);

        currentMachines.get(Tiles.A).forEach(Machine::makeProduct);
        currentMachines.get(Tiles.B).forEach(m->getNeighbors(m.x,m.y,1).forEach(n->m.getPreproductFromInputMachine(n)));
        currentMachines.get(Tiles.B).forEach(Machine::makeProduct);
        currentMachines.get(Tiles.C).forEach(m->getNeighbors(m.x,m.y,1).forEach(n->m.getPreproductFromInputMachine(n)));
        currentMachines.get(Tiles.C).forEach(Machine::makeProduct);
        currentMachines.get(Tiles.D).forEach(m->getNeighbors(m.x,m.y,1).forEach(n->m.getPreproductFromInputMachine(n)));
        currentMachines.get(Tiles.D).forEach(Machine::makeProduct);

        currentMachines.get(Tiles.A).forEach(Machine::makeProduct);
        currentMachines.get(Tiles.B).forEach(m->getNeighbors(m.x,m.y,1).forEach(n->m.getPreproductFromInputMachine(n)));
        currentMachines.get(Tiles.B).forEach(Machine::makeProduct);
        currentMachines.get(Tiles.C).forEach(m->getNeighbors(m.x,m.y,1).forEach(n->m.getPreproductFromInputMachine(n)));
        currentMachines.get(Tiles.C).forEach(Machine::makeProduct);
        currentMachines.get(Tiles.D).forEach(m->getNeighbors(m.x,m.y,1).forEach(n->m.getPreproductFromInputMachine(n)));
        currentMachines.get(Tiles.D).forEach(Machine::makeProduct);
        currentMachines.get(Tiles.E).forEach(m->getNeighbors(m.x,m.y,1).forEach(n->m.getPreproductFromInputMachine(n)));
        currentMachines.get(Tiles.E).forEach(Machine::makeProduct);
        currentMachines.get(Tiles.E).forEach(m->score +=m.getProduct(10000));
        currentMachines.forEach((k,v)->v.forEach(m->m.resetMachine()));
    }

    public void mutate(double percent){
        int tilesToSwap = (int)((TOTAL_TILES * percent)/2);
        Factory m = new Factory(layout.length,layout[0].length,rules);
        m.copyLayout(this.layout);
        Machine[][] tempLayout = m.getLayout();
        Random random = new Random();
        for (int i = 0; i < tilesToSwap; i++) {
            int x1 = random.nextInt(layout.length);
            int y1 = random.nextInt(layout[0].length);
            int x2 = random.nextInt(layout.length);
            int y2 = random.nextInt(layout[0].length);
            m.setMachine(x1,y1,layout[x2][y2]);
        }
        m.enforceRules(this,this);
        m.evaluateLayout();
        if(this.getScore() <m.getScore()){
            this.copyLayout(m.getLayout());
        }
    }

    private double scoreInt(Machine a, Machine b){
        /*double negativeResultModifier = .0001d;
        double positiveResultModifier = 1d;

        if(compareMachines(a,b)<0){
            return negativeResultModifier * compareMachines(a,b);
        }
        if(!(a.x-b.x == 0 && a.y - b.y == 0))
            return (compareMachines(a,b) * positiveResultModifier)/Math.sqrt(((Math.pow(a.x-b.x,2)+Math.pow(a.y-b.y,2))));
        else{
            return 0;
        }*/
        return a.scoreMachine(b);
    }
    private HashSet<Machine> getNeighbors(int x, int y, int recurse){
        if( x < 0 || x >= layout.length || y < 0 || y >= layout[0].length){
            return new HashSet<>();
        }
        HashSet<Machine> machines = new HashSet<>();
        machines.add(layout[x][y]);
        if (recurse > 0) {
            for(Machine m: machines) {
                machines.addAll(getNeighbors(m.x, m.y-1, recurse - 1));
                machines.addAll(getNeighbors(m.x-1, m.y, recurse - 1));
                machines.addAll(getNeighbors(m.x+1, m.y, recurse - 1));
                machines.addAll(getNeighbors(m.x, m.y+1, recurse - 1));
            }
        }
        return machines;
    }

    private static int compareMachines(Machine a, Machine b){
        if(a.getName().equals(b.getName()))
            return -1;
        return Machine.compareTiles(a.getName(),b.getName());
    }

    private int getMachineNum(Machine t){
        int sum = 0;
        for(Machine[] q: layout){
            for(Machine b: q){
                if(b.equals(t)){
                    sum++;
                }
            }
        }
        return sum;
    }

    //TODO needs work.
    void copyLayout(Machine[][] layout){
        for(Tiles t: Tiles.values()){
            currentMachines.put(t,new ArrayList<>());
        }
        this.layout = new Machine[layout.length][];
        for(int i = 0; i < layout.length; i++){
            this.layout[i] = new Machine[layout[0].length];
            for(int j = 0; j < this.layout[0].length; j++){
                this.layout[i][j] = new Machine(layout[i][j]);
                setMachine(i,j,layout[i][j]);
            }

        }
    }
    private void setMachine(int x, int y, Machine t){
        Machine m = layout[x][y];
        currentMachines.forEach((k,v)->v.remove(m));
        currentMachines.get(t.getName()).add(m);
        layout[x][y].setName(t.getName());
    }

    Machine getMachine(int x, int y){ return layout[x][y];    }

    Machine[][] getLayout(){
        return layout;
    }

    double getScore(){
        return score;
    }

    @Override
    public int compareTo(Factory o){
        return Double.compare(getScore(),o.getScore());
    }

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
