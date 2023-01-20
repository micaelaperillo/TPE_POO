package backend.model;

public class Point implements Movable{
    protected double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    //la referencia va a ser el centro del canvas en (width/2,height/2)
    @Override
    public void invertX(Point centerPoint){
        invertCoords(new Point(centerPoint.getX(), this.getY()));
    }

    @Override
    public void invertY(Point centerPoint){
        invertCoords(new Point(this.getX(), centerPoint.getY()));
    }
    @Override

    public void invertCoords(Point refPoint){
        Point destPoint = new Point(this.getX() - 2*diffX(refPoint), this.getY() - 2*diffY(refPoint));
        moveTo(destPoint);
    }
    @Override
    public void move(double diffX, double diffY) {
        x+=diffX;
        y+=diffY;
    }
    @Override
    public void moveTo(Point dest) {
        this.x = dest.x;
        this.y = dest.y;
    }

    public double diffX(Point other){
        return this.x - other.x;
    }

    public double diffY(Point other){
        return this.y - other.y;
    }

    @Override
    public String toString() {
        return String.format("{%.2f, %.2f}", x, y);
    }

    public Point copyPoint(){
        return new Point(this.x, this.y);
    }
}
