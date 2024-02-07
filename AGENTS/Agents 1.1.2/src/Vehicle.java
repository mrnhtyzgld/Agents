import java.util.ArrayList;
import java.util.Random;

public class Vehicle extends Entity{
    private WorldMap worldMap;
    public void setWorldMap(WorldMap worldMap) {
        if(this.worldMap == null)
            this.worldMap = worldMap;
    }

    private final Perception myPerceptionModule; // needs to be public for GUI to access // or give this class methods to give gui class outputs
    private final Learning myLearningModule;
    private final Decision myDecisionModule;
    private final Movement myMovementModule;

    // Vehicle's understanding of its environnment
    private final class Cell {
        boolean isKnown = false;
        Entity inside = null;
        Integer x,y; // important! used when sensor detects it and send to other module its constructs after construction
        // TODO can make it so cell operations are here, learning a cell, fetching its data from worldMap, instantiating etc
        // TODO also surface can be accessed everywhere in this class so no need to make getter setter etc
        // TODO thats why the best approach would be the make them different classes
        // TODO 4 supercover line algo for chechking intersetion is bad check and update each frame the main grid after that if main grid vals intersects then thr err
        public void memorize(Entity data, int x, int y) {
            this.inside = data;
            this.isKnown = true;
            this.y = y;
            this.x = x;
        }
        public Cell(boolean isKnown, Entity inside) {
            this.isKnown = isKnown;
            this.inside = inside;
        }
        public Cell() {

        }
    }
    private class Perception{
        ArrayList<Sensor> mySensors = new ArrayList<>();
        public class Sensor {

            Dot connectionDotInitializing; // this is where the dot is in regard to Vehicle's center is
            double length;
            double connectionAngle;

            public Dot getConnectionDot() {
                Dot connectionDot = new Dot();
                connectionDot.set(connectionDotInitializing);
                Rotation.rotateDot(Vehicle.this.getDotInPx(),connectionDot,getDirection());
                return connectionDot;
            }
            public Dot getTipDot() {
                Dot tipDot = new Dot(length, 0); // might be 0, length instead of length, 0
                Rotation.rotateDot(tipDot, connectionAngle + getDirection());
                tipDot.add(getConnectionDot());
                return tipDot;
            }
            SupercoverLineAlgorithm algo;
            public ArrayList<Cell> getIntersected() {
                ArrayList<SupercoverLineAlgorithm.Cell> input;
                input = algo.getIntersectedCells(getConnectionDot(), getTipDot());

                ArrayList<Cell> output = new ArrayList<>();
                for (int a = 0; a < input.size(); a++) {
                    Entity data = worldMap.getCell(input.get(a).xIndex, input.get(a).yIndex);
                    Cell newCell = new Cell(true, data);
                    newCell.x = input.get(a).xIndex; // TODO can we convert that construction
                    newCell.y = input.get(a).yIndex; // TODO can we convert that construction
                    output.add(newCell); // will we need the coordinates of the cells that we saw with our sensor

                    if (data instanceof Obstacle) // when multiplying the numbers of vehicles ect. this is something to consider
                        break;
                }

                return output;
            }

            public Sensor(Dot connectionDot, int length, int connectionAngle) {
                // imagine a 90 degree vehicle (or 0?)
                connectionDotInitializing = connectionDot;
                this.length = length;
                this.connectionAngle = connectionAngle;
                this.algo = new SupercoverLineAlgorithm(WorldMap.boxWidth, WorldMap.boxHeight);
            }
        }

        public Perception() {
            mySensors.add(new Sensor(new Dot(-2, -1.5), 3, 0));
            mySensors.add(new Sensor(new Dot(-1, -1.5), 3, 0));
            mySensors.add(new Sensor(new Dot(0,  -1.5), 3, 0));
            mySensors.add(new Sensor(new Dot(1,  -1.5), 3, 0));
            mySensors.add(new Sensor(new Dot(2,  -1.5), 3, 0));
        }
        public void nextFrame() {

        }

