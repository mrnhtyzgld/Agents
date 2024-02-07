import java.util.ArrayList;

public class SupercoverLineAlgorithm {
    private int cellWidth, cellHeight;
    public class Cell {
        int xIndex, yIndex;
        boolean isFromTheSide = false;

        public Cell(int xIndex, int yIndex, boolean isFromTheSide) {
            this.xIndex = xIndex;
            this.yIndex = yIndex;
            this.isFromTheSide = isFromTheSide;
        }
    }
    public SupercoverLineAlgorithm(int cellWidth, int cellHeight) {
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
    }
    public ArrayList<Cell> getIntersectedCells(Dot dot0, Dot dot1) {
        return getIntersectedCells(dot0.x,dot0.y,dot1.x,dot1.y);
    }


    public ArrayList<Cell> getIntersectedCells(double x1, double y1, double x2, double y2) {
        
        boolean isToRight = x2 >= x1; // true and true is the expected case
        if (!isToRight) {
            double temp = x2;
            x2 = x1;
            x1 = temp;
            temp = y2;
            y2 = y1;
            y1 = temp;
        }
        boolean isToUp = y2 >= y1;
        boolean isInfSlope = x1 == x2;

        double realSlope = isInfSlope ? 0 : (y2 - y1) / (x2 - x1);
        double idealSlope = 0;

        int currCellXIndex = (int) x1 / cellWidth;
        int currCellYIndex = (int) y1 / cellHeight;
        int dx = ((int) x1 / cellWidth + 1) * cellWidth - (int) x1;
        int dy = ((int) y1 / cellHeight  + 1) * cellHeight - (int) y1;
        if (!isToUp) {
            dy = ((int) y1 / cellHeight)*cellHeight - (int)y1;
        }
        int lastCellXIndex = (int) x2 / cellWidth;
        int lastCellYIndex = (int) y2 / cellHeight;

        ArrayList<Cell> toReturn = new ArrayList<>();

        cellNo(currCellXIndex, currCellYIndex, false, isToRight, toReturn);

        while (!(currCellXIndex == lastCellXIndex && currCellYIndex == lastCellYIndex)) {
            if (!isInfSlope) {
                idealSlope = (double) dy / dx;
            }

            if (isToUp) {
                if (!isInfSlope && idealSlope > realSlope) {
                    dx += cellWidth;
                    currCellXIndex += 1;

                } else if (!isInfSlope && idealSlope == realSlope) {
                    dx += cellWidth;
                    dy += cellHeight;
                    if (currCellXIndex == lastCellXIndex && currCellYIndex == lastCellYIndex-1)
                        currCellXIndex--;
                    if (currCellXIndex == lastCellXIndex-1 && currCellYIndex == lastCellYIndex)
                        currCellYIndex--;
                    //cellNo(currCellXIndex+1, currCellYIndex, true, isToRight, toReturn);
                    //cellNo(currCellXIndex, currCellYIndex+1, true, isToRight, toReturn);

                    currCellXIndex += 1;
                    currCellYIndex += 1;

                } else if (isInfSlope || idealSlope < realSlope) {
                    dy += cellHeight;
                    currCellYIndex += 1;

                }
            } else {

                if (isInfSlope || idealSlope > realSlope) {
                    dy -= cellHeight;
                    currCellYIndex -= 1;

                } else if (!isInfSlope && idealSlope == realSlope) {
                    dx += cellWidth;
                    dy -= cellHeight;
                    if (currCellXIndex == lastCellXIndex && currCellYIndex == lastCellYIndex+1)
                        currCellXIndex--;
                    if (currCellXIndex == lastCellXIndex-1 && currCellYIndex == lastCellYIndex)
                        currCellYIndex++;
                    //cellNo(currCellXIndex, currCellYIndex-1, true, isToRight, toReturn);
                    //cellNo(currCellXIndex+1, currCellYIndex, true, isToRight, toReturn);
                    currCellXIndex += 1;
                    currCellYIndex -= 1;

                } else if (!isInfSlope && idealSlope < realSlope) {
                    dx += cellHeight;
                    currCellXIndex += 1;

                }
            }


            cellNo(currCellXIndex, currCellYIndex, false, isToRight, toReturn);


        }

        return toReturn;
    }
    private void cellNo(int x, int y, boolean isFromTheSide, boolean isToRight, ArrayList<Cell> list) {
        if (isToRight && isFromTheSide) {
            list.add(new Cell(x, y, true));

        }
        if (isToRight && !isFromTheSide){
            list.add(new Cell(x, y, false));

        }
        if (!isToRight && isFromTheSide) {
            list.add(0, new Cell(x, y, true));

        }
        if (!isToRight && !isFromTheSide){
            list.add(0, new Cell(x, y, false));

        }
    }

    // hücrelerin kenarındayken tüm hücrleri alma problemini çöz
    // TODO bu kodun hepsini daha simplistic tekrar yaz mükemmel olsun
    // DEFUNCT needs more thinking
    //public ArrayList<Cell> getIntersectedCells(int x1, int y1, int x2, int y2) {
    //    return getIntersectedCells(x1+0.5,y1+0.5,x2+0.5,y2+0.5); // TODO dunno if thats true but its along the ways
    //}
}
