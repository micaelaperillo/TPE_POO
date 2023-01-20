package frontend;

//Models
import backend.CanvasState;
import backend.model.*;
import backend.model.Point;
import backend.model.Rectangle;

//Javafx
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

//Exceptions
import java.util.NoSuchElementException;

public class PaintPane extends BorderPane {

	// BackEnd
	private final CanvasState canvasState;

	// Canvas y relacionados
	private final Canvas canvas = new Canvas(800, 600);

	private final Point centerCanvas = new Point( canvas.getWidth() / 2, canvas.getHeight() / 2);
	private final GraphicsContext gc = canvas.getGraphicsContext2D();
	private final Format defaultformat = new Format(Color.YELLOW, Color.BLACK, 1);
	private Format clipboardFormat;

	// Botones Barra Izquierda
	private final ToggleButton selectionButton = new ToggleButton("Seleccionar");
	private final ToggleButton rectangleButton = new ToggleButton("Rectángulo");
	private final ToggleButton circleButton = new ToggleButton("Círculo");
	private final ToggleButton squareButton = new ToggleButton("Cuadrado");
	private final ToggleButton ellipseButton = new ToggleButton("Elipse");
	private final ToggleButton deleteButton = new ToggleButton("Borrar");
	private final ToggleButton copyFormat = new ToggleButton("Cop. Form.");


	// Borde

	// Dibujar una figura
	private Point startPoint;

	// Seleccionar una figura
	private Figure selectedFigure;

	// Copia o Corta la figura en el portapapeles
	private Figure clipBoardFigure;

	// UndoRedo pane
	private final UndoRedoPane undoRedoPane;

	public PaintPane(CanvasState canvasState, StatusPane statusPane) {
		this.canvasState = canvasState;
		// StatusBar
		this.undoRedoPane = new UndoRedoPane();
		LeftPane leftPane = new LeftPane();

		//CopyPasteCut
		CopyPasteCutPane cop = new CopyPasteCutPane();
		BorderPane topPane = new BorderPane();
		topPane.setTop(cop);
		//
		//
		this.setOnKeyPressed(keyEvent -> {
			if (keyEvent.isControlDown()) {
				if (keyEvent.getCode().equals(KeyCode.V))
					cop.paste();
				if (keyEvent.getCode().equals(KeyCode.C))
					cop.copy();
				if (keyEvent.getCode().equals(KeyCode.X))
					cop.cut();
			}
		});
		///

		gc.setLineWidth(1);

		//Consigue un punto inicial para crear las figuras
		canvas.setOnMousePressed(event -> startPoint = new Point(event.getX(), event.getY()));

		canvas.setOnMouseReleased(event -> {
			Point endPoint = new Point(event.getX(), event.getY());

			if (startPoint == null || endPoint.getX() < startPoint.getX() || endPoint.getY() < startPoint.getY()) {
				return ;
			}


			Figure fig;

			if(rectangleButton.isSelected()) {

				fig = new Rectangle(startPoint, endPoint, leftPane.getCanvasFormat());

			} else if(circleButton.isSelected()) {

				double circleRadius = Math.abs(endPoint.getX() - startPoint.getX());
				fig = new Circle(startPoint, circleRadius, leftPane.getCanvasFormat());

			} else if (squareButton.isSelected()) {

				double size = Math.abs(endPoint.getX() - startPoint.getX());
				fig = new Square(startPoint, size, leftPane.getCanvasFormat());

			} else if (ellipseButton.isSelected()) {

				Point centerPoint = new Point(Math.abs(endPoint.getX() + startPoint.getX()) / 2, (Math.abs((endPoint.getY() + startPoint.getY())) / 2));
				double sMayorAxis = Math.abs(endPoint.getX() - startPoint.getX());
				double sMinorAxis = Math.abs(endPoint.getY() - startPoint.getY());
				fig = new Ellipse(centerPoint, sMayorAxis, sMinorAxis, leftPane.getCanvasFormat());

			} else {
				return;
			}

			canvasState.addFigure(fig);
			canvasState.performAction(String.format("Se creo un %s", fig.getName()));
			undoRedoPane.refreshUndoRedo();

			startPoint = null;
			redrawCanvas();
		});

		canvas.setOnMouseMoved(event -> {
			Point eventPoint = new Point(event.getX(), event.getY());

			Figure figure = getFigureOnPoint(eventPoint);
			if(figure == null){
				statusPane.updateStatus(eventPoint.toString());
				return;
			}
			statusPane.updateStatus(figure.toString());

		});



		canvas.setOnMouseClicked(event -> {

			Point eventPoint = new Point(event.getX(), event.getY());
			if(selectionButton.isSelected()) {
				if((selectedFigure = getFigureOnPoint(eventPoint)) != null){
					statusPane.updateStatus(String.format("Se seleccionó: %s", selectedFigure));

					leftPane.setFormat(selectedFigure.copyFormat());
					redrawCanvas();
					return;
				}
				statusPane.updateStatus("Ninguna figura encontrada");
			}

			// copy format
			if(copyFormat.isSelected()) {
				clipboardFormat = selectedFigure.copyFormat();
				getFigureOnPoint(eventPoint).setFormat(clipboardFormat);

				canvasState.performAction("Se copió el formato");
				copyFormat.setSelected(false);

				undoRedoPane.refreshUndoRedo();
				redrawCanvas();
			}
		});

		canvas.setOnMouseDragged(event -> {
			if(selectionButton.isSelected()) {
				Point eventPoint = new Point(event.getX(), event.getY());

				if(selectedFigure != null && selectedFigure.hasPoint(eventPoint))
					selectedFigure.moveTo(eventPoint);

				redrawCanvas();
			}
		});


		topPane.setBottom(undoRedoPane);

		setTop(topPane);
		setLeft(leftPane);
		setRight(canvas);
		redrawCanvas();
	}