        public Entity getTouchedInfo() {
            // eğer vücut ile target intersect
            // eğer vücut ile engel intersect
            // ikisi de değilse null

            Dot leftUpDot = new Dot(Vehicle.this.getXInPx()-Vehicle.this.getWidthInPx()/2, Vehicle.this.getYInPx()-Vehicle.this.getHeightInPx()/2);
            Shape rect = Rotation.rotateRectangleFromDot(leftUpDot, (int) Vehicle.this.getWidthInPx(), (int) Vehicle.this.getHeightInPx(), Vehicle.this.getDirection(),0);
            SupercoverLineAlgorithm lineFinder = new SupercoverLineAlgorithm(WorldMap.boxWidth,WorldMap.boxHeight);
            ArrayList<SupercoverLineAlgorithm.Cell> wholePackage= new ArrayList<>();
            wholePackage.addAll(lineFinder.getIntersectedCells(rect.getDot(0),rect.getDot(1)));
            wholePackage.addAll(lineFinder.getIntersectedCells(rect.getDot(1),rect.getDot(2)));
            wholePackage.addAll(lineFinder.getIntersectedCells(rect.getDot(2),rect.getDot(3)));
            wholePackage.addAll(lineFinder.getIntersectedCells(rect.getDot(3),rect.getDot(0)));

            // this simplisifes all of these algos cells, clears clones
            for (int a = wholePackage.size()-1; a >= 0; a--) {
                SupercoverLineAlgorithm.Cell currElement = wholePackage.get(a);
                boolean isMultiple = false;
                for (int b = 0; b < wholePackage.size(); b++) {
                    if (wholePackage.get(b).yIndex == currElement.yIndex && wholePackage.get(b).xIndex == currElement.xIndex) {
                        isMultiple = true;
                        break;
                    }
                }
                if (isMultiple) {
                    wholePackage.remove(a);
                }
            }
            ArrayList<Entity> data = new ArrayList<>();
            for (SupercoverLineAlgorithm.Cell cell: wholePackage) {
                data.add(worldMap.getCell(cell.xIndex,cell.yIndex));
            }

            // if there is a target return that, if there is an obstacle return that (returning obstacle is prior)
            for (Entity ent: data) {
                if (ent instanceof Obstacle)
                    return ent;
                if (ent instanceof Target)
                    return ent;
            }

            return null;
        }

        public ArrayList<Cell> getSightInfo() {
            ArrayList<Cell> mySight = new ArrayList<>();
            for(Sensor sensor: mySensors) {
                for (Cell cell: sensor.getIntersected()) {
                    if (!mySight.contains(cell)) {
                        mySight.add(cell);
                    }
                }
            }
            return mySight;
        }
    }
    private class Learning {
        // extends threaad is uncomplete TODO
        private final Cell[][] knowledgeMap;
        private final ArrayList<Cell> surface = new ArrayList<>();

        public Learning() { 
            knowledgeMap = new Cell[WorldMap.boxCountY][WorldMap.boxCountX];

            for (int a = 0; a < knowledgeMap.length; a++) {
                for (int b = 0; b < knowledgeMap[a].length; b++) {
                    knowledgeMap[a][b] = new Cell();
                    knowledgeMap[a][b].y = a;
                    knowledgeMap[a][b].x = b;
                }
            }
            
            initializeSurface();
        }
        public void nextFrame() {
            updateSighthings(myPerceptionModule.getSightInfo());
        }
        public boolean isRecognized(Entity e) {
            for (int a = 0; a < knowledgeMap.length; a++) {
                for (int b = 0; b < knowledgeMap[a].length; b++) {
                    if (knowledgeMap[a][b].isKnown && knowledgeMap[a][b].inside.equals(e)) {
                        return true;
                    }
                }
            }
            return false;
        }
        public void recognize(Entity e, int x, int y) {
            knowledgeMap[e.getYIndex()][e.getXIndex()].memorize(e, x, y);
        }

