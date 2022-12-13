import javafx.application.Application;
import javafx.scene.chart.*;
import java.util.*;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.input.*;

public class GraphingCalculator extends Application {
	public static void main (String[] args) {
		launch(args);
	}

	protected static final int WINDOW_WIDTH = 600, WINDOW_HEIGHT = 500;
	protected static final double MIN_X = -10, MAX_X = +10, DELTA_X = 0.01;
	protected static final double MIN_Y = -10, MAX_Y = +10;
	protected static final double GRID_INTERVAL = 5;
	protected static final String EXAMPLE_EXPRESSION = "2*x+5*x*x";
	protected final ExpressionParser expressionParser = new SimpleExpressionParser();

	private void graph (LineChart<Number, Number> chart, Expression expression, boolean clear) {
		final XYChart.Series series = new XYChart.Series();
		for (double x = MIN_X; x <= MAX_X; x += DELTA_X) {
			final double y = expression.evaluate(x);
			series.getData().add(new XYChart.Data(x, y));
		}
		if (clear) {
			chart.getData().clear();
		}
		chart.getData().addAll(series);
	}

	@Override
	public void start (Stage primaryStage) {
		primaryStage.setTitle("Graphing Calculator");

		final Pane queryPane = new HBox();
		final Label label = new Label("y=");
		final TextField textField = new TextField(EXAMPLE_EXPRESSION);
		final Button graphButton = new Button("Graph");
		final CheckBox diffBox = new CheckBox("Show Derivative");
		final CheckBox autoScaleBox = new CheckBox("Auto Scale");
		queryPane.getChildren().add(label);
		queryPane.getChildren().add(textField);

		final Pane graphPane = new Pane();
		final LineChart<Number, Number> chart;
//		chart = new LineChart<Number, Number>(new NumberAxis(), new NumberAxis());
		chart = new LineChart<Number, Number>(new NumberAxis(MIN_X, MAX_X, GRID_INTERVAL), new NumberAxis(MIN_Y, MAX_Y, GRID_INTERVAL));
		chart.setLegendVisible(false);
		chart.setCreateSymbols(false);
		chart.setTranslateX(0);
		chart.setTranslateY(0);
		graphPane.getChildren().add(chart);
		autoScaleBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle (MouseEvent event) {
				if (autoScaleBox.isSelected()) {
					chart.getXAxis().setAutoRanging(true);
					chart.getYAxis().setAutoRanging(true);
				} else {
					chart.getXAxis().setAutoRanging(false);
					chart.getYAxis().setAutoRanging(false);
				}
			}
		});
		graphButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle (MouseEvent e) {
				try {
					final Expression expression = expressionParser.parse(textField.getText());
					graph(chart, expression, true);
					System.out.println(expression.convertToString(0));
					if (diffBox.isSelected()) {
						final Expression derivative = expression.differentiate();
						graph(chart, derivative, false);
					}
					if (autoScaleBox.isSelected()) {
						chart.getXAxis().setAutoRanging(true);
						chart.getYAxis().setAutoRanging(true);
					} else {
						chart.getXAxis().setAutoRanging(false);
						chart.getYAxis().setAutoRanging(false);
					}
				} catch (ExpressionParseException epe) {
					textField.setStyle("-fx-text-fill: red");
				} catch (UnsupportedOperationException epe) {
					textField.setStyle("-fx-text-fill: red");
				}
			}
		});
		queryPane.getChildren().add(graphButton);
		queryPane.getChildren().add(diffBox);
		queryPane.getChildren().add(autoScaleBox);

		////////////////////////////////////
		final double SCALE_DELTA = 1.01;
		chart.setOnScroll(new EventHandler<ScrollEvent>() {
			public void handle(ScrollEvent event) {
				event.consume();

				if (event.getDeltaY() == 0) {
					return;
				}

				double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;

//				chart.setScaleX(chart.getScaleX() * scaleFactor);
//				chart.setScaleY(chart.getScaleY() * scaleFactor);
//				((NumberAxis) chart.getXAxis()).setScaleX(((NumberAxis) chart.getXAxis()).getScale() * scaleFactor);
//				((NumberAxis) chart.getYAxis()).setScaleY(((NumberAxis) chart.getYAxis()).getScale() * scaleFactor);
				((NumberAxis) chart.getXAxis()).setLowerBound(((NumberAxis) chart.getXAxis()).getLowerBound() * scaleFactor);
				((NumberAxis) chart.getXAxis()).setUpperBound(((NumberAxis) chart.getXAxis()).getUpperBound() * scaleFactor);
				((NumberAxis) chart.getYAxis()).setLowerBound(((NumberAxis) chart.getXAxis()).getLowerBound() * scaleFactor);
				((NumberAxis) chart.getYAxis()).setUpperBound(((NumberAxis) chart.getXAxis()).getUpperBound() * scaleFactor);
			}
		});

		chart.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2) {
					chart.setScaleX(1.0);
					chart.setScaleY(1.0);
				}
			}
		});

		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//final Pane graphPane = new Pane();
		//chart.setCreateSymbols(false);
		//graphPane.getChildren().add(chart);