	//Se utiliza para actualizar el canvas a medida que se hacen cambios
	private void redrawCanvas() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		for(Figure figure : canvasState.figures()) {
			if(figure == selectedFigure) {
				gc.setStroke(Color.RED);
			} else {
				gc.setStroke(figure.copyFormat().getBorderColor());
			}
			gc.setFill(figure.copyFormat().getFillColor());
			gc.setLineWidth(figure.copyFormat().getBorderHeight());

			figure.drawFigure(gc);
		}
	}

	private static ImageView getIcon(String path){
		ImageView icon = new ImageView(new Image(path));
		icon.setFitHeight(25);
		icon.setPreserveRatio(true);
		return icon;
	}

	private Figure getFigureOnPoint(Point point){
		Figure toReturn = null;
		for(Figure figure : canvasState.figures()){
			if(figure.hasPoint(point)){
				toReturn = figure;
			}
		}
		return toReturn;
	}

	//Modularizacion del LeftPane
	private class LeftPane extends VBox {
		private final Slider borderSlider = new Slider(1, 50, 1);
		private final ColorPicker borderColorPicker = new ColorPicker(defaultformat.getBorderColor());

		private final ColorPicker fillColorPicker = new ColorPicker(defaultformat.getFillColor());

		//Inversion de ejes
		private final ToggleButton invertXAxis = new ToggleButton("Invertir X");
		private final ToggleButton invertYAxis = new ToggleButton("Invertir Y");

		public LeftPane(){
			super(10);
			borderSlider.setShowTickMarks(true);
			borderSlider.setShowTickLabels(true);
			borderSlider.setMajorTickUnit(25);
			borderSlider.setBlockIncrement(5);

			ToggleButton[] toolsArr = {selectionButton, rectangleButton, circleButton, squareButton,
					ellipseButton, deleteButton, copyFormat, invertXAxis, invertYAxis};

			ToggleGroup group = new ToggleGroup();

			for(ToggleButton tool: toolsArr){
				tool.setMinWidth(90);
				tool.setToggleGroup(group);
				tool.setCursor(Cursor.HAND);
			}

			getChildren().addAll(toolsArr);
			setPadding(new Insets(5));
			setStyle("-fx-background-color: #999");

			Text borderTitle = new Text("Borde");
			getChildren().add(borderTitle);
			getChildren().add(borderColorPicker);
			getChildren().add(borderSlider);

			// Relleno
			Text fillTitle = new Text("Relleno");
			getChildren().add(fillTitle);
			getChildren().add(fillColorPicker);

			setPrefWidth(100);

			setActionButtons();
		}

		private void setActionButtons(){
			deleteButton.setOnAction(event -> {
				if (selectedFigure != null) {
					canvasState.performAction(String.format("Se eliminó un %s", selectedFigure.getName()));
					canvasState.deleteFigure(selectedFigure);
					undoRedoPane.refreshUndoRedo();
					selectedFigure = null;
					redrawCanvas();
				}
			});

			invertXAxis.setOnAction(event -> {
				if(selectedFigure != null){
					canvasState.performAction(String.format("Se invirtió en X un %s", selectedFigure.getName()));
					selectedFigure.invertX(centerCanvas);
					undoRedoPane.refreshUndoRedo();
					redrawCanvas();
				}
			});

			invertYAxis.setOnAction(event -> {
				if(selectedFigure != null){
					canvasState.performAction(String.format("Se invirtió en Y un %s", selectedFigure.getName()));
					selectedFigure.invertY(centerCanvas);
					undoRedoPane.refreshUndoRedo();
					redrawCanvas();
				}
			});

			borderColorPicker.setOnAction(event1 ->  formatButton("Se cambió el color del borde"));
			fillColorPicker.setOnAction(event2 -> formatButton("Se cambió color del interior"));
			borderSlider.setOnMouseReleased(event3 -> formatButton("Se cambio el grosor del borde"));
		}

		private void formatButton(String action){
			if( selectedFigure != null ){
				selectedFigure.setFormat(getCanvasFormat());

				canvasState.performAction(action + " de un " + selectedFigure.getName());
				undoRedoPane.refreshUndoRedo();
			}
			redrawCanvas();
		}

		private Format getCanvasFormat(){
			return new Format(fillColorPicker.getValue(), borderColorPicker.getValue(),borderSlider.getValue());
		}

		private void setFormat(Format format){
			fillColorPicker.setValue(format.getFillColor());
			borderColorPicker.setValue(format.getBorderColor());
			borderSlider.setValue(format.getBorderHeight());
		}
	}

	//Modularizacion UndoRedo
	private class UndoRedoPane extends HBox {
		// Texto descriptivo del undo/redo
		private final Text undoInf = new Text();
		private final Text redoInf = new Text();
		//botones para deshacer y rehacer e informacion
		private final ToggleButton undoButton = new ToggleButton("Deshacer", getIcon("frontend/resources/undo_icon.png"));
		private final ToggleButton redoButton = new ToggleButton("Rehacer", getIcon("frontend/resources/redo_icon.png"));

		public UndoRedoPane(){
			super(10);

			//Para que los botones no se muevan cuando imprime la accion//
			TextFlow und = new TextFlow(undoInf);
			TextFlow red = new TextFlow(redoInf);

			setTextFlowWidth(und);
			setTextFlowWidth(red);

			und.setTextAlignment(TextAlignment.RIGHT);
			red.setTextAlignment(TextAlignment.LEFT);

			setTextFlowTranslateY(und);
			setTextFlowTranslateY(red);
			//////------------------------//////

			ToggleButton[] undoRedo = { undoButton, redoButton };

			getChildren().add(und);
			getChildren().addAll(undoRedo);
			getChildren().add(red);
			setStyle("-fx-alignment: center; -fx-background-color: #999");

			refreshUndoRedo();
			setActionButtons();
		}

		private void setTextFlowWidth(TextFlow text){
			text.setMinWidth(270);
		}

		private void setTextFlowTranslateY(TextFlow txt){
			txt.setTranslateY(7);
		}
		private void setActionButtons(){
			undoButton.setOnAction(e -> {
				canvasState.undoAction();
				refreshUndoRedo();
				redrawCanvas();
			});
			redoButton.setOnAction(e -> {
				try {
					canvasState.redoAction();
					refreshUndoRedo();
					redrawCanvas();
				} catch(NoSuchElementException ex){  //Si no hay que rehacer, tira una excepcion (NoSuchElement)
					Alert al = new Alert(Alert.AlertType.ERROR);
					al.setTitle("Redo Error");
					al.setHeaderText("Nothing to Redo.");
					al.showAndWait();
				}
			});
		}

		public void refreshUndoRedo(){ //Se actualiza que se deshizo/hizo
			undoInf.setText(canvasState.getUndoString());
			redoInf.setText(canvasState.getRedoString());
		}
	}
	//Modularizacion de copy/paste/cut
	private class CopyPasteCutPane extends HBox{
		//Botones de Copy paste cut
		private final ToggleButton cutButton = new ToggleButton("Cortar", getIcon("frontend/resources/cut_icon.png"));
		private final ToggleButton copyButton = new ToggleButton("Copiar", getIcon("frontend/resources/copy_icon.png"));
		private final ToggleButton pasteButton = new ToggleButton("Pegar", getIcon("frontend/resources/paste_icon.png"));

		public CopyPasteCutPane(){
			super(10);

			ToggleButton[] actionArr = { cutButton, copyButton, pasteButton };
			getChildren().addAll(actionArr);
			setStyle("-fx-alignment: center-left; -fx-background-color: #999");

			setOnActionButtons();
		}

		private void copyButton(String action){
			if (selectedFigure != null) {
				canvasState.performAction(action + " " + selectedFigure.getName());
				undoRedoPane.refreshUndoRedo();
			}
			clipBoardFigure = selectedFigure;
			redrawCanvas();
		}

		private void setOnActionButtons() {
			cutButton.setOnAction(event -> {
				canvasState.deleteFigure(selectedFigure);
				copyButton("Se cortó un ");
				selectedFigure = null;
			});

			copyButton.setOnAction(event -> copyButton("Se copió un "));

			pasteButton.setOnAction(event -> {
				if (clipBoardFigure != null) {
					Figure figure = clipBoardFigure.copyFigure();
					figure.moveTo(centerCanvas);
					canvasState.addFigure(figure);
					canvasState.performAction(String.format("Se pegó un %s", figure.getName()));
					undoRedoPane.refreshUndoRedo();
					redrawCanvas();
				}
			});
		}

		//Se usan exclusivamente para setear ctrl v /c / x
		private void paste(){
			pasteButton.fire();
		}

		private void cut(){
			cutButton.fire();
		}

		private void copy(){
			copyButton.fire();
		}
	}
}