        public void updateSighthings(ArrayList<Cell> cells) {
            // 1-perception saw some cells therefore we update them as known and
            // 2-delete them from surface and
            // 3-add freshly known cells unknown neighbours to these which are not in the list already

            // 1
            for (int a = 0; a < cells.size(); a++) {
                Cell currCell = cells.get(a);
                knowledgeMap[currCell.y][currCell.x].memorize(currCell.inside ,currCell.x,currCell.y);
            }
            // 2
            for (Cell cell: cells)
                surface.remove(cell);
            // 3
            for (Cell cell: cells)
                trySurfacingTheNeighbours(cell);
        }
        public void trySurfacingTheNeighbours(Cell cell){
            // tries to add that cells unknown neighbours if that cell is known
            if (cell.x == null || cell.y == null)
                try {
                    throw new Exception("Cell with null coordinates are given!");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            int a = cell.y;
            int b = cell.x;
            if (knowledgeMap[a][b].isKnown) {
                if (a + 1 <= knowledgeMap.length && !knowledgeMap[a + 1][b].isKnown)  {
                    surface.add(knowledgeMap[a+1][b]);
                }
                if (b + 1 <= knowledgeMap.length && !knowledgeMap[a][b + 1].isKnown) {
                    surface.add(knowledgeMap[a][b+1]);
                }
                if (a - 1 >= 0 && !knowledgeMap[a - 1][b].isKnown) {
                    surface.add(knowledgeMap[a-1][b]);
                }
                if (b - 1 >= 0 && !knowledgeMap[a][b - 1].isKnown) {
                    surface.add(knowledgeMap[a][b-1]);
                }
            }
        }
        public void initializeSurface() {
            // this is not that much efficient because it looks at every cell,
            // even though it doesnt operate on every cell, it still is not preferred
            for (int a = 0; a < knowledgeMap.length; a++) {
                for (int b = 0; b < knowledgeMap[a].length; b++) {
                    trySurfacingTheNeighbours(knowledgeMap[a][b]);
                }  
            }
        }
        public ArrayList<Entity> getAllRecognizedCells()  {
            ArrayList<Entity> toSend = new ArrayList<>();
            for (int a = 0; a < knowledgeMap.length; a++) {
                for (int b = 0; b < knowledgeMap[a].length; b++) {
                    if (knowledgeMap[a][b].isKnown) {
                        toSend.add(knowledgeMap[a][b].inside);
                    }
                }
            }
            return toSend;
        }
        private Cell getFromKnowings(int x, int y) {
            return knowledgeMap[y][x];
        }

    }
    private class Decision {
        boolean isTargetFound = false;
        boolean isTargetPathFound = false;
        Cell target = null;
        public Decision() {

        }
        public void nextFrame() {
            // intersected with an obstacle, program failed
            if (myPerceptionModule.getTouchedInfo() instanceof Obstacle) {
                //TODO DIE
                return;
            }

            // arrived to the target, program succesfully finished
            if (myPerceptionModule.getTouchedInfo() instanceof Target) {
                // TODO WIN
                return;
            }

            // if above conditions are not met, then we need to control if we are in danger (about to die)
            // that means if sightings have an obstacle we need to escape
            // if above condition did not meet, if we see the target we need to arrange our state to that
            // after that we need to check if we can find target path and that changes our state as well
            for (Cell cell : myPerceptionModule.getSightInfo()) {
                if (cell.inside instanceof Obstacle) {
                    myMovementModule.escapeCell(cell);
                    return;
                }
                if (cell.inside instanceof Target) {
                    if (!isTargetFound) {
                        isTargetFound = true;
                        target = cell;

                    }
                }

            }

            if (isTargetFound)
                if (!isTargetPathFound && isPathFeasible(target))
                    isTargetPathFound = true;


            if (isTargetPathFound) {
                myMovementModule.updatePath(getPath(target));
                return;
            }

            discover();
        }

        public void discover() {
            ArrayList<Path> paths = new ArrayList<>();
            for (Cell cell: myLearningModule.surface)
                if (isPathFeasible(cell))
                    paths.add(getPath(cell));

            int minPathVal = Integer.MAX_VALUE;
            for (int a = 0;a < paths.size(); a++)
                if (paths.get(a).length < minPathVal)
                    minPathVal = paths.get(a).length;

            for (int a = paths.size()-1; a >= 0; a--)
                if (paths.get(a).length != minPathVal)
                    paths.remove(a);

            // en küçük path length'e sahip elemanlar kaldı

            // random eleman seçme
            int randomVal = new Random().nextInt(paths.size());

            myMovementModule.updatePath(paths.get(randomVal));
            return;
        }
        public Path getPath(Cell target) {
            Path a = new Path();
            a.x = target.x;
            a.y = target.y;
            a.length = (int) (Math.pow(Math.abs(a.x-getXIndex()),2) + Math.pow(a.y-getYIndex(),2));




            return a;

        }
        public boolean isPathFeasible(Cell target) {
            // use A* or dijkstra
            // does not need to be "the target"
            // when objective is to go the target it is
            // when the objective is discover target is undiscovered territory
            // when the objective is escape we do not care about pathing TODO but that might change
            return true;
        }
    }
    private class Movement {
        final int turnSpeedLimit = 1; // TODO
        final int moveSpeed = 30; // TODO all finals

