package backend.model;

public class Square extends Rectangle {

    public Square(Point topLeft, double size, Format format) {
        super(topLeft, new Point(topLeft.getX() + size, topLeft.getY() + size), format);
        setName("Cuadrado");
    }

    public Square(Point topLeft, Point bottomRight, Format format){
        super(topLeft, bottomRight, format);
    }

    @Override
    public Figure copyFigure() {
        return new Square(getTopLeft(), getBottomRight(), copyFormat());
    }
}
