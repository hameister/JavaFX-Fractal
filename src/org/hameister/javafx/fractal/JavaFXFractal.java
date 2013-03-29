/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.hameister.javafx.fractal;



import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxBuilder;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineBuilder;
import javafx.scene.text.TextBuilder;
import javafx.stage.Stage;

/**
 * 
 * @author Jörn Hameister
 * 
 * http://www.hameister.org
 * 
 */
public class JavaFXFractal extends Application {

    private static final int CANVAS_WIDTH = 750;
    private static final int CANVAS_HEIGHT = 600;
    // Left and right border
    private static final int X_OFFSET = 100;
    // Top and Bottom border
    private static final int Y_OFFSET = 50;
    // Width of the Application Scene
    private static final int WIDTH = (2 * X_OFFSET) + CANVAS_WIDTH;
    // Height of the Application Scene
    private static final int HEIGHT = (2 * Y_OFFSET) + CANVAS_HEIGHT;
    // Size of the coordinate system for the Mandelbrot set
    private static double MANDELBROT_RE_MIN = -2;
    private static double MANDELBROT_RE_MAX = 1;
    private static double MANDELBROT_IM_MIN = -1;
    private static double MANDELBROT_IM_MAX = 1;
    // Size of the coordinate system for the Julia set
    private static double JULIA_RE_MIN = -1.5;
    private static double JULIA_RE_MAX = 1.5;
    private static double JULIA_IM_MIN = -1.5;
    private static double JULIA_IM_MAX = 1.5;
    // List with the Nodes for the Coordinate system (Lines, Text)
    private List<Node> coordinateSystemNodes;
    // Main Panel with den Mandelbrot set
    private Pane fractalRootPane;
    // Canvas for the Mandelbrot oder Julia set
    private Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
    private CheckBox showCoordinateSystem;
    private CheckBox switchMandelbrotJuliaSet;
    private ChoiceBox colorSchemeChoiceBox;
    private ColorPicker convergenceColorPicker;
    private TextField z;
    private TextField zi;
    private MandelbrotBean bean;

    @Override
    public void start(Stage primaryStage) {
        fractalRootPane = new Pane();

        // Move Canvas to the correct position
        canvas.setLayoutX(X_OFFSET);
        canvas.setLayoutY(Y_OFFSET);

        // Paint canvan with initial Mandelbrot set
        fractalRootPane.getChildren().add(canvas);
        bean = new MandelbrotBean(50, MANDELBROT_RE_MIN, MANDELBROT_RE_MAX, MANDELBROT_IM_MIN, MANDELBROT_IM_MAX, 0, 0);
        paintSet(canvas.getGraphicsContext2D(), bean);

        // Create controls
        coordinateSystemNodes = createCoordinateSystem(bean);
        switchMandelbrotJuliaSet = createMandelbrotJuliaCheckBox();
        showCoordinateSystem = createShowCoordinateCheckBox();
        convergenceColorPicker = createConvergenceColorPicker();
        colorSchemeChoiceBox = createSchemaChoiceBox();
        z = createZTextField();
        zi = createZiTextField();

        // Grid layout for the controls
        GridPane grid = new GridPane();
        grid.setHgap(10); // LEFT, RIGHT
        grid.setVgap(5); // TOP, BOTTOM
        grid.setPadding(new Insets(10, 10, 0, 10));
        // Show the GridLines
        grid.setGridLinesVisible(true);

        grid.add(showCoordinateSystem, 0, 0);
        grid.add(switchMandelbrotJuliaSet, 0, 1);

        grid.add(TextBuilder.create().text("Color Schema: ").styleClass("fractal-text").build(), 1, 0);
        grid.add(TextBuilder.create().text("Covergence Color: ").styleClass("fractal-text").build(), 1, 1);

        grid.add(colorSchemeChoiceBox, 2, 0);
        grid.add(convergenceColorPicker, 2, 1);

        grid.add(LabelBuilder.create().text("z:").styleClass("fractal-text").build(), 3, 0);
        grid.add(LabelBuilder.create().text("zi:").styleClass("fractal-text").build(), 3, 1);

        grid.add(z, 4, 0);
        grid.add(zi, 4, 1);

        BorderPane border = new BorderPane();
        border.setTop(grid);
        border.setCenter(fractalRootPane);
        fractalRootPane.getChildren().addAll(coordinateSystemNodes);

        Scene scene = new Scene(border, WIDTH, HEIGHT);

        primaryStage.setTitle("JavaFX Fractal");
        primaryStage.setScene(scene);
        primaryStage.getScene().getStylesheets().add("fractal");
        primaryStage.show();
    }

