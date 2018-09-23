package GeneticFactory.Factory;

public class Machine implements Comparable<Machine> {


    Tiles name;
    int x,y;
    private int preProduct;
    private int maxFlow;
    private int product;
    private Tiles inputMachine;
    private Tiles outputMachine;
    public Machine( int x, int y, Tiles name){
        this.name = name;
        this.x = x;
        this.y = y;
        getInputMachine();
        getOutputMachine();
        determineMaxFlow();
    }

    public Machine(Machine m){
        this(m.x,m.y,m.getName());
    }

    void resetMachine(){
        preProduct = 0;
        product = 0;
    }

    private void determineMaxFlow(){
        switch(name){
            case A:
                maxFlow = 64;
                break;
            case B:
                maxFlow = 48;
                break;
            case C:
                maxFlow = 48;
                break;
            case D:
                maxFlow = 32;
                break;
            case E:
                maxFlow = 64;
                break;
            case EMPTY:
                maxFlow = 0;
                break;
        }
    }

    private void getInputMachine(){
        switch(name){
            case A:
                inputMachine = Tiles.EMPTY;
                break;
            case B:
                inputMachine =  Tiles.A;
                break;
            case C:
                inputMachine =  Tiles.B;
                break;
            case D:
                inputMachine =  Tiles.C;
                break;
            case E:
                inputMachine =  Tiles.D;
                break;
            case EMPTY:
                inputMachine = null;
                break;
        }
    }
    void getPreproductFromInputMachine(Machine m){
        if(distance(m) == 0)
            return;
        if (m.getName().equals(inputMachine)){
            int neededToMaxFlow = maxFlow - preProduct;
            int demand = neededToMaxFlow/distance(m);
            preProduct += getProduct(demand);
        }
    }

    void makeProduct(){
        if(!getName().equals(Tiles.A))
            product = preProduct;
        else
            product = maxFlow;
    }

    public int distance(Machine m){
        return Math.abs(x-m.x + y - m.y);
    }
    public int getProduct(int demand){
        if(product > demand){
            product-= demand;
            return demand;
        }
        else{
            int temp = product;
            product = 0;
            return temp;
        }
    }
    private void getOutputMachine(){
        switch(name){
            case A:
                outputMachine = Tiles.B;
                break;
            case B:
                outputMachine =  Tiles.C;
                break;
            case C:
                outputMachine =  Tiles.D;
                break;
            case D:
                outputMachine =  Tiles.E;
                break;
            case E:
                outputMachine =  Tiles.EMPTY;
                break;
            case EMPTY:
                outputMachine = null;
                break;
        }
    }

    @Override
    public int compareTo(Machine o) {
        return name.compareTo(o.getName());
    }

    Tiles getName(){
        return name;
    }

    void setName(Tiles name) {
        this.name = name;
        getInputMachine();
        getOutputMachine();
    }

    static int compareTiles(Tiles a, Tiles b){
        int aValue = getProductionRate(a);
        int bValue = getProductionRate(b);
        if (a.equals(b) || a.equals(Tiles.EMPTY) || b.equals(Tiles.EMPTY)){
            return 0;
        }
        return (aValue + bValue) % 6;
    }

    int scoreMachine(Machine other) {
        int distance = Math.abs(x - other.x + y - other.y);
        if (distance != 0){
            if (other.getName().equals(inputMachine) || other.getName().equals(outputMachine)) {
                return 3 / distance;
            } else {
                return -1/distance;
            }
        }
        return 0;
    }


    private static int getProductionRate(Tiles a){
        switch(a){
            case A:
                return 1;
            case B:
                return 2;
            case C:
                return 3;
            case D:
                return 4;
            case E:
                return 5;
            case EMPTY:
                return 0;
        }
        return 0;
    }
}
