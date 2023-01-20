package backend.model;

import javafx.scene.canvas.GraphicsContext;

public abstract class Figure implements Movable {
    private Format format;
    private String name;

    public Figure(String name, Format format){
        setFormat(format);
        this.name = name;
    }

    public abstract boolean hasPoint(Point point);
    public abstract void drawFigure(GraphicsContext gc);

    //Para el invertion --------------------- //
    public abstract void invertX(Point ref);
    public abstract void invertY(Point ref);

    // ----------------------------------//

    //Seccion formatos---------------------//
    public void setFormat(Format format){
        this.format = format;
    }

    public Format copyFormat(){
        return new Format(format);
    }

    //--------------------------------------//

    //Metodo para copiar figuras (se usa en Copy/Paste/Cut)
    public abstract Figure copyFigure();

    //Geter y Seter del nombre
    protected void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
