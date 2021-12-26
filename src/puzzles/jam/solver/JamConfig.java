package puzzles.jam.solver;

import puzzles.Configuration;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class JamConfig implements Configuration {
    public final static String EMPTY = ".";

    private boolean check = true;
    private String[][] board;
    private int Carnum;
    private int row;
    private int col;
    private ArrayList<Car> cars;
    public JamConfig(String filename) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String hi = in.readLine();
        String[] RowClm= hi.split(" ");
        int RowNum = Integer.parseInt(String.valueOf(RowClm[0]));
        this.row = RowNum;
        int ColNum = Integer.parseInt(String.valueOf(RowClm[1]));
        this.col = ColNum;
        this.Carnum = Integer.parseInt(in.readLine());
        this.board = new String[RowNum][ColNum];
        for (int row = 0; row < RowNum; row++) {
            for (int col = 0; col < ColNum; col++) {
                board[row][col] = EMPTY;
            }
        }
        this.cars = new ArrayList<>();
        String car = in.readLine();
        for (int i = 0; i != this.Carnum; i++) {
            cars.add(new Car(this.board, car, row, col));
            car = in.readLine();
        }

    }
    public JamConfig(JamConfig other) {
        this.Carnum = other.Carnum;
        this.row = other.row;
        this.col = other.col;
        this.cars = new ArrayList<>();
        this.board = new String[other.row][other.col];
        for (int r=0; r<other.row; r++) {
                System.arraycopy(other.board[r], 0, this.board[r], 0, other.col);
        }
        for (int i = 0; i != this.Carnum; i++) {
            this.cars.add(new Car(other.cars.get(i),this.board));
        }
    }

    @Override
    public Collection<Configuration> getSuccessors() {
        List<Configuration> successors = new ArrayList<>();
        for (int i = 0; i < cars.size(); i++) {
            JamConfig forward = new JamConfig(this);
            JamConfig back = new JamConfig(this);
            forward.cars.get(i).drive();
            if(forward.isGoal()){
                successors.add(forward);
                break;
            }
            if(!forward.equals(this)){
                successors.add(forward);
            }
            back.cars.get(i).backup();
            if(back.isGoal()){
                successors.add(back);
                break;
            }
            if(!back.equals(this)){
                successors.add(back);
            }
        }
        return successors;
    }
    @Override
    public boolean isValid() {
        return false;
    }
    public boolean isGoal(){
        Car x = cars.get(cars.size()-1);
        if(x.getFront().getY()==col-1) {
            return true;
        }
        return false;
    }
    @Override
    public String toString() {
        String config = "";
        for (String[] item:board) {
            for (String cha:item) {
                config += cha;
                config += " ";
            }
            config +='\n';
        }
        return config;
    }
    private int getRow(){
        return row;
    }
    @Override
    public boolean equals(Object other){
        JamConfig oth = (JamConfig) other;
        if (this.board == null) {
            return (oth.board == null);
        }
        if (oth.board == null) {
            return false;
        }
        if (this.board.length != oth.board.length) {
            return false;
        }
        for (int i = 0; i < this.board.length; i++) {
            if (!Arrays.equals(this.board[i], oth.board[i])) {
                return false;
            }
        }
        return true;
    }
    @Override
    public int hashCode(){
        int result = 0;
        for (Car car:cars) {
            result+= (int)car.getFront().getX()+car.getFront().getY();
            result+= (int)car.getBack().getX()+car.getBack().getY();
        }
        return result;
    }
}
