package backend.model;

import javafx.scene.paint.Color;

public class Format {
    private final Color fillColor, borderColor;
    private final double borderHeight;

    public Format(Color fillColor, Color borderColor, double borderHeight){
        this.fillColor = fillColor;
        this.borderColor = borderColor;
        this.borderHeight = borderHeight;
    }

    public Format(Format format){
        this(format.fillColor, format.borderColor, format.borderHeight);
    }

    public Color getFillColor() {
        return fillColor;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public double getBorderHeight() {
        return borderHeight;
    }

}
