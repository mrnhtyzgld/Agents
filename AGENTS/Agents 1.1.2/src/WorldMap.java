import java.util.ArrayList;
import java.util.Random;

public class WorldMap {
    public static void main(String[] args) throws Exception {
        new WorldMap();
        //Dot a = new Dot(10,10);
        //Dot o = new Dot(15,15);
        //Rotation.rotateDot(o,a,0);
        //System.out.println(a.x + " " + a.y);
    }
    public int getObstacleCount() {
        return obstacles.size();
    }
    public int getVehicleCount() {
        return vehicles.size();
    }
    public int getTargetCount() {
        return targets.size();
    }

    public Obstacle getObstacle(int index) {
        return obstacles.get(index);
    }
    public Target getTarget(int index) {
        return targets.get(index);
    }
    public Vehicle getVehicle(int index) {
        return vehicles.get(index);
    }
    private ArrayList<Obstacle> obstacles = new ArrayList<>();
    private ArrayList<Target> targets = new ArrayList<>();
    private ArrayList<Vehicle> vehicles = new ArrayList<>();
    private final Entity[][] grid = new Entity[boxCountY][boxCountX];
    /*
    00 01 02 ... 0n
    10 11 12 ... 1n
    20 21 22 ... 2n
    .  .  .      .
    .  .  .      .
    .  .  .      .
    n0 n1 n2 ... nn

    matrix and the continuous world
     */


    // there might be an algo for changing one of the below values then it changes other two vals by itself
    final static int boxCountX = 80, boxCountY = 40;
    final static int gridWidth = 1400, gridHeight = 700;
    final static int boxWidth =  gridWidth / boxCountX;
    final static int boxHeight = gridHeight / boxCountY;

    private final GUI myGui;
    //

    // TODO arrlist kendisni döndürmeden iterator döndürerek for loop kurmaya çalış gizlilik koruyarak

    private WorldMap() {
        Random r = new Random();
        myGui = new GUI(this);

        //generateEntity(new Obstacle(1,3));
        while (!generateEntity(new Target(6,6))){}//r.nextInt(5), r.nextInt(5)))) {} // TODO
        while (!generateEntity(new Vehicle(60,30))){}//r.nextInt(20), r.nextInt(20)))) {}
        generateRandomObstacles(31);
        while (true) { // MAIN THREAD
            for(Vehicle vehicle: vehicles) {
                vehicle.nextFrame();
                myGui.repaint();
            }
        }
    }

    private boolean generateEntity(Entity e) {
        //if (e.getYIndex()<1 || e.getXIndex()<1 || e.getWidthInBlocks()<1 || e.getHeightInBlocks()<1) return false;
        if (e.getHeightInBlocks() + e.getYIndex() - 1 > boxCountY) return false;
        if (e.getWidthInBlocks() + e.getXIndex() - 1 > boxCountX) return false;
        for (int a = 0; a < e.getHeightInBlocks();a++) {
            for (int b = 0; b < e.getWidthInBlocks(); b++) {
                if (grid[e.getYIndex() + a][e.getXIndex() + b] != null) return false;
            }
        }

        for (int a = 0; a < e.getHeightInBlocks(); a++) {
            for (int b = 0; b < e.getWidthInBlocks(); b++) {
                grid[e.getYIndex() + a][e.getXIndex() + b] = e;
            }
        }

        if (e instanceof Obstacle) obstacles.add((Obstacle) e);
        if (e instanceof Target)   targets.add((Target) e);
        if (e instanceof Vehicle)  {
            vehicles.add((Vehicle) e);
            ((Vehicle) e).setWorldMap(this); // this is required when vehicle is able to see it needs to fetch data from somewhere
        }

        return true;
    }

    private void generateRandomObstacles(int count) {
        Random r = new Random();

        for (int a = 0; a < count; a++) {
            Obstacle o = new Obstacle(0, 0);
            do {
                o.setXIndex(r.nextInt(WorldMap.boxCountX-o.getWidthInBlocks())); // TODO -1?
                o.setYIndex(r.nextInt(WorldMap.boxCountY-o.getHeightInBlocks()));
            } while (!generateEntity(o));
        }
    }
    public Entity getCell(int x, int y) {
        return grid[y][x];
    }
}
