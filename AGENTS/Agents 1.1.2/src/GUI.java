import javax.swing.*;
import java.awt.*;

public class GUI extends JPanel {
    WorldMap worldMap;
    public GUI(WorldMap worldMap) {
        this.worldMap = worldMap;

        JFrame mainF = new JFrame();
        mainF.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainF.setExtendedState(Frame.MAXIMIZED_BOTH);
        mainF.setSize(500,700);
        mainF.setLocationRelativeTo(null);
        mainF.add(this);
        mainF.setBackground(Color.DARK_GRAY);
        this.setBackground(Color.DARK_GRAY);
        mainF.setVisible(true);

    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int rightLeftOffset = 50;
        int upDownOffset = 50;

        // çizgi
        g.setColor(Color.GRAY);
        for (int a = 0; a < WorldMap.boxCountY+1; a++)
        {
            g.drawLine(rightLeftOffset, upDownOffset + (int) (WorldMap.boxHeight*a), rightLeftOffset + (int) (WorldMap.boxWidth* WorldMap.boxCountX), upDownOffset + (int) (WorldMap.boxHeight*a));
        }

        // çizgi
        g.setColor(Color.GRAY);
        for (int b = 0; b < WorldMap.boxCountX+1; b++)
        {
            g.drawLine(rightLeftOffset + (int) (WorldMap.boxWidth*b),upDownOffset, rightLeftOffset + (int) (WorldMap.boxWidth*b), upDownOffset + WorldMap.gridHeight);
        }

        // engeller
        g.setColor(Color.BLUE);
        for (int a = 0; a < worldMap.getObstacleCount(); a++)
        {
            Obstacle o = worldMap.getObstacle(a);

            g.fillRect((int)(o.getXInPx()-o.getWidthInPx()/2) + rightLeftOffset,
                    (int)(o.getYInPx()-o.getHeightInPx()/2) + upDownOffset,
                    (int) o.getWidthInPx(),
                    (int) o.getHeightInPx());
        }

        // amaçlar
        g.setColor(Color.WHITE);
        for (int a = 0; a < worldMap.getTargetCount(); a++)
        {
            Target t = worldMap.getTarget(a);

            g.fillRect((int)(t.getXInPx()-t.getWidthInPx()/2) + rightLeftOffset,
                    (int)(t.getYInPx()-t.getHeightInPx()/2) + upDownOffset,
                    (int) t.getWidthInPx(),
                    (int) t.getHeightInPx());
        }

        // araçlar
        g.setColor(Color.RED);
        for (int a = 0; a < worldMap.getVehicleCount(); a++) {
            Vehicle v = worldMap.getVehicle(a);

            double [][] polygonDouble = DirectionalRect.coordinatesOfRectangeWithDirection(v.getXInPx(), v.getYInPx(), (int) v.getWidthInPx(), (int) v.getHeightInPx(), v.getDirection());
            int [][] polygonInt = DirectionalRect.alignCenter(polygonDouble);
            for(int b = 0; b < polygonInt[0].length; b++) {
                polygonInt[0][b] += rightLeftOffset; // [0][a] = x(a) // [1][a] = y(a)
            }
            for(int b = 0; b < polygonInt[0].length; b++) {
                polygonInt[0][b] += upDownOffset;
            }
            g.fillPolygon(polygonInt[0], polygonInt[1], polygonInt[0].length);
        }
    }


}
