package backend;

import backend.model.Figure;

import java.util.*;

public class CanvasState {
    private List<Figure> list = new ArrayList<>();

    private final Memory<Action> memory = new Memory<>();

    public void addFigure(Figure figure) {
        list.add(figure);
    }

    public void deleteFigure(Figure figure) {
        list.remove(figure);
    }

    public Iterable<Figure> figures() {
        return list;
    }

    //Guarda el snapshot en el memory
    public void performAction(String action){
        ArrayList<Figure> aux = new ArrayList<>();
        for(Figure figure : list)
            aux.add(figure.copyFigure());

        memory.performAction(new Action(action, aux));
    }

    //el undo en el canvas
    public void undoAction() {
        try {
            memory.undo();
            list = getCopyOfCurrent();
        } catch (Exception ex){
            list = new ArrayList<>();
        }
    }

    //Redo en el canvas
    public void redoAction(){
       memory.redo();
       list = getCopyOfCurrent();
    }

    public String getRedoString(){
        return String.format("%d %s",memory.getRedoSize(),memory.getFirstRedo() != null ? memory.getFirstRedo() : "");
    }

    public String getUndoString(){
        return String.format("%s %d", memory.getCurrentState() != null ? memory.getCurrentState() : "", memory.getUndoSize());
    }

    //Obtiene una copia del estado actual
    private List<Figure> getCopyOfCurrent(){
        List<Figure> aux = new ArrayList<>();
        for(Figure figure : memory.getCurrentState().getSnapshot())
            aux.add(figure.copyFigure());
        return aux;
    }
}