        private double direction = 0;

        boolean needsToEscape = false;
        Cell cellToEscape = null;
        Path path = null;
        public void nextFrame() {
            // 1-find the direction to move
            // 2-then move

            // 1
            // if emergency else ideality
            if (needsToEscape) {
                if (direction <= 135 && direction >= 45) { // sağ
                    if (getDotInPx().x- cellToEscape.x > 0)
                        changeDirectionTowards(getDirection() + 90);
                    else changeDirectionTowards(getDirection() - 90);


                } else if (direction <= 45 || direction >= 360-45) { // sol
                    if (getDotInPx().y- cellToEscape.y > 0)
                        changeDirectionTowards(getDirection() - 90);
                    else changeDirectionTowards(getDirection() + 90);



                } else if (direction <= 360-45 && direction >= 225) { // sol
                    if (getDotInPx().x- cellToEscape.x > 0)
                        changeDirectionTowards(getDirection() - 90);
                    else changeDirectionTowards(getDirection() + 90);

                } else { // sağ
                    if (getDotInPx().y- cellToEscape.y > 0)
                        changeDirectionTowards(getDirection() + 90);
                    else changeDirectionTowards(getDirection() - 90);

                }

                // maybe just as easy as changeDirectionTowards(atan(cellToEscape.position +180);

            } else {

                double idealDirection;
                idealDirection = Math.atan((double) (path.y - getYIndex()) / (path.x - getXIndex()));

                changeDirectionTowards(idealDirection);
            }

            needsToEscape = false;
            cellToEscape = null;
            path = null;
            // 2
            move();

        }
        int deltaTime; // TODO empty needs some sort of thread // TODO

        public void move() {
            Vehicle.this.incXInPx(moveSpeed * Math.cos(direction) * deltaTime);
            Vehicle.this.incXInPx(moveSpeed * Math.sin(direction) * deltaTime);
        }
        // pseudo methods
        public void escapeCell(Cell cell) {
            needsToEscape = true;
            cellToEscape = cell;
        }
        public void updatePath(Path path) {
            this.path = path;

        }
        // direction methods
        public void changeDirectionTowards(double direction) {
            // this method assures that changing direction while staying within the limits
            double idealChangeInDirection = direction-getDirection();
            if (turnSpeedLimit > Math.abs(idealChangeInDirection)) {
                incDirection(idealChangeInDirection);
                return;
            }

            if (direction-getDirection() > 180) {
                incDirection(turnSpeedLimit);
            } else if (direction - getDirection() <= 180) {
                incDirection(-turnSpeedLimit);
            }
        }
        // we dont know how much but we now we need to rotate left or right
        public void wantToTurnLeft() {
            direction -= turnSpeedLimit;
        }
        public void wantToTurnRight() {
            direction += turnSpeedLimit;
        }
        // internal direction method, needs to check that direction is between [0,360]
        private void incDirection(double val) {
            this.direction += val;
            this.direction = (360 + direction) % 360;
        }
    }
    public Vehicle(int firstXInBlocks, int firstYInBlocks) {
        super(firstXInBlocks, firstYInBlocks, 5, 3);
        myPerceptionModule = new Perception();
        myLearningModule   = new Learning();
        myDecisionModule   = new Decision();
        myMovementModule   = new Movement();
    }
    public void nextFrame() {
        // these methods need to call all the other methods
        myPerceptionModule.nextFrame();
        myLearningModule.nextFrame();
        myDecisionModule.nextFrame();
        myMovementModule.nextFrame();
    }



    // methods to keep gui informed
    public double getDirection() {
        return myMovementModule.direction;
    }


}
