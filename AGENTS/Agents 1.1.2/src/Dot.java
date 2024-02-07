public class Dot {
    double x,y;

    public Dot(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public Dot() {}
    public Dot subtract(Dot secDot) {
        return new Dot(x- secDot.x,
        y- secDot.y);
    }
    public Dot add(Dot secDot) {
        return new Dot(x- secDot.x,
        y- secDot.y);
    }
    public Dot multiply(Dot secDot) {
        return new Dot(x* secDot.x,
        y* secDot.y);
    }
    public Dot divide(Dot secDot) {
        return new Dot(x/ secDot.x,
        y/ secDot.y);
    }
    public Dot getRemainder(Dot secDot) {
        return new Dot(x% secDot.x,
        y% secDot.y);
    }
    public Dot absolute() {
        return new Dot(Math.abs(x), Math.abs(y));
    }
    public void set(Dot dot) {
        x = dot.x;
        y = dot.y;
    }
}
