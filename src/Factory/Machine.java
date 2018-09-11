package Factory;

public class Machine implements Comparable<Machine> {
    public void setName(Tiles name) {
        this.name = name;
    }

    Tiles name;
    int x,y;

    public Machine( int x, int y, Tiles name){
        this.name = name;
        this.x = x;
        this.y = y;
    }

    @Override
    public int compareTo(Machine o) {
        return name.compareTo(o.getName());
    }

    public Tiles getName(){
        return name;
    }

    public static int compareTiles(Tiles a, Tiles b){
        int aValue = getTileValue(a);
        int bValue = getTileValue(b);
        if (a.equals(b) || a.equals(Tiles.EMPTY) || b.equals(Tiles.EMPTY)){
            return 0;
        }
        return (aValue + bValue) % 6;
    }

    private static int getTileValue(Tiles a){
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
