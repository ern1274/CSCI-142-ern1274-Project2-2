package puzzles.jam.model;

import puzzles.Configuration;
import puzzles.common.Observer;
import puzzles.common.solver.Solver;
import puzzles.jam.solver.Car;
import puzzles.jam.solver.Jam;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class JamModel {
    /** the collection of observers of this model */
    private final List<Observer<JamModel, JamClientData>> observers = new LinkedList<>();

    /** the current configuration */
    private String file;
    private JamConfig currentConfig;
    private Car currentSelection;
    private Point currentLoc;
    private Point futureLoc;
    private int select;
    private boolean hint;
    private boolean solved;
    private boolean restart;
    private boolean loaded;
    private String loadfail;

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<JamModel, JamClientData> observer) {
        this.observers.add(observer);
    }
    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(JamClientData data) {
        for (var observer : observers) {
            observer.update(this, data);
        }
    }

    /**
     * gets the Current Location of the selection
     * @return
     */
    public Point getCurrentLoc() { return currentLoc; }

    /**
     * gets the selection for the current location to move to
     * aka the place where the car is going to be moved
     * @return
     */
    public Point getFutureLoc(){return futureLoc;}

    /**
     * Constructor, gets the file and makes a config off of that and initializes the stuff for the displayBoard
     * @param file
     * @throws IOException
     */
    public JamModel(JamClientData file) throws IOException {
        this.file = file.getFile();
        JamConfig jam = new JamConfig(this.file);
        this.currentConfig = jam;
        this.select = 0;
        this.solved = false;
        this.hint = false;
        this.restart = false;
        this.loaded = false;
        this.loadfail = null;
    }

    /**
     * returns the currentConfig
     * @return
     */
    public JamConfig getCurrentConfig(){
        return currentConfig;
    }

    /**
     * returns the select which means number based value that determines whether the car has been selected or the
     * location to move has been selected
     * @return
     */
    public int getSelect(){ return select; }

    /**
     * for the load option, returns the success of the load option
     * @return
     */
    public boolean loaded(){return loaded;}

    /**
     * calls the drive method on the selected car and returns if the car has moved or not
     * @param car
     * @param row
     * @param col
     */
    public void moveCar(Car car, int row, int col){// will have to add a different method to Car class
        if(!solved) {
            this.futureLoc = new Point(row, col);
            boolean success = currentSelection.move(currentLoc,futureLoc);
            if(success){
                select = 2;
            }
            else{
                select = -2;
            }
        }
    }

    /**
     * if the current selection/car hasnt been picked then the selection is
     * to look for a car with those coordinates
     * else
     * move the car
     * @param row
     * @param col
     */
    public void selectLoc(int row, int col){
        if(this.currentSelection ==null&&!solved) {
            ArrayList<Car> jam = currentConfig.getCars();
            Point com = new Point(row, col);
            this.currentLoc = com;
            for (Car car : jam) {
                if (car.getFront().equals(com) || car.getBack().equals(com)||car.inRange(row,col)) {
                    this.currentSelection = car;
                    select = 1;
                    break;
                }
            }
        }
        else{
            moveCar(this.currentSelection, row, col);

        }
        if(currentSelection == null){
            select =-1;
        }
        alertObservers(null);
        solved = currentConfig.isGoal();
        if(select == -2 || select == 2|| select == -1){
            select = 0;
            this.currentLoc = null;
            this.currentSelection = null;
            this.futureLoc = null;
        }
    }

    /**
     * runs BFS on the current config and shows the next step
     */
    public void hint(){
        if(!solved) {
            this.hint = true;
            Solver BigBrain = new Solver();
            Optional<List<Configuration>> sol = BigBrain.doBFS(currentConfig);
            List<Configuration> path = sol.get();
            JamConfig con = (JamConfig) path.get(1);
            this.currentConfig = new JamConfig(con);
        }
        alertObservers(null);
        hint = false;
        solved = currentConfig.isGoal();

    }

    /**
     * returns whether the hint was activated or not
     * @return
     */
    public boolean getHint(){
        return hint;
    }

    /**
     * resets the current config using the client data which stored the original file
     * @throws IOException
     */
    public void reset() throws IOException {
        JamConfig jam = new JamConfig(file);
        currentConfig = new JamConfig(jam);
        restart = true;
        alertObservers(null);
        restart = false;
        solved = false;
    }
    /**
     * returns if the config is solved or not
     */
    public boolean isSolved() {
        return solved;
    }

    /**
     * returns if the config got reset
     * @return
     */
    public boolean isReset(){
        return restart;
    }

    /**
     * loads a new file by checking if it exists and replaces it with the jam client data file and
     * creates a new config and displays it
     * @param newfile
     * @throws IOException
     */
    public void load(String newfile) throws IOException {
        File hi = new File(newfile);
        if(hi.exists()) {
            file = newfile;
            JamConfig jam = new JamConfig(file);
            currentConfig = new JamConfig(jam);
            solved = false;
            loaded = true;
        }
        else {
            loaded = true;
            loadfail = newfile;
        }
        alertObservers(null);
        loaded = false;
        loadfail=null;

    }

    /**
     * returns if the file failed to load or didnt exist
     * @return
     */
    public String isLoadfail(){
        return loadfail;
    }


}
