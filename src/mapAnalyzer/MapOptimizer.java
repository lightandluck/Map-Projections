package mapAnalyzer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;
import mapAnalyzer.MapAnalyzer.Projection;
import util.Stat;

/**
 * An application to compare and optimize map projections
 * 
 * @author Justin Kunimune
 */
public class MapOptimizer extends Application {
	
	
	private static final String[] EXISTING_PROJECTIONS = { "Hobo-Dyer", "Winkel Tripel", "Robinson", "Van der Grinten",
			"Mercator" };
	private ScatterChart<Number, Number> chart;
	
	
	
	public static final void main(String[] args) {
		launch(args);
	}
	
	
	@Override
	public void start(Stage stage) throws Exception {
		double[][][] globe = MapAnalyzer.globe(0.02);
		final Series<Number, Number> oldMaps = analyzeAll(globe, EXISTING_PROJECTIONS);
		final Series<Number, Number> hyperMaps = optimizeHyperelliptical(globe);
		final Series<Number, Number> roundMaps = optimizeElliptihypercosine(globe);
		
		chart = new ScatterChart<Number, Number>(new NumberAxis("Size distortion", 0, 1, 0.1),
				new NumberAxis("Shape distortion", 0, 0.5, 0.1));
		chart.getData().add(oldMaps);
		chart.getData().add(hyperMaps);
		chart.getData().add(roundMaps);
		
		stage.setTitle("Map Projections");
		stage.setScene(new Scene(chart));
		stage.show();
	}
	
	
	private static Series<Number, Number> analyzeAll(double[][][] points,
			String... projs) { //analyze and plot the specified preexisting map projections
		Series<Number, Number> output = new Series<Number, Number>();
		output.setName("Basic Projections");
		
		for (String name: projs)
			output.getData().add(plot(points, MapAnalyzer.projFromName(name)));
		
		return output;
	}
	
	
	private static Series<Number, Number> optimizeHyperelliptical(double[][][] points) { //optimize and plot some hyperelliptical maps
		System.out.println("Hyperelliptical stuff. Aw yeah.");
		final double[] weights = {.22, .50, .86, 1, 1.3, 2, 3.0, 4.7, 8.0, 18.0 };
		double[][] currentBest = new double[weights.length][5]; //the 0-3 cols are the min distortions for each weight, the other cols are the values of k and n that caused that
		for (int i = 0; i < weights.length; i ++)
			currentBest[i][0] = Integer.MAX_VALUE;
		
		System.out.println("Iterating k and n:");
		for (double k = 2; k <= 5; k += .25) {
			for (double n = 1; n <= 2.75; n += .125) {
				System.out.println("("+k+","+n+")");
				final double k0 = k, n0 = n;
				double[][][] dist = MapAnalyzer.calculateDistortion(points,
						(coords) -> hyperelliptical(coords, k0, n0));
				final double avgSizeD = Stat.stdDev(dist[0]);
				final double avgShapeD = Stat.mean(dist[1]);
				for (int i = 0; i < weights.length; i ++) {
					final double avgDist = avgSizeD + weights[i]*avgShapeD;
					if (avgDist < currentBest[i][0]) {
						currentBest[i][0] = avgDist;
						currentBest[i][1] = avgSizeD;
						currentBest[i][2] = avgShapeD;
						currentBest[i][3] = k;
						currentBest[i][4] = n;
					}
				}
			}
		}
		
		final Series<Number, Number> output = new Series<Number, Number>();
		output.setName("Hyperelliptics");
		
		System.out.println("We got the best hyperelliptic projections using:");
		for (double[] best: currentBest) {
			System.out.println("\tk="+best[3]+"; n="+best[4]);
			output.getData().add(new Data<Number, Number>(best[1], best[2]));
		}
		return output;
	}
	
	
	private static Series<Number, Number> optimizeElliptihypercosine(double[][][] points) { //optimize and plot some elliptical-hypebolic-cosine maps
		return new Series<Number, Number>();
	}
	
	
	private static Data<Number, Number> plot(double[][][] pts, Projection proj) {
		double[][][] distortion = MapAnalyzer.calculateDistortion(pts, proj);
		return new Data<Number, Number>(
				Stat.stdDev(distortion[0]),
				Stat.mean(distortion[1]));
	}
	
	
	private static double[] hyperelliptical(double[] coords, double k, double n) { //a hyperelliptic map projection with hyperellipse order k and lattitudinal spacind described by x^n/n
		final double lat = coords[0], lon = coords[1];
		
		return new double[] {
				Math.pow(1 - Math.pow(Math.abs(lat/(Math.PI/2)), k),1/k)*lon,
				(1-Math.pow(1-Math.abs(lat/(Math.PI/2)), n))/Math.sqrt(n)*Math.signum(lat)*Math.PI/2};
	}

}