    private CheckBox createMandelbrotJuliaCheckBox() {
        CheckBox switchMandelbrotJuliaSetCheckBox = CheckBoxBuilder.create()
                .text("Switch Mandelbrot/Julia")
                .styleClass("fractal-text")
                .selected(true)
                .onAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                boolean checkBoxSelected = ((CheckBox) e.getSource()).isSelected();
                if (checkBoxSelected) {
                    bean = new MandelbrotBean(50, MANDELBROT_RE_MIN, MANDELBROT_RE_MAX, MANDELBROT_IM_MIN, MANDELBROT_IM_MAX, 0, 0);
                    z.setDisable(true);
                    zi.setDisable(true);

                    canvas.setLayoutX(X_OFFSET);
                    canvas.setLayoutY(Y_OFFSET);
                } else {
                    bean = new MandelbrotBean(50, JULIA_RE_MIN, JULIA_RE_MAX, JULIA_IM_MIN, JULIA_IM_MAX, 0.3, -0.5);

                    z.setDisable(false);
                    zi.setDisable(false);

                    // Reset value of z and zi
                    z.setText(String.valueOf(bean.getZ()));
                    zi.setText(String.valueOf(bean.getZi()));

                    // Move canvans to the middlepoint
                    canvas.setLayoutX(CANVAS_WIDTH / (bean.getReMax() - bean.getReMin()) / 2 + X_OFFSET/2);
                    canvas.setLayoutY(CANVAS_HEIGHT/ (bean.getImMax() - bean.getImMin()) / 2 - Y_OFFSET*2);
                }
                bean.setColorSchema((MandelbrotBean.ColorSchema) colorSchemeChoiceBox.getSelectionModel().getSelectedItem());
                bean.setConvergenceColor(convergenceColorPicker.getValue());
                paintSet(canvas.getGraphicsContext2D(), bean);
                switchCoordnateSystem(bean);
            }
        }).build();

        return switchMandelbrotJuliaSetCheckBox;
    }

    private CheckBox createShowCoordinateCheckBox() {
        CheckBox showCoordinateSystemCheckBox = CheckBoxBuilder.create()
                .text("Coordinates")
                .styleClass("fractal-text")
                .selected(true)
                .onAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                boolean checkBoxSelected = ((CheckBox) e.getSource()).isSelected();
                if (checkBoxSelected) {
                    fractalRootPane.getChildren().addAll(coordinateSystemNodes);
                } else {
                    fractalRootPane.getChildren().removeAll(coordinateSystemNodes);
                }
            }
        }).build();

        return showCoordinateSystemCheckBox;
    }

    private void switchCoordnateSystem(MandelbrotBean bean) {
        if (showCoordinateSystem.isSelected()) {
            fractalRootPane.getChildren().removeAll(coordinateSystemNodes);
            coordinateSystemNodes = createCoordinateSystem(bean);
            fractalRootPane.getChildren().addAll(coordinateSystemNodes);
        } else {
            //Also calculate new coordinate system when the system is not visible
            // later it maybe...
            coordinateSystemNodes = createCoordinateSystem(bean);
        }
    }

    private List<Node> createCoordinateSystem(MandelbrotBean bean) {
        List<Node> coordinateSystem = new ArrayList<>();

        double stepsX = (bean.getReMax() - bean.getReMin()) / CANVAS_WIDTH;

        int xPos = X_OFFSET;
        double eps = 0.01;
        for (double x = bean.getReMin(); x < bean.getReMax() + eps; x = x + stepsX, xPos++) {
            if (x == 0 || (x > 0 && x < 0.0001)) {
                // Paint y-Axis
                coordinateSystem.add(LineBuilder.create().startX(xPos).startY(Y_OFFSET).endX(xPos).endY(500 + Y_OFFSET).strokeWidth(1).stroke(Color.RED).build());
                coordinateSystem.add(TextBuilder.create().x(xPos - 20).y(Y_OFFSET - 10).text(String.valueOf(bean.getImMax())).stroke(Color.RED).styleClass("coordinate-system-text").build());
                coordinateSystem.add(TextBuilder.create().x(xPos - 20).y(500 + Y_OFFSET + 25).text(String.valueOf(bean.getImMin())).styleClass("coordinate-system-text").stroke(Color.RED).build());
            } else if (Math.abs(x) == 1 || Math.abs(x) > 1 && Math.abs(x) < 1 + eps) {
                coordinateSystem.add(LineBuilder.create().startX(xPos).startY(260 + Y_OFFSET).endX(xPos).endY(240 + Y_OFFSET).strokeWidth(1).stroke(Color.RED).build());
            }
        }

        // Paint x-Axis
        coordinateSystem.add(LineBuilder.create().startX(X_OFFSET).startY((HEIGHT/2)-Y_OFFSET).endX(WIDTH - X_OFFSET).endY((HEIGHT/2)-Y_OFFSET).strokeWidth(1).stroke(Color.RED).build());

        // x-Text
        coordinateSystem.add(TextBuilder.create().x(X_OFFSET - 50).y(255 + Y_OFFSET).text(String.valueOf(bean.getReMin())).styleClass("coordinate-system-text").build());
        coordinateSystem.add(TextBuilder.create().x(749 + X_OFFSET + 10).y(255 + Y_OFFSET).text(String.valueOf(bean.getReMax())).styleClass("coordinate-system-text").build());

        return coordinateSystem;
    }

    private void paintSet(GraphicsContext ctx, MandelbrotBean bean) {
        double precision = Math.max((bean.getReMax() - bean.getReMin()) / CANVAS_WIDTH, (bean.getImMax() - bean.getImMin()) / CANVAS_HEIGHT); // 0.004;

        double convergenceValue;
        for (double c = bean.getReMin(), xR = 0; xR < CANVAS_WIDTH; c = c + precision, xR++) {
            for (double ci = bean.getImMin(), yR = 0; yR < CANVAS_HEIGHT; ci = ci + precision, yR++) {
                if (bean.isIsMandelbrot()) {
                    convergenceValue = checkConvergence(ci, c, 0, 0, bean.getConvergenceSteps());
                } else {
                    convergenceValue = checkConvergence(bean.getZi(), bean.getZ(), ci, c, bean.getConvergenceSteps());
                }
                double t1 = (double) convergenceValue / bean.getConvergenceSteps(); //(50.0 .. )
                double c1 = Math.min(255 * 2 * t1, 255);
                double c2 = Math.max(255 * (2 * t1 - 1), 0);

                if (convergenceValue != bean.getConvergenceSteps()) {
                    //Set color
                    ctx.setFill(getColorSchema(c1, c2));
                } else {
                    ctx.setFill(bean.getConvergenceColor());
                }
                ctx.fillRect(xR, yR, 1, 1);
            }
        }
    }

        /**
     * Checks the convergence of a coordinate (c, ci) The convergence factor
     * determines the color of the point.
     */
    private int checkConvergence(double ci, double c, double z, double zi, int convergenceSteps) {
        for (int i = 0; i < convergenceSteps; i++) {
            double ziT = 2 * (z * zi);
            double zT = z * z - (zi * zi);
            z = zT + c;
            zi = ziT + ci;

            if (z * z + zi * zi >= 4.0) {
                return i;
            }
        }
        return convergenceSteps;
    }
    
    private Color getColorSchema(double c1, double c2) {
        MandelbrotBean.ColorSchema colorSchema = bean.getColorSchema();
        switch (colorSchema) {
            case RED:
                return Color.color(c1 / 255.0, c2 / 255.0, c2 / 255.0);
            case YELLOW:
                return Color.color(c1 / 255.0, c1 / 255.0, c2 / 255.0);
            case MAGENTA:
                return Color.color(c1 / 255.0, c2 / 255.0, c1 / 255.0);
            case BLUE:
                return Color.color(c2 / 255.0, c2 / 255.0, c1 / 255.0);
            case GREEN:
                return Color.color(c2 / 255.0, c1 / 255.0, c2 / 255.0);
            case CYAN:
                return Color.color(c2 / 255.0, c1 / 255.0, c1 / 255.0);
            default:
                return Color.color(c2 / 255.0, c1 / 255.0, c2 / 255.0);
        }
    }



    private ChoiceBox createSchemaChoiceBox() {
        ChoiceBox colorSchema = new ChoiceBox(FXCollections.observableArrayList(MandelbrotBean.ColorSchema.values()));
        colorSchema.getSelectionModel().select(bean.getColorSchema());

        colorSchema.valueProperty().addListener(new ChangeListener<MandelbrotBean.ColorSchema>() {
            @Override
            public void changed(ObservableValue ov, MandelbrotBean.ColorSchema oldColorSchema, MandelbrotBean.ColorSchema newColorSchema) {
                bean.setColorSchema(newColorSchema);
                paintSet(canvas.getGraphicsContext2D(), bean);
            }
        });
        return colorSchema;
    }

    private ColorPicker createConvergenceColorPicker() {
        ColorPicker colorPicker = new ColorPicker(Color.WHITE);
        
        colorPicker.valueProperty().addListener(new ChangeListener<Color>() {
            @Override
            public void changed(ObservableValue ov, Color oldColorSchema, Color newColorSchema) {
                bean.setConvergenceColor(newColorSchema);
                paintSet(canvas.getGraphicsContext2D(), bean);
            }
        });
        
        return colorPicker;
    }

    private TextField createZTextField() {
        TextField zField = TextFieldBuilder.create()
                .text(String.valueOf(bean.getZ()))
                .prefWidth(60)
                .disable(true)
                .build();

        zField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                TextField textField = (TextField) t.getSource();
                String number = textField.getText();
                try {
                    bean.setZ(Double.valueOf(number));
                    paintSet(canvas.getGraphicsContext2D(), bean);
                } catch (NumberFormatException e) {
                    textField.setText(String.valueOf(bean.getZ()));
                }
            }
        });

        return zField;
    }

    private TextField createZiTextField() {
        TextField ziField = TextFieldBuilder.create()
                .text(String.valueOf(bean.getZi()))
                .disable(true)
                .prefWidth(60)
                .build();

        ziField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                TextField textField = (TextField) t.getSource();
                String number = textField.getText();
                try {
                    bean.setZi(Double.valueOf(number));
                    paintSet(canvas.getGraphicsContext2D(), bean);
                } catch (NumberFormatException e) {
                    textField.setText(String.valueOf(bean.getZi()));
                }
            }
        });
        return ziField;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
