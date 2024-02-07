public class Rotation {
    // if there is no origin Dot then it rotates around avarage of the dots
    // şf there is origin dot it rotates the thing around that dot
    // rectFromDot method takea upper left dot and width height direction after that calculates rotation
    //rect method takes 4 dots
    // instead of dot line segment or rectangle we can give Shape as parameter and n doesnt have to be 1,2,4 (n: number of dots)
    public static void rotateDot(Dot dot0, double angle) {
        double alpha = Math.atan(dot0.y/dot0.x);
        if (alpha != alpha) alpha = Math.toRadians(90);
        double absPath = Math.sqrt(Math.pow(dot0.x,2) + Math.pow(dot0.y,2));
        double lastAngle = alpha + Math.toRadians(angle);
        dot0.set(new Dot(Math.cos(lastAngle)*absPath, Math.sin(lastAngle)*absPath));
    }
    public static void rotateDot(Dot originDot, Dot dot0, double angle) {
        if (originDot.x == dot0.x && originDot.y == dot0.y) return;
        Dot differenceDot = new Dot();
        differenceDot = dot0.subtract(originDot);
        rotateDot(differenceDot, angle);
        dot0.set(originDot.add(differenceDot));
    }
    public static void rotateLineSegment(Dot originDot, Dot dot0, Dot dot1, double angle) {
        rotateDot(originDot, dot0, angle);
        rotateDot(originDot, dot1, angle);
    }
    public static Shape rotateRectangleFromDot(Dot originDot, Dot leftUpDot, int width, int height, double direction, double rotationAngle) {
        Dot dot1 = new Dot(leftUpDot.x+width,leftUpDot.y);
        Dot dot2 = new Dot(leftUpDot.x+width,leftUpDot.y-height);
        Dot dot3 = new Dot(leftUpDot.x,leftUpDot.y-height);
        rotateRectangle(leftUpDot,leftUpDot,dot1,dot2,dot3,direction);
        rotateRectangle(originDot,leftUpDot,dot1,dot2,dot3,rotationAngle);
        return new Shape(leftUpDot,dot1,dot2,dot3);
    }
    public static void rotateRectangle(Dot originDot, Dot dot0, Dot dot1, Dot dot2, Dot dot3, double angle) {
        if (angle == 0) return;
        rotateDot(originDot, dot0, angle);
        rotateDot(originDot, dot1, angle);
        rotateDot(originDot, dot2, angle);
        rotateDot(originDot, dot3, angle);
    }
    public static Shape rotateRectangleFromDot(Dot leftUpDot, int width, int height, double direction, double rotationAngle) {
        Dot dot1 = new Dot(leftUpDot.x+width,leftUpDot.y);
        Dot dot2 = new Dot(leftUpDot.x+width,leftUpDot.y-height);
        Dot dot3 = new Dot(leftUpDot.x,leftUpDot.y-height);
        rotateRectangle(leftUpDot,leftUpDot,dot1,dot2,dot3,direction);

        Dot originDot = new Dot((leftUpDot.x+dot1.x+dot2.x+dot3.x)/4, (leftUpDot.y + dot1.y + dot2.y + dot3.y)/4);
        rotateRectangle(originDot,leftUpDot,dot1,dot2,dot3,rotationAngle);
        return new Shape(leftUpDot,dot1,dot2,dot3);
    }
    public static void rotateRectangle(Dot dot0, Dot dot1, Dot dot2, Dot dot3, double angle) {
        Dot originDot = new Dot((dot0.x + dot1.x + dot3.x + dot3.x)/4, (dot0.y + dot1.y + dot2.y + dot3.y)/4);
        rotateDot(originDot, dot0, angle);
        rotateDot(originDot, dot1, angle);
        rotateDot(originDot, dot2, angle);
        rotateDot(originDot, dot3, angle);
    }
    public static void rotateShape(Dot originDot, Shape shape, double angle) {
        for (Dot dot: shape) {
            rotateDot(originDot,dot,angle);
        }

    }
    public static void rotateShape(Shape shape, double angle) {
        double avarageX = 0, avarageY = 0, size = 0;
        for (Dot dot: shape) {
            avarageX+= dot.x;
            avarageY += dot.y;
            size++;
        }
        Dot originDot = new Dot(avarageX/size, avarageY/size);

        rotateShape(originDot, shape, angle);
    }

    // from now on below part is about screen error fixation
    public static void changeYDir(Dot dot) {
        dot.y*=-1;
    }
    public static void changeYDir(Dot... dots) {
        for (Dot dot: dots) {
            dot.y*=-1;
        }
    }
    public static void changeYDir(Shape shape) {
        for (int a = 0; a < shape.getCornerCount(); a++) {
            shape.getDot(a).y *= -1;
        }
    }
    public static void changeYDir(Shape... shapes) {
        for (Shape shape: shapes) {
            for (int a = 0; a < shape.getCornerCount(); a++) {
                shape.getDot(a).y *= -1;
            }
        }
    }
    public static void ChangeScreenOrigin(int screenHeight, Dot dot) {
        dot.y*=-1;
    }
    public static void ChangeScreenOrigin(int screenHeight, Dot... dots) {
        for (Dot dot: dots) {
            dot.y*=-1;
        }
    }
    public static void ChangeScreenOrigin(int screenHeight, Shape shape) {
        for (Dot dot: shape) {
            dot.y *= -1;
        }
    }
    public static void ChangeScreenOrigin(int screenHeight, Shape... shapes) {
        for (Shape shape: shapes) {
            for (Dot dot: shape) {
                dot.y *= -1;
            }
        }
    }


}