//		graphPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
//			@Override
//			public void handle(MouseEvent event) {
//				if (event.getClickCount() == 2) {
//					final double x = (double) chart.getXAxis().getValueForDisplay(event.getX());
//					final double y = (double) chart.getYAxis().getValueForDisplay(event.getY());
//					final double xMin = ((NumberAxis) chart.getXAxis()).getLowerBound();
//					final double xMax = ((NumberAxis) chart.getXAxis()).getUpperBound();
//					final double yMin = ((NumberAxis) chart.getYAxis()).getLowerBound();
//					final double yMax = ((NumberAxis) chart.getYAxis()).getUpperBound();
//					final double xRange = xMax - xMin;
//					final double yRange = yMax - yMin;
//					((NumberAxis) chart.getXAxis()).setLowerBound(x - xRange / 4);
//					((NumberAxis) chart.getXAxis()).setUpperBound(x + xRange / 4);
//					((NumberAxis) chart.getYAxis()).setLowerBound(y - yRange / 4);
//					((NumberAxis) chart.getYAxis()).setUpperBound(y + yRange / 4);
//				}
//			}
//		});

		graphPane.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 1 && event.isPrimaryButtonDown()) {

//					final double x = (double) chart.getXAxis().getValueForDisplay(event.getX());
//					final double y = (double) chart.getYAxis().getValueForDisplay(event.getY());
//					final double xMin = ((NumberAxis) chart.getXAxis()).getLowerBound();
//					final double xMax = ((NumberAxis) chart.getXAxis()).getUpperBound();
//					final double yMin = ((NumberAxis) chart.getYAxis()).getLowerBound();
//					final double yMax = ((NumberAxis) chart.getYAxis()).getUpperBound();
//					final double xRange = xMax - xMin;
//					final double yRange = yMax - yMin;
//					double i = 2;
//					((NumberAxis) chart.getXAxis()).setLowerBound(x - (xRange/i));
//					((NumberAxis) chart.getXAxis()).setUpperBound(x + (xRange/i));
//					((NumberAxis) chart.getYAxis()).setLowerBound(y - (yRange/i));
//					((NumberAxis) chart.getYAxis()).setUpperBound(y + (yRange/i));
//					double sceneX = event.getSceneX();
//					double mouseX = event.getX();
//					double valueForDisplayX = (double) chart.getXAxis().getValueForDisplay(mouseX);
//					double deltaX = sceneX - mouseX;
//					double sceneY = event.getSceneY();
//					double mouseY = event.getY();
//					double valueForDisplayY = (double) chart.getYAxis().getValueForDisplay(mouseY);
//					double deltaY = sceneY - mouseY;
//
//					// update the chart's position
//					chart.setTranslateX(chart.getTranslateX() + valueForDisplayX);
//					chart.setTranslateY(chart.getTranslateY() + valueForDisplayY);

				}
			}
		});

		graphPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isSecondaryButtonDown()) {
					final double x = (double) chart.getXAxis().getValueForDisplay(event.getX());
					final double y = (double) chart.getYAxis().getValueForDisplay(event.getY());
					final double xMin = ((NumberAxis) chart.getXAxis()).getLowerBound();
					final double xMax = ((NumberAxis) chart.getXAxis()).getUpperBound();
					final double yMin = ((NumberAxis) chart.getYAxis()).getLowerBound();
					final double yMax = ((NumberAxis) chart.getYAxis()).getUpperBound();
					final double xRange = xMax - xMin;
					final double yRange = yMax - yMin;
//					((NumberAxis) chart.getXAxis()).setLowerBound(x - xRange);
//					((NumberAxis) chart.getXAxis()).setUpperBound(x + xRange);
//					((NumberAxis) chart.getYAxis()).setLowerBound(y - yRange);
//					((NumberAxis) chart.getYAxis()).setUpperBound(y + yRange);
//					chart.setScaleX(2);
					// calculate the distance that the mouse has been dragged
//					double deltaX = event.getSceneX() - event.getX();
//					double deltaY = event.getSceneY() - event.getY();
//
//					// update the chart's position
//					chart.setTranslateX(chart.getTranslateX() + deltaX);
//					chart.setTranslateY(chart.getTranslateY() + deltaY);
				}
			}
		});

		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		textField.setOnKeyPressed(e -> textField.setStyle("-fx-text-fill: black"));

		final BorderPane root = new BorderPane();
		root.setTop(queryPane);
		root.setCenter(graphPane);
		final Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
