package backend.model;

public class Circle extends Ellipse {

    public Circle(Point centerPoint, double radius, Format format) {
        super(centerPoint, radius, radius,format);
        setName("Circulo");
    }

    public double getRadius() {
        return getsMayorAxis();
    }

    //Revisa que tenga el punto (Se usa en isSelected)
    @Override
    public boolean hasPoint(Point point) {
        return Math.sqrt(Math.pow(this.getCenterPoint().getX() - point.getX(), 2) +
                Math.pow(this.getCenterPoint().getY() - point.getY(), 2)) <= this.getRadius()/2;
    }

    @Override
    public String toString() {
        return String.format("Círculo [Centro: %s, Radio: %.2f]", getCenterPoint(), getRadius());
    }

    //Como dice el nombre, se usa para copiar figuras (Seccion copyPaste)
    @Override
    public Figure copyFigure() {
        return new Circle(getCenterPoint(), getRadius(), copyFormat());
    }
}
