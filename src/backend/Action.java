package backend;

import backend.model.Figure;

import java.util.List;

public class Action {
    //Se utiliza en CanvasState dentro de memory<Action>
    //Permite guardar los snapshots del CanvasState para deshacer o rehacer
    private final String description;
    private final List<Figure> snapshot;

    public Action(String description, List<Figure> snapshot) {
        this.description = description;
        this.snapshot = snapshot;
    }

    public List<Figure> getSnapshot() {
        return snapshot;
    }

    @Override
    public String toString() {
        return description;
    }
}
