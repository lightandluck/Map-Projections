/**
 * MIT License
 * 
 * Copyright (c) 2017 Justin Kunimune
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package apps;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleUnaryOperator;

import dialogs.ProgressBarDialog;
import dialogs.ProjectionSelectionDialog;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import maps.Azimuthal;
import maps.CahillKeyes;
import maps.Conic;
import maps.Cylindrical;
import maps.Lenticular;
import maps.Misc;
import maps.MyProjections;
import maps.Projection;
import maps.Pseudocylindrical;
import maps.Arbitrary;
import maps.Snyder;
import maps.Polyhedral;
import maps.Tobler;
import maps.Waterman;
import maps.WinkelTripel;
import utils.Flag;
import utils.Math2;
import utils.MutableDouble;
import utils.Procedure;


/**
 * A base class for all GUI applications that deal with maps
 * 
 * @author jkunimune
 */
public abstract class MapApplication extends Application {

	protected static final int GUI_WIDTH = 350;
	protected static final int IMG_WIDTH = 500;
	protected static final int V_SPACE = 5;
	protected static final int H_SPACE = 3;
	protected static final int SPINNER_WIDTH = 92;
	
	private static final KeyCombination CTRL_O = new KeyCodeCombination(KeyCode.O, KeyCodeCombination.CONTROL_DOWN);
	private static final KeyCombination CTRL_S = new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_DOWN);
	private static final KeyCombination CTRL_ENTER = new KeyCodeCombination(KeyCode.ENTER, KeyCodeCombination.CONTROL_DOWN);
	
	
	public static final Projection[] FEATURED_PROJECTIONS = { Polyhedral.LEE_TETRAHEDRAL_RECTANGULAR, Cylindrical.MERCATOR,
			Cylindrical.EQUIRECTANGULAR, Cylindrical.EQUAL_AREA, Cylindrical.GALL_STEREOGRAPHIC,
			Azimuthal.STEREOGRAPHIC, Azimuthal.POLAR, Azimuthal.EQUAL_AREA, Azimuthal.GNOMONIC,
			Azimuthal.PERSPECTIVE, Conic.LAMBERT, Conic.EQUIDISTANT, Conic.ALBERS,
			Polyhedral.LEE_TETRAHEDRAL_RECTANGULAR, Polyhedral.ACTUAUTHAGRAPH,
			Polyhedral.AUTHAPOWER, CahillKeyes.M_MAP, Pseudocylindrical.SINUSOIDAL,
			Pseudocylindrical.MOLLWEIDE, Tobler.TOBLER, Lenticular.AITOFF,
			Lenticular.VAN_DER_GRINTEN, Arbitrary.ROBINSON, WinkelTripel.WINKEL_TRIPEL,
			Misc.PEIRCE_QUINCUNCIAL, Misc.TWO_POINT_EQUIDISTANT, Pseudocylindrical.LEMONS }; //the set of featured projections for the ComboBox
	
	public static final String[] PROJECTION_CATEGORIES = { "Cylindrical", "Azimuthal", "Conic", "Polyhedral",
			"Pseudocylindrical", "Lenticular", "Other", "Invented by Justin" }; //the overarching categories by which I organise my projections
	public static final Projection[][] ALL_PROJECTIONS = {
			{ Cylindrical.EQUAL_AREA, Cylindrical.EQUIRECTANGULAR, Cylindrical.GALL_ORTHOGRAPHIC,
					Cylindrical.GALL_STEREOGRAPHIC, Cylindrical.HOBO_DYER, Cylindrical.LAMBERT,
					Cylindrical.MERCATOR, Cylindrical.MILLER, Cylindrical.PLATE_CARREE },
			{ Azimuthal.EQUAL_AREA, Azimuthal.POLAR, Azimuthal.GNOMONIC, Azimuthal.ORTHOGRAPHIC,
					Azimuthal.PERSPECTIVE, Azimuthal.STEREOGRAPHIC },
			{ Conic.ALBERS, Conic.LAMBERT, Conic.EQUIDISTANT },
			{ Polyhedral.AUTHAGRAPH, CahillKeyes.M_MAP, Polyhedral.LEE_TETRAHEDRAL_RECTANGULAR,
				Polyhedral.LEE_TETRAHEDRAL_TRIANGULAR, Waterman.BUTTERFLY },
			{ Pseudocylindrical.ECKERT_IV, Pseudocylindrical.KAVRAYSKIY_VII,
					Pseudocylindrical.MOLLWEIDE, Arbitrary.NATURAL_EARTH, Arbitrary.ROBINSON,
					Pseudocylindrical.SINUSOIDAL, Tobler.TOBLER },
			{ Lenticular.AITOFF, Lenticular.HAMMER, Lenticular.STREBE_95,
					Lenticular.VAN_DER_GRINTEN, WinkelTripel.WINKEL_TRIPEL },
			{ Snyder.GS50, Misc.GUYOU, Misc.HAMMER_RETROAZIMUTHAL, Pseudocylindrical.LEMONS,
					Misc.PEIRCE_QUINCUNCIAL, Misc.TWO_POINT_EQUIDISTANT, Misc.FLAT_EARTH },
			{ MyProjections.EXPERIMENT, Polyhedral.AUTHAPOWER, Polyhedral.ACTUAUTHAGRAPH,
					MyProjections.MAGNIFIER, MyProjections.PSEUDOSTEREOGRAPHIC,
					Polyhedral.TETRAGRAPH, Polyhedral.TETRAPOWER,
					MyProjections.TWO_POINT_EQUALIZED } }; // every projection I have programmed
	
	private static final String[] ASPECT_NAMES = { "Standard", "Transverse", "Cassini", "Atlantis",
			"Jerusalem", "Point Nemo", "Longest Line", "Cylindrical", "Tetrahedral", "Antipode",
			"Random" };
	private static final double[][] ASPECT_VALS = { //the aspect presets (in degrees)
			{ 90., 0.,  0.,  -4., 31.78, 48.88, -28.52,-35.,    57. },
			{  0., 0., 90.,  65., 35.22, 56.61, 141.45,-13.61,-176. },
			{  0., 0.,-90.,-147.,-35.,  -45.,    30.,  145.,   154. } };
	
	
	final private String name;
	private Stage root;
	private ComboBox<Projection> projectionChooser;
	private GridPane paramGrid;
	private Label[] paramLabels;
	private Slider[] paramSliders;
	private Spinner<Double>[] paramSpinners;
	private double[] currentParams;
	
	private Flag isChanging = new Flag();
	private Flag suppressListeners = new Flag(); //a flag to prevent events from triggering projection setting
	
	
	
	public MapApplication(String name) {
		super();
		this.name = name;
	}
	
	
	@Override
	public void start(Stage root) {
		this.root = root;
		this.root.setTitle(this.name);
		this.root.setScene(new Scene(new StackPane(makeWidgets())));
		this.root.show();
		
		this.suppressListeners.set();
		this.projectionChooser.setValue(FEATURED_PROJECTIONS[0]);;
		this.suppressListeners.clear();
	}
	
	
	protected abstract Node makeWidgets();
	
	
	/**
	 * Create a set of widgets to select an input image
	 * @param allowedExtensions The List of possible file types for the input
	 * @param setInput The method to be called when an input file is loaded.
	 * 		This method will be called from a nongui thread.
	 * @return the full formatted Region
	 */
	protected Region buildInputSelector(
			FileChooser.ExtensionFilter[] allowedExtensions,
			FileChooser.ExtensionFilter defaultExtension,
			Consumer<File> inputSetter) {
		final Label label = new Label("Current input:");
		final Text inputLabel = new Text("None");
		
		final FileChooser inputChooser = new FileChooser(); //TODO: remember last directory
		inputChooser.setInitialDirectory(new File("input"));
		inputChooser.setTitle("Choose an input map");
		inputChooser.getExtensionFilters().addAll(allowedExtensions);
		inputChooser.setSelectedExtensionFilter(defaultExtension);
		
		final Button loadButton = new Button("Choose input...");
		loadButton.setTooltip(new Tooltip(
				"Choose the image to determine the style of your map"));
		loadButton.setOnAction((event) -> {
				File file;
				try {
					file = inputChooser.showOpenDialog(root);
				} catch (IllegalArgumentException e) {
					inputChooser.setInitialDirectory(new File("."));
					file = inputChooser.showOpenDialog(root);
				}
				if (file != null) {
					final File f = file;
					trigger(loadButton, () -> inputSetter.accept(f));
					inputLabel.setText(f.getName());
				}
			});
		loadButton.setTooltip(new Tooltip(
				"Change the input image"));
		root.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {	// ctrl-O opens
				if (CTRL_O.match(event)) {
					loadButton.requestFocus();
					loadButton.fire();
				}
			});
		
		VBox output = new VBox(V_SPACE, new HBox(H_SPACE, label, inputLabel), loadButton);
		output.setAlignment(Pos.CENTER);
		return output;
	}
	
	
	/**
	 * Create a set of widgets to choose a Projection
	 * @param defProj The default projection, before the user chooses anything
	 * @return the full formatted Region
	 */
	protected Region buildProjectionSelector(Procedure projectionSetter) {
		final Label label = new Label("Projection:");
		projectionChooser =
				new ComboBox<Projection>(FXCollections.observableArrayList(FEATURED_PROJECTIONS));
		projectionChooser.getItems().add(Projection.NULL_PROJECTION);
		projectionChooser.setPrefWidth(210);
		
		final Text description = new Text();
		description.setWrappingWidth(GUI_WIDTH);
		
		projectionChooser.valueProperty().addListener((observable, old, now) -> {
			final boolean suppressedListeners = suppressListeners.isSet(); //save this value, because revealParameters()...
				if (projectionChooser.getValue() == Projection.NULL_PROJECTION) {
					chooseProjectionFromExpandedList(old); //<aside>NULL_PROJECTION is the "More..." button. It triggers the expanded list</aside>
				}
				else {
					description.setText(projectionChooser.getValue().getDescription());
					revealParameters(projectionChooser.getValue()); //...clears suppressListeners. That's fine,
					if (!suppressedListeners) //because suppressListeners is only needed here for that one case.
						projectionSetter.execute();
				}
			});
		
		return new VBox(V_SPACE, new HBox(H_SPACE, label, projectionChooser), description);
	}
	
	
	/**
	 * Create a set of widgets to choose an aspect either from a preset or numbers
	 * Also bind aspectArr to the sliders
	 * @return the full formatted Region
	 */
	protected Region buildAspectSelector(double[] aspectArr, Procedure aspectSetter) {
		final MenuButton presetChooser = new MenuButton("Aspect Presets");
		presetChooser.setTooltip(new Tooltip(
				"Set the aspect sliders based on a preset"));
		
		final String[] labels = { "Latitude:", "Longitude:", "Ctr. Meridian:" };
		final Slider[] sliders = new Slider[] {
				new Slider(-90, 90, 0.),
				new Slider(-180, 180, 0.),
				new Slider(-180, 180, 0.) };
		final Spinner<Double> spin0 = new Spinner<Double>(-90, 90, 0.); //yes, this is awkward. Java gets weird about arrays with generic types
		@SuppressWarnings("unchecked")
		final Spinner<Double>[] spinners = (Spinner<Double>[]) Array.newInstance(spin0.getClass(), 3);
		spinners[0] = spin0;
		spinners[1] = new Spinner<Double>(-180, 180, 0.);
		spinners[2] = new Spinner<Double>(-180, 180, 0.);
		
		for (int i = 0; i < 3; i ++) {
			aspectArr[i] = Math.toRadians(sliders[i].getValue());
			link(sliders[i], spinners[i], i, aspectArr, Math::toRadians,
					aspectSetter, isChanging, suppressListeners);
		}
		setAspectByPreset("Standard", sliders, spinners);
		
		for (String preset: ASPECT_NAMES) {
			MenuItem m = new MenuItem(preset);
			m.setOnAction((event) -> {
					setAspectByPreset(((MenuItem) event.getSource()).getText(),
							sliders, spinners);
					for (int i = 0; i < 3; i ++)
						aspectArr[i] = Math.toRadians(sliders[i].getValue());
					if (!suppressListeners.isSet())
						aspectSetter.execute();
				});
			presetChooser.getItems().add(m);
		}
		
		final GridPane grid = new GridPane();
		grid.setVgap(V_SPACE);
		grid.setHgap(H_SPACE);
		grid.getColumnConstraints().addAll(
				new ColumnConstraints(SPINNER_WIDTH,Control.USE_COMPUTED_SIZE,Control.USE_COMPUTED_SIZE),
				new ColumnConstraints(), new ColumnConstraints(SPINNER_WIDTH));
		for (int i = 0; i < 3; i ++) {
			GridPane.setHgrow(sliders[i], Priority.ALWAYS);
			sliders[i].setTooltip(new Tooltip("Change the aspect of the map"));
			spinners[i].setTooltip(new Tooltip("Change the aspect of the map"));
			spinners[i].setEditable(true);
			grid.addRow(i, new Label(labels[i]), sliders[i], spinners[i]);
		}
		
		VBox all = new VBox(V_SPACE, presetChooser, grid);
		all.setAlignment(Pos.CENTER);
		all.managedProperty().bind(all.visibleProperty()); //make it hide when it is hiding
		return all;
	}
	
	
	/**
	 * Create a grid of sliders and spinners not unlike the aspectSelector
	 * @param parameterSetter The function to execute when the parameters change
	 * @return the full formatted Region
	 */
	@SuppressWarnings("unchecked")
	protected Region buildParameterSelector(Procedure parameterSetter) {
		currentParams = new double[4];
		paramLabels = new Label[4];
		paramSliders = new Slider[4]; // I don't think any projection has more than four parameters
		final Spinner<Double> spin0 = new Spinner<Double>(0.,0.,0.); //yes, this is awkward. Java gets weird about arrays with generic types
		paramSpinners = (Spinner<Double>[])Array.newInstance(spin0.getClass(), 4);
		paramSpinners[0] = spin0;
		
		for (int i = 0; i < 4; i ++) {
			paramLabels[i] = new Label();
			paramSliders[i] = new Slider();
			if (i != 0)
				paramSpinners[i] = new Spinner<Double>(0.,0.,0.);
			link(paramSliders[i], paramSpinners[i], i, currentParams, (d)->d, parameterSetter, isChanging, suppressListeners);
		}
		
		for (int i = 0; i < 4; i ++) {
			GridPane.setHgrow(paramSliders[i], Priority.ALWAYS);
			paramSpinners[i].setEditable(true);
		}
		
		paramGrid = new GridPane();
		paramGrid.setVgap(V_SPACE);
		paramGrid.setHgap(H_SPACE);
		paramGrid.getColumnConstraints().addAll(
				new ColumnConstraints(SPINNER_WIDTH,Control.USE_COMPUTED_SIZE,Control.USE_COMPUTED_SIZE),
				new ColumnConstraints(), new ColumnConstraints(SPINNER_WIDTH));
		return paramGrid;
	}
	
	
	/**
	 * Create a block of niche options - specifically International Dateline cropping and whether
	 * there should be a graticule
	 * @param cropAtIDL The mutable boolean value to which to bind the "Crop at Dateline" CheckBox
	 * @param graticule The mutable double value to which to bind the "Graticule" Spinner
	 * @return the full formatted Region
	 */
	protected Region buildOptionPane(Flag cropAtIDL, MutableDouble graticule) {
		final CheckBox cropBox = new CheckBox("Crop at International Dateline"); //the CheckBox for whether there should be shown imagery outside the International Dateline
		cropBox.setSelected(cropAtIDL.isSet());
		cropBox.setTooltip(new Tooltip("Show every point exactly once."));
		cropBox.selectedProperty().addListener((observable, old, now) -> {
				cropAtIDL.set(now);
			});
		
		final ObservableList<Double> factorsOf90 = FXCollections.observableArrayList();
		for (double f = 1; f <= 90; f += 0.5)
			if (90%f == 0)
				factorsOf90.add((double)f);
		final Spinner<Double> gratSpinner = new Spinner<Double>(factorsOf90); //spinner for the graticule value
		gratSpinner.getValueFactory().setConverter(new DoubleStringConverter());
		gratSpinner.getValueFactory().setValue(graticule.get());
		gratSpinner.setDisable(graticule.isZero());
		gratSpinner.setEditable(true);
		gratSpinner.setTooltip(new Tooltip("The spacing in degrees between shown parallels and meridians."));
		gratSpinner.setPrefWidth(SPINNER_WIDTH);
		gratSpinner.valueProperty().addListener((observable, old, now) -> {
				graticule.set(now); //which is tied to the mutable graticule spacing variable
			});
		
		final CheckBox gratBox = new CheckBox("Graticule: "); //the CheckBox for whether there should be a graticule
		gratBox.setSelected(!graticule.isZero());
		gratBox.setTooltip(new Tooltip("Overlay a mesh of parallels and meridians."));
		gratBox.selectedProperty().addListener((observable, old, now) -> {
				if (now)
					graticule.set(gratSpinner.getValue()); //set the value of graticule appropriately when checked
				else
					graticule.set(0); //when not checked, represent "no graticule" as a spacing of 0
				gratSpinner.setDisable(!now); //disable the graticule Spinner when appropriate
			});
		
		final HBox gratRow = new HBox(H_SPACE, gratBox, gratSpinner);
		gratRow.setAlignment(Pos.CENTER_LEFT);
		return new VBox(V_SPACE, cropBox, gratRow);
	}
	
	
	/**
	 * Create a default button that will update the map
	 * @return the button
	 */
	protected Button buildUpdateButton(Runnable mapUpdater) {
		final Button updateButton = new Button("Update map");
		updateButton.setOnAction((event) -> {
				trigger(updateButton, mapUpdater);
			});
		updateButton.setTooltip(new Tooltip(
				"Update the current map with your parameters"));
		
		updateButton.setDefaultButton(true);
		root.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
				if (CTRL_ENTER.match(event)) {
					updateButton.requestFocus();
					updateButton.fire();
				}
			});
		
		return updateButton;
	}
	
	
	/**
	 * Build a button that will save something
	 * @param bindCtrlS Should ctrl+S trigger this button?
	 * @param savee The name of the thing being saved
	 * @param allowedExtensions The allowed file formats that can be saved
	 * @param defaultExtension The default file format to be saved
	 * @param settingCollector A callback to run just before the saving happens that returns true if it should commence
	 * @param calculateAndSaver The callback that saves the thing
	 * @return the button, ready to be pressed
	 */
	protected Button buildSaveButton(boolean bindCtrlS, String savee,
			FileChooser.ExtensionFilter[] allowedExtensions,
			FileChooser.ExtensionFilter defaultExtension,
			BooleanSupplier settingCollector,
			BiConsumer<File, ProgressBarDialog> calculateAndSaver) {
		final FileChooser saver = new FileChooser();
		saver.setInitialDirectory(new File("output"));
		saver.setInitialFileName("my"+savee+defaultExtension.getExtensions().get(0).substring(1));
		saver.setTitle("Save "+savee);
		saver.getExtensionFilters().addAll(allowedExtensions);
		saver.setSelectedExtensionFilter(defaultExtension);
		try {
			if (!saver.getInitialDirectory().exists())
				saver.getInitialDirectory().mkdirs();
		} catch (SecurityException e) {}
		
		final Button saveButton = new Button("Save "+savee+"...");
		saveButton.setOnAction((event) -> {
				File file;
				try {
					file = saver.showSaveDialog(root);
				} catch(IllegalArgumentException e) {
					saver.setInitialDirectory(new File("."));
					file = saver.showSaveDialog(root);
				}
				if (file != null) {
					final File f = file;
					if (settingCollector.getAsBoolean()) {
						final ProgressBarDialog pBar = new ProgressBarDialog();
						pBar.setContentText("Finalizing "+savee+"...");
						pBar.show();
						trigger(saveButton, () -> {
								calculateAndSaver.accept(f, pBar);
								Platform.runLater(pBar::close);
							}); //TODO: I think I might want to automatically open the image
					}
				}
			});
		saveButton.setTooltip(new Tooltip("Save the "+savee+" with current settings"));
		
		if (bindCtrlS) // ctrl+S saves
			root.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
				if (CTRL_S.match(event)) {
					saveButton.requestFocus();
					saveButton.fire();
				}
			});
		
		return saveButton;
	}
	
	
	protected Projection getProjection() {
		return projectionChooser.getValue();
	}
	
	
	protected boolean getParamsChanging() { //are the aspect or parameters actively changing?
		return isChanging.isSet();
	}
	
	
	protected void loadParameters() {
		getProjection().setParameters(currentParams);
	}
	
	
	private void chooseProjectionFromExpandedList(Projection lastProjection) {
		final ProjectionSelectionDialog selectDialog = new ProjectionSelectionDialog();
		
		do {
			final Optional<Projection> result = selectDialog.showAndWait();
			
			if (result.isPresent()) {
				if (result.get() == Projection.NULL_PROJECTION) {
					showError("No projection chosen", "Please select a projection.");
				}
				else {
					Platform.runLater(() -> projectionChooser.setValue(result.get()));
					break;
				}
			}
			else {
				Platform.runLater(() -> projectionChooser.setValue(lastProjection)); //I need these runLater()s because JavaFX is dumb and throws an obscure error on recursive edits to ComboBoxes
				break;
			}
		} while (true);
	}
	
	
	private void setAspectByPreset(String presetName,
			Slider[] sliders, Spinner<Double>[] spinners) {
		this.suppressListeners.set();
		if (presetName.equals("Antipode")) {
			sliders[0].setValue(-sliders[0].getValue());
			sliders[1].setValue((sliders[1].getValue()+360)%360-180);
		}
		else if (presetName.equals("Random")) {
			sliders[0].setValue(Math.toDegrees(Math.asin(Math.random()*2-1)));
			sliders[1].setValue(Math.random()*360-180);
			sliders[2].setValue(Math.random()*360-180);
		}
		else {
			for (int i = 0; i < ASPECT_NAMES.length; i ++) {
				if (ASPECT_NAMES[i].equals(presetName)) {
					for (int j = 0; j < 3; j ++)
						sliders[j].setValue(ASPECT_VALS[j][i]);
					break;
				}
			}
		}
		
		for (int i = 0; i < 3; i ++)
			spinners[i].getEditor().textProperty().set(
					Double.toString(sliders[i].getValue()));
		this.suppressListeners.clear();
	}
	
	
	private void revealParameters(Projection proj) {
		this.suppressListeners.set();
		final String[] paramNames = proj.getParameterNames();
		final double[][] paramValues = proj.getParameterValues();
		paramGrid.getChildren().clear();
		for (int i = 0; i < proj.getNumParameters(); i ++) {
			paramLabels[i].setText(paramNames[i]+":");
			paramSliders[i].setMin(paramValues[i][0]);
			paramSliders[i].setMax(paramValues[i][1]);
			paramSliders[i].setValue(paramValues[i][2]);
			final SpinnerValueFactory.DoubleSpinnerValueFactory paramSpinVF =
					(DoubleSpinnerValueFactory) paramSpinners[i].getValueFactory();
			paramSpinVF.setMin(paramValues[i][0]);
			paramSpinVF.setMax(paramValues[i][1]);
			paramSpinVF.setValue(paramValues[i][2]);
			final Tooltip tt = new Tooltip(
					"Change the "+paramNames[i]+" of the map (default is " + paramValues[i][2]+")");
			paramSliders[i].setTooltip(tt);
			paramSpinners[i].setTooltip(tt);
			paramGrid.addRow(i, paramLabels[i], paramSliders[i], paramSpinners[i]);
		}
		this.suppressListeners.clear();
	}
	
	
	private static void trigger(Button btn, Runnable task) {
		btn.setDisable(true);
		new Thread(() -> {
			try {
				task.run();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				btn.setDisable(false);
			}
		}).start();
	}
	
	
	private static void link(Slider sld, Spinner<Double> spn, int i, double[] doubles,
		DoubleUnaryOperator converter, Procedure callback, Flag isChanging, Flag suppressListeners) {
		sld.valueChangingProperty().addListener((observable, prev, now) -> { //link spinner to slider
				isChanging.set(now);
				if (!now) {
					if (spn.getValue() != sld.getValue())
						spn.getValueFactory().setValue(sld.getValue());
					doubles[i] = converter.applyAsDouble(Math2.round(sld.getValue(),3));
					if (!suppressListeners.isSet())
						callback.execute();
				}
			});
		sld.valueProperty().addListener((observable, prev, now) -> {
				if (spn.getValue() != sld.getValue())
					spn.getValueFactory().setValue(sld.getValue());
				doubles[i] = converter.applyAsDouble(Math2.round(now.doubleValue(),3));
				if (!suppressListeners.isSet())
					callback.execute();
			});
		
		spn.valueProperty().addListener((observable, prev, now) -> { //link slider to spinner
				if (spn.getValue() != sld.getValue())
					sld.setValue(spn.getValue());
			});
		
		spn.focusedProperty().addListener((observable, prev, now) -> { //make spinner act rationally
				if (!now) 	spn.increment(0);
			});
	}
	
	
	protected static void showError(String header, String message) { //a simple thread-safe error handling thing
		Platform.runLater(() -> {
				final Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setHeaderText(header);
				alert.setContentText(message);
				alert.showAndWait();
			});
	}

}
