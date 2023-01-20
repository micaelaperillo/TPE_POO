package backend.model;

import javafx.scene.canvas.GraphicsContext;

public class Ellipse extends Figure {

    private final Point centerPoint;
    private final double sMayorAxis, sMinorAxis;

    public Ellipse(Point centerPoint, double sMayorAxis, double sMinorAxis, Format format) {
        super("Elipse", format);
        this.centerPoint = centerPoint;
        this.sMayorAxis = sMayorAxis;
        this.sMinorAxis = sMinorAxis;
    }

    @Override
    public String toString() {
        return String.format("Elipse [Centro: %s, DMayor: %.2f, DMenor: %.2f]", centerPoint, sMayorAxis, sMinorAxis);
    }

    public Point getCenterPoint() {
        return centerPoint.copyPoint();
    }

    public double getsMayorAxis() {
        return sMayorAxis;
    }

    public double getsMinorAxis() {
        return sMinorAxis;
    }

    @Override
    public boolean hasPoint(Point point) {
        return ((Math.pow(point.getX() - this.getCenterPoint().getX(), 2) / Math.pow(this.getsMayorAxis(), 2)) +
                (Math.pow(point.getY() - this.getCenterPoint().getY(), 2) / Math.pow(this.getsMinorAxis(), 2))) <= 0.30;
    }

    @Override
    public void move(double diffX, double diffY) {
        this.centerPoint.move(diffX, diffY);
    }

    @Override
    public void moveTo(Point dest) {
        this.centerPoint.moveTo(dest);
    }

    @Override
    public void drawFigure(GraphicsContext gc) {
        gc.strokeOval(this.getCenterPoint().getX() - (this.getsMayorAxis() / 2), this.getCenterPoint().getY() - (this.getsMinorAxis() / 2), this.getsMayorAxis(), this.getsMinorAxis());
        gc.fillOval(this.getCenterPoint().getX() - (this.getsMayorAxis() / 2), this.getCenterPoint().getY() - (this.getsMinorAxis() / 2), this.getsMayorAxis(), this.getsMinorAxis());
    }

    public Figure copyFigure(){
        return new Ellipse(getCenterPoint(), sMayorAxis, sMinorAxis, copyFormat());
    }

    //Inversion
    @Override
    public void invertX(Point ref){
        centerPoint.invertX(ref);
    }
    @Override
    public void invertY(Point ref){
        centerPoint.invertY(ref);
    }

    @Override
    public void invertCoords(Point ref){
        centerPoint.invertCoords(ref);
    }
    // ----------------------------------------------
}
