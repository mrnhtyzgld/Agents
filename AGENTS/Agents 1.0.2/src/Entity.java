public class Entity {
    private final Dot position;
    private final int width, height;

    public Entity(int firstXInBlocks, int firstYInBlocks, int widthInBlocks, int heightInBlocks) {

        this.width = widthInBlocks;
        this.height = heightInBlocks;
        position = new Dot(firstXInBlocks* WorldMap.boxWidth+this.getWidthInPx()/2,firstYInBlocks* WorldMap.boxWidth+this.getHeightInPx()/2);
    }
    /*
    public double getXInBlocks() {
        return getXInPx()/WorldMap.boxWidth;
    }
    public double getYInBlocks() {
        return getYInPx()/WorldMap.boxHeight;
    }

     */
    public Dot getDotInPx() {
        return position;
    }
    public double getXInPx() {
        return position.x;
    }
    public double getYInPx() {
        return position.y;
    }

    public void setXInPx(double x) {
        this.position.x = x;
    }
    public void setYInPx(double y) {
        this.position.y = y;
    }

    public void incXInPx(double x) {
        this.position.x += x;
    }
    public void incYInPx(double y) {
        this.position.y += y;
    }

    public void decXInPx(double x) {
        this.position.x -= x;
    }
    public void decYInPx(double y) {
        this.position.y -= y;
    }

    /*
    public void setXInBlocks(int x) {
        this.x = x * WorldMap.boxWidth;
    }
    public void setYInBlocks(int y) {
        this.y = y * WorldMap.boxHeight;
    }

     */
    public int getWidthInBlocks() {
        return width;
    }
    public int getHeightInBlocks() {
        return height;
    }
    public double getWidthInPx() {
        return getWidthInBlocks()* WorldMap.boxWidth;
    }
    public double getHeightInPx() {
        return getHeightInBlocks()* WorldMap.boxHeight;
    }


    public int getYIndex() {
        return (int)((getYInPx()-getHeightInPx()/2)/ WorldMap.boxHeight);
    }
    public int getXIndex() {
        return (int)((getXInPx()-getWidthInPx()/2)/ WorldMap.boxWidth);
    }
    public void setYIndex(int index) {
        setYInPx(index* WorldMap.boxHeight+getHeightInPx()/2);
    }
    public void setXIndex(int index) {
        setXInPx(index* WorldMap.boxHeight+getWidthInPx()/2);
    }
}
