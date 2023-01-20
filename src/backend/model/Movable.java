package backend.model;

public interface Movable {
        //Move y moveTo comunmente utilizadas para invertX/Y
        void move(double diffX, double diffY);
        void moveTo(Point dest);

        //Los puntos deben saber invertirse con el centro de referencia
        void invertX(Point ref);
        void invertY(Point ref);

        //Metodo generico para invertir
        void invertCoords(Point ref);
}
