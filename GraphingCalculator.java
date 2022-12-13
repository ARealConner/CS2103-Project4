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
		chart = new LineChart<Number, Number>(new NumberAxis(MIN_X, MAX_X, GRID_INTERVAL), new NumberAxis(MIN_Y, MAX_Y, GRID_INTERVAL));
		chart.setLegendVisible(false);
		chart.setCreateSymbols(false);
		chart.setTranslateX(0);
		chart.setTranslateY(0);
		graphPane.getChildren().add(chart);

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

		////////////////////////////////////
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
		queryPane.getChildren().add(autoScaleBox);

		/*
		 * Allows the graph to zoom in and out when the mouse is scrolled
		 */
		final double SCALE_DELTA = 1.05;
		chart.setOnScroll(new EventHandler<ScrollEvent>() {
			public void handle(ScrollEvent event) {
				event.consume();
				if (event.getDeltaY() == 0) {
					return;
				}
				double scaleFactor;
				if  (event.getDeltaY() > 0) {
					scaleFactor = SCALE_DELTA;
				} else {
					scaleFactor = 1 / SCALE_DELTA;
				}
				((NumberAxis) chart.getXAxis()).setLowerBound(((NumberAxis) chart.getXAxis()).getLowerBound() * scaleFactor);
				((NumberAxis) chart.getXAxis()).setUpperBound(((NumberAxis) chart.getXAxis()).getUpperBound() * scaleFactor);
				((NumberAxis) chart.getYAxis()).setLowerBound(((NumberAxis) chart.getXAxis()).getLowerBound() * scaleFactor);
				((NumberAxis) chart.getYAxis()).setUpperBound(((NumberAxis) chart.getXAxis()).getUpperBound() * scaleFactor);
			}
		});

		/*
		 * When the right mouse button is pressed, the graph will recenter back to the default location
		 */
		chart.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if (event.isSecondaryButtonDown()) {
					((NumberAxis) chart.getXAxis()).setLowerBound(MIN_X);
					((NumberAxis) chart.getXAxis()).setUpperBound(MAX_X);
					((NumberAxis) chart.getYAxis()).setLowerBound(MIN_Y);
					((NumberAxis) chart.getYAxis()).setUpperBound(MAX_Y);
				}
			}
		});

		/*
		 * When the left mouse button is clicked, have the graph center around where the mouse.
		 */
		graphPane.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 1 && event.isPrimaryButtonDown()) {
					Point2D point = new Point2D(event.getX(), event.getY());
					double x = chart.getXAxis().getValueForDisplay(point.getX()).doubleValue();
					double y = chart.getYAxis().getValueForDisplay(point.getY()).doubleValue();
					((NumberAxis) chart.getXAxis()).setLowerBound(x - GRID_INTERVAL);
					((NumberAxis) chart.getXAxis()).setUpperBound(x + GRID_INTERVAL);
					((NumberAxis) chart.getYAxis()).setLowerBound(y - GRID_INTERVAL);
					((NumberAxis) chart.getYAxis()).setUpperBound(y + GRID_INTERVAL);
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