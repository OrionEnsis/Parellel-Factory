package Factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Factory implements Comparable<Factory>{
    private int score;
    private Machine[][] layout;
    private HashMap<Tiles,Integer> rules;
    private HashMap<Tiles,ArrayList<Machine>> currentMachines = new HashMap<>();

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
    }

    public void generateNewLayout(){
        Random r = new Random();
        for(HashMap.Entry<Tiles,Integer> entry: rules.entrySet()){
            for(int i = 0; i < entry.getValue(); i++){
                int x = r.nextInt(currentMachines.get(Tiles.EMPTY).size());
                Machine m = currentMachines.get(Tiles.EMPTY).get(x);
                m.setName(entry.getKey());
                currentMachines.get(entry.getKey()).add(m);
                currentMachines.get(Tiles.EMPTY).remove(m);
                /*int y = r.nextInt(layout[0].length);
                if(layout[x][y].getName().equals(Tiles.EMPTY)){
                    layout[x][y].setName(entry.getKey());
                    currentMachines.get(entry.getKey()).add(layout[x][y]);
                }
                else{
                    i--;
                }*/
            }
        }
    }

    public Factory crossBreed(Factory otherFactory){
        Random random = new Random();
        Factory child = new Factory(layout.length,layout[0].length, rules);
        ArrayList<Machine> nonMatchingMachines = new ArrayList<>();

        //get matching tiles (keep these)
        for(int i = 0; i < layout.length;  i++){
            for(int j = 0; j < layout[0].length; j++){
                if(layout[i][j].equals(otherFactory.getMachine(i,j))){
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

        //run mutation.

        //evalueate performance
        evaluateLayout();

        return child;
    }

    private void enforceRules(Factory factoryA, Factory factoryB){
        Random random = new Random();
        currentMachines.get(Tiles.EMPTY).forEach(v ->{
            if(!v.getName().equals(Tiles.EMPTY))
                System.out.println("x: " + v.x + " y: " + v.y + " Name: " + v.getName());
        });
        currentMachines.forEach((k,v)->{
            //remove too random too high one.
            if(!k.equals(Tiles.EMPTY)) {
                while (v.size() > rules.get(k)) {
                    int r = random.nextInt(v.size());
                    v.get(r).setName(Tiles.EMPTY);
                    currentMachines.get(Tiles.EMPTY).add(v.get(r));
                    v.remove(r);
                }
                //add to random low member
                while (v.size() < rules.get(k)) {
                    int x = random.nextInt(currentMachines.get(Tiles.EMPTY).size());
                    Machine m = currentMachines.get(Tiles.EMPTY).get(x);
                    m.setName(k);
                    currentMachines.get(k).add(m);
                    currentMachines.get(Tiles.EMPTY).remove(m);
                /*int y = random.nextInt(layout[0].length);

                if(layout[x][y].getName().equals(Tiles.EMPTY)){
                    layout[x][y].setName(k);
                    v.add(layout[x][y]);
                }*/
                }
            }
        });
        currentMachines.get(Tiles.EMPTY).forEach(v ->{
            if(!v.getName().equals(Tiles.EMPTY))
                System.out.println("x: " + v.x + " y: " + v.y + " Name: " + v.getName());
        });
    }

    public void mutate(float percentSwap){

    }

    public void evaluateLayout(){
        score = 0;

        for(int i = 0; i < layout.length; i++){
            for(int j = 0; j < layout[0].length; j++){
                if( i > 0){
                    score += compareMachines(layout[i][j],layout[i-1][j]);
                }
                if( j > 0){
                    score += compareMachines(layout[i][j],layout[i][j-1]);
                }
                if( i > 0 && j > 0){
                    score += compareMachines(layout[i][j],layout[i-1][j-1]);
                }
            }
        }
    }

    int compareMachines(Machine a, Machine b){
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

    public void copyLayout(Machine[][] layout){
        this.layout = new Machine[layout.length][];
        for(int i = 0; i < layout.length; i++){
            Machine[] amatrix = layout[i];
            int j = layout[i].length;
            this.layout[i] = new Machine[j];
            System.arraycopy(layout[i],0,this.layout[i],0,this.layout[i].length);
        }
    }

    private void setMachine(int x, int y, Machine t){
        layout[x][y].setName(t.getName());
        currentMachines.forEach((k,v)->v.remove(t));
        currentMachines.get(t.getName()).add(t);
    }

    public Machine getMachine(int x, int y){ return layout[x][y];    }

    public Machine[][] getLayout(){
        return layout;
    }

    public int getScore(){
        return score;
    }

    @Override
    public int compareTo(Factory o){
        return Integer.compare(getScore(),o.getScore());
    }

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
