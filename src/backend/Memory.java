package backend;

import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class Memory<E> {
    //Clase generica para guardar un historial de acciones para rehacer/deshacer
    //Dos pilas, una para redo y otra para undo
    Deque<E> toUndoneActions = new LinkedList<>();
    Deque<E> toRedoActions = new LinkedList<>();

    public void undo(){
        switchBetween(toUndoneActions, toRedoActions);
    }

    public void redo(){
        switchBetween(toRedoActions, toUndoneActions);
    }

    //Dependiendo de como se llame, rehace o deshace
    private void switchBetween(Deque<E> d1, Deque<E> d2){
        if(d1.isEmpty())
            throw new NoSuchElementException();

        E action = d1.pop();
        d2.push(action);

    }
    //Cuando se hace una accion, se limpia la pila del redo y se agrega a la pila del undo
    public void performAction(E action){
        toUndoneActions.push(action);
        toRedoActions.clear();
    }

    public E getCurrentState(){
        return toUndoneActions.peek();
    }

    public E getFirstRedo(){
        return toRedoActions.peek();
    }

    public Integer getUndoSize(){
        return toUndoneActions.size();
    }
    public Integer getRedoSize(){
        return toRedoActions.size();
    }

    @Override
    public String toString(){
        return "to undo: " + toUndoneActions.toString() +"\n" + "to redo:" + toRedoActions.toString();
    }
}
