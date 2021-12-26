package puzzles.jam.gui;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import puzzles.common.Observer;
import puzzles.jam.model.JamClientData;
import puzzles.jam.model.JamConfig;
import puzzles.jam.model.JamModel;
import puzzles.jam.solver.Car;
import puzzles.jam.solver.Jam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class JamGUI extends Application  implements Observer<JamModel, JamClientData>  {
    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "resources/";

    // for demonstration purposes
    private final static String X_CAR_COLOR = "#DF0101";
    private final static int BUTTON_FONT_SIZE = 20;
    private final static int ICON_SIZE = 75;
    private JamModel model;
    private JamClientData data;
    private Label text = new Label();

    /**
     * The first stage of the GUI initialization
     * makes a model and gets the file from the arguments and model is based off that
     * initializes the view/ observers
     * @throws IOException
     */
    public void init() throws IOException {
        String filename = getParameters().getRaw().get(0);
        this.data = new JamClientData(filename);
        this.model = new JamModel(data);
        initializeView();

    }

    /**
     * initializes the observers and connects them to the model
     */
    private void initializeView(){
        this.model.addObserver(this);
        update(model,null);
    }

    @Override
    /**
     * the Start stage, where all of the buttons and events happen
     * sets up the grid and buttons
     */
    public void start(Stage stage) throws Exception {
        FileChooser fileChooser = new FileChooser();
        Stage choose = new Stage();
        fileChooser.setInitialDirectory(new File("data/jam"));

        GridPane grid = new GridPane();
        BorderPane border = new BorderPane();
        HBox bottom = new HBox();

        text.setText("Loaded: "+data.getFile().substring(9));
        Button reset = new Button(); reset.setText("reset");
        Button hint = new Button(); hint.setText("hint");
        Button load = new Button(); load.setText("load");
        reset.setMinSize(75, 40);reset.setMaxSize(75, 40);
        load.setMinSize(75, 40);load.setMaxSize(75, 40);
        hint.setMinSize(75, 40);hint.setMaxSize(75, 40);
        reset.setStyle("-fx-font-size: "+ "20");
        hint.setStyle("-fx-font-size: "+ "20");
        load.setStyle("-fx-font-size: "+ "20");

        bottom.getChildren().addAll(load,reset,hint); bottom.setSpacing(20);
        JamConfig jam = model.getCurrentConfig();
        String[][] board = jam.getBoard();
        for (int i = 0; i < jam.getSize().x; i++) {
            for (int j = 0; j < jam.getSize().y; j++) {
                Button button = new Button();
                button.setMinSize(ICON_SIZE, ICON_SIZE);
                button.setMaxSize(ICON_SIZE, ICON_SIZE);
                decorate(board, button, i,j);
                grid.add(button,j,i);
            }
        }
        for (Node node: grid.getChildren()) {
            Button but = (Button) node;
            but.setOnAction(event ->{
                model.selectLoc(GridPane.getRowIndex(but),GridPane.getColumnIndex(but));
                String[][] eboard = model.getCurrentConfig().getBoard();
                try {
                    updateBoard(eboard,grid);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }
        reset.setOnAction(event ->{
            try {
                model.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String[][] eboard = model.getCurrentConfig().getBoard();
            try {
                updateBoard(eboard,(GridPane) border.getCenter());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        hint.setOnAction(event ->{
            model.hint();
            String[][] eboard = model.getCurrentConfig().getBoard();
            try {
                updateBoard(eboard,(GridPane) border.getCenter());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        load.setOnAction(event->{
            File selected = fileChooser.showOpenDialog(choose);
            data.setFile("data/jam/"+selected.getName());
            try {
                model.load("data/jam/"+selected.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
            GridPane gridded = new GridPane();
            for (int i = 0; i < model.getCurrentConfig().getSize().x; i++) {
                for (int j = 0; j < model.getCurrentConfig().getSize().y; j++) {
                    Button button = new Button();
                    try {
                        decorate(model.getCurrentConfig().getBoard(), button, i,j);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    button.setMinSize(ICON_SIZE, ICON_SIZE);
                    button.setMaxSize(ICON_SIZE, ICON_SIZE);
                    gridded.add(button,j,i);
                }
            }
            border.setCenter(gridded);
            stage.sizeToScene();
            for (Node node: gridded.getChildren()) {
                Button but = (Button) node;
                but.setOnAction(event2 ->{
                    model.selectLoc(GridPane.getRowIndex(but),GridPane.getColumnIndex(but));
                    String[][] eboard = model.getCurrentConfig().getBoard();
                    try {
                        updateBoard(eboard,gridded);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            }
        });

        border.setTop(text);
        border.setCenter(grid);
        border.setBottom(bottom);
        Scene scene = new Scene(border);
        text.setStyle("-fx-font-size: "+ "20");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    @Override
    /**
     * the update method that changes anything thats been pressed from the model
     */
    public void update(JamModel jamModel, JamClientData jamClientData) {
        displayBoard(this.model.getSelect(), this.model.getHint(), this.model.isSolved(), this.model.isReset(), this.model.loaded(), this.model.isLoadfail());
    }

    /**
     * changes the text on top of the GUI screen to tell the user whats happening
     * @param select
     * @param hint
     * @param solved
     * @param restarted
     * @param loaded
     * @param loadfail
     */
    public void displayBoard(int select, boolean hint, boolean solved, boolean restarted, boolean loaded, String loadfail) {
        if(restarted) {
            text.setText("Puzzle reset!");
        }
        else if(!solved) {
            switch (select) {
                case 1:
                    text.setText("Selected " + "(" + this.model.getCurrentLoc().x + ", " + this.model.getCurrentLoc().y + ")");
                    break;
                case 2:
                    text.setText("Moved from " + "(" + this.model.getCurrentLoc().x + ", " + this.model.getCurrentLoc().y + ")"
                            + " to " + "(" + this.model.getFutureLoc().x + ", " + this.model.getFutureLoc().y + ")");
                    break;
                case -1:
                    text.setText("No car at " + "(" + this.model.getCurrentLoc().x + ", " + this.model.getCurrentLoc().y + ")");
                    break;
                case -2:
                    text.setText("Can't move from " + "(" + this.model.getCurrentLoc().x + ", " + this.model.getCurrentLoc().y + ")"
                            + " to " + "(" + this.model.getFutureLoc().x + ", " + this.model.getFutureLoc().y + ")");
            }
            if (hint) {
                text.setText("Next step!");
            }
            if(loaded){
                if(loadfail==null) {
                    text.setText("Loaded: " + data.getFile().substring(9));
                }
                else{
                    text.setText("failed to load: "+loadfail);
                }
            }
        }
        else{
            text.setText("Already solved");
        }
    }

    /**
     * the main launches the application
     * @param args
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * changes the buttons colors based off their name
     * @param board: the current configs board
     * @param button: the button itself
     * @param row: the row that the button is in
     * @param col: the col the button is in
     * @throws FileNotFoundException
     */
    private void decorate(String[][] board,Button button, int row, int col) throws FileNotFoundException {
        String thing = board[row][col];
        switch (thing){
            case ".":
                button.setText(" ");
                button.setStyle(
                        "-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                                "-fx-background-color: " + "#FFFFFF" + ";" +
                                "-fx-font-weight: bold;");
                break;
            case "A":
                button.setText("A");
                button.setStyle(
                        "-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                                "-fx-background-color: " + "#9CC7E8" + ";" +
                                "-fx-font-weight: bold;");
                break;
            case "B":
                button.setText("B");
                button.setStyle(
                        "-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                                "-fx-background-color: " + "#068DF4" + ";" +
                                "-fx-font-weight: bold;");
                break;
            case "C":
                button.setText("C");
                button.setStyle(
                        "-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                                "-fx-background-color: " + "#C70039" + ";" +
                                "-fx-font-weight: bold;");
                break;
            case "D":
                button.setText("D");
                button.setStyle(
                        "-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                                "-fx-background-color: " + "#FFC300" + ";" +
                                "-fx-font-weight: bold;");
                break;
            case "E":
                button.setText("E");
                button.setStyle(
                        "-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                                "-fx-background-color: " + "#F45B3B" + ";" +
                                "-fx-font-weight: bold;");
                break;
            case "F":
                button.setText("F");
                button.setStyle(
                        "-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                                "-fx-background-color: " + "#E89CD7" + ";" +
                                "-fx-font-weight: bold;");
                break;
            case "G":
                button.setText("G");
                button.setStyle(
                        "-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                                "-fx-background-color: " + "#71E56D" + ";" +
                                "-fx-font-weight: bold;");
                break;
            case "H":
                button.setText("H");
                button.setStyle(
                        "-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                                "-fx-background-color: " + "#003ACB" + ";" +
                                "-fx-font-weight: bold;");
                break;
            case "I":
                button.setText("I");
                button.setStyle(
                        "-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                                "-fx-background-color: " + "#F11ED2" + ";" +
                                "-fx-font-weight: bold;");
                break;
            case "J":
                button.setText("J");
                button.setStyle(
                        "-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                                "-fx-background-color: " + "#581845" + ";" +
                                "-fx-font-weight: bold;");
                break;
            case "K":
                button.setText("K");
                button.setStyle(
                        "-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                                "-fx-background-color: " + "#BE2357" + ";" +
                                "-fx-font-weight: bold;");
                break;
            case "L":
                button.setText("L");
                button.setStyle(
                        "-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                                "-fx-background-color: " + "#DBC256" + ";" +
                                "-fx-font-weight: bold;");
                break;
            case "O":
                button.setText("O");
                button.setStyle(
                        "-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                                "-fx-background-color: " + "#117711" + ";" +
                                "-fx-font-weight: bold;");
                break;
            case "P":
                button.setText("P");
                button.setStyle(
                        "-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                                "-fx-background-color: " + "#4F5489" + ";" +
                                "-fx-font-weight: bold;");
                break;
            case "Q":
                button.setText("Q");
                button.setStyle(
                        "-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                                "-fx-background-color: " + "#964BE2" + ";" +
                                "-fx-font-weight: bold;");
                break;
            case "R":
                button.setText("R");
                button.setStyle(
                        "-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                                "-fx-background-color: " + "#43EEBD" + ";" +
                                "-fx-font-weight: bold;");
                break;
            case "S":
                button.setText("S");
                button.setStyle(
                        "-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                                "-fx-background-color: " + "#F4FA0D" + ";" +
                                "-fx-font-weight: bold;");
                break;
            case "X":
                button.setText("X");
                button.setStyle(
                        "-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                                "-fx-background-color: " + X_CAR_COLOR + ";" +
                                "-fx-font-weight: bold;");

                break;
        }
    }

    /**
     * calls update board on each button with the grid it is provided
     * @param board
     * @param grid
     * @throws FileNotFoundException
     */
    public void updateBoard(String[][] board, GridPane grid) throws FileNotFoundException {
        for (Node node:grid.getChildren()) {
            Button button = (Button) node;
            int row = GridPane.getRowIndex(button);
            int col = GridPane.getColumnIndex(button);
            decorate(board,button,row,col);
        }
    }
}
