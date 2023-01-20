package backend.model;

import javafx.scene.canvas.GraphicsContext;

public class Rectangle extends Figure {
    private final Point topLeft, bottomRight;

    public Rectangle(Point topLeft, Point bottomRight, Format format) {
        super("Rectangle", format);
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    public Point getTopLeft() {
        return topLeft.copyPoint();
    }

    public Point getBottomRight() {
        return bottomRight.copyPoint();
    }

    @Override
    public String toString() {
        return String.format("%s [ %s , %s ]", getName(), topLeft, bottomRight);
    }

    @Override
    public void move(double diffX, double diffY) {
        this.topLeft.move(diffX, diffY);
        this.bottomRight.move(diffX, diffY);
    }

    @Override
    public void moveTo(Point dest) {
        Point newTopLeft = new Point(dest.getX() - topLeft.diffX(bottomRight)/2, dest.getY() - topLeft.diffY(bottomRight)/2);
        Point newBottomRight = new Point(dest.getX() + topLeft.diffX(bottomRight)/2, dest.getY() + topLeft.diffY(bottomRight)/2);
        topLeft.moveTo(newBottomRight);
        bottomRight.moveTo(newTopLeft);
    }

    private Point getCenterPoint(){
        double diffX = topLeft.getX() - topLeft.diffX(bottomRight)/2;
        double diffY = topLeft.getY() - topLeft.diffY(bottomRight)/2;

        return new Point(diffX, diffY);
    }

    @Override
    public boolean hasPoint(Point point) {
        return point.getX() > this.getTopLeft().getX() && point.getX() < this.getBottomRight().getX() &&
                point.getY() > this.getTopLeft().getY() && point.getY() < this.getBottomRight().getY();
    }

    @Override
    public void drawFigure(GraphicsContext gc) {
        gc.fillRect(this.getTopLeft().getX(), this.getTopLeft().getY(),
                Math.abs(this.getTopLeft().getX() - this.getBottomRight().getX()), Math.abs(this.getTopLeft().getY() - this.getBottomRight().getY()));

        gc.strokeRect(this.getTopLeft().getX(), this.getTopLeft().getY(),
                Math.abs(this.getTopLeft().getX() - this.getBottomRight().getX()), Math.abs(this.getTopLeft().getY() - this.getBottomRight().getY()));
    }

    public Figure copyFigure(){
       return new Rectangle(getTopLeft(), getBottomRight(), copyFormat());
    }

    @Override
    public void invertX(Point centerPoint){
        invertCoords(new Point(centerPoint.getX(), getCenterPoint().getY()));
    }
    @Override
    public void invertY(Point centerPoint){
        invertCoords(new Point(getCenterPoint().getX(), centerPoint.getY()));
    }

    @Override
    public void invertCoords(Point refPoint){
        Point aux = getCenterPoint();

        aux.invertCoords(refPoint);
        moveTo(aux);
    }
}
