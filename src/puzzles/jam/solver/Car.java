package puzzles.jam.solver;
import java.awt.Point;

public class Car {
    public final static String EMPTY = ".";
    private String name;
    private boolean RC;//true for row and false for column
    //decided by the cars orientation
    private Point row;
    private Point col;
    private Point back;
    private Point front;
    private Point size;
    private String[][] board;
    private int length;

    /**
     * the Constructor for a Car object
     * this object is assigned to a position on the board based on its name
     * @param board
     * @param car
     * @param rows
     * @param cols
     */
    public Car(String[][] board, String car,int rows, int cols){
        String[] loc = car.split(" ");
        this.size = new Point(rows,cols);
        this.board = board;
        this.name = loc[0];
        this.row = new Point(Integer.parseInt(loc[1]),Integer.parseInt(loc[3]));
        this.col = new Point(Integer.parseInt(loc[2]),Integer.parseInt(loc[4]));

        this.RC = row.getX()==row.getY();

        if(this.RC){
            front = new Point(row.x,col.y);
            back = new Point(row.y,col.x);
            this.length = front.y-back.y+1;
            for (int f = this.col.x; f <= this.col.y; f++) {
                board[row.x][f] = name;
            }
        }
        else{
            front = new Point(row.x,col.x);
            back = new Point(row.y,col.y);
            this.length = back.x-front.x+1;
            for (int f =this.row.x; f <= this.row.y; f++) {
                board[f][col.x] = name;
            }
        }
    }
    public Car(Car other,String[][] board) {
        this.name = other.name;
        this.RC = other.RC;
        this.board = board;
        this.row = other.getRow();
        this.col = other.getCol();
        this.length = other.length;
        this.front = new Point((int)other.getFront().getX(),(int)other.getFront().getY());
        this.back = new Point((int)other.getBack().getX(),(int)other.getBack().getY());
        this.size = other.size;

    }
    //returns the row of the car
    public Point getRow(){
        return row;
    }
    //returns the col of the car
    public Point getCol(){
        return col;
    }
    // returns the name of the car
    public String getName(){
        return name;
    }
    // returns the Back coordinates of the Car
    public Point getBack(){
        return back;
    }
    // returns the Front Coordinates of the Car
    public Point getFront(){
        return front;
    }

    /**
     * used for the Problem Solver
     * checks if the car is horizontal or vertical and
     * then checks if the front of the car is clear
     */
    public void drive(){
        int oldx = back.x;
        int oldy = back.y;
        if(RC){
            if(front.y+1<size.y && board[front.x][front.y+1].equals(EMPTY)){
                front.setLocation(front.x,front.y+1); back.setLocation(back.x,back.y+1);
                board[front.x][front.y] = name; board[back.x][back.y] = name;
                board[oldx][oldy] = EMPTY;
            }
        }
        else{
            if(front.x-1>=0 && board[front.x - 1][front.y].equals(EMPTY)){
                front.setLocation(front.x-1,front.y); back.setLocation(back.x-1,back.y);
                board[front.x][front.y] = name; board[back.x][back.y] = name;
                board[oldx][oldy] = EMPTY;

            }
        }

    }
    /**
     * used for the Problem Solver
     * checks if the car is horizontal or vertical and
     * then checks if the back of the car is clear
     */
    public void backup(){
        int oldx = (int)front.getX();
        int oldy = (int)front.getY();
        if(RC){
            if(back.y-1>=0&&board[back.x][back.y-1].equals(EMPTY)){
                front.setLocation(front.x,front.y-1); back.setLocation(back.x,back.y-1);
                board[back.x][back.y] = name;
                board[front.x][front.y] = name;
                board[oldx][oldy] = EMPTY;
            }
        }
        else{
            if(back.x+1<size.x&& board[back.x + 1][back.y].equals(EMPTY)){
                front.setLocation(front.x+1,front.y);
                back.setLocation(back.x+1,back.y);
                board[back.x][back.y] = name;
                board[front.x][front.y] = name;
                board[oldx][oldy] = EMPTY;
            }
        }
    }
    /**
     * used for the PTUI and GUI, takes a destination and
     * checks of the path to the destination is clear and if its in the
     * same row or column as the car that is selected
     */
    public boolean move(Point current, Point destination){
        if(RC){
            if(destination.x!=front.x){return false;}
            else{
                // if the bottom/back is closer to the destination than the front
                //
                //
                if(Math.abs(back.y-destination.y)<Math.abs(front.y-destination.y)){
                    for (int i = 1; i <= Math.abs(back.y-destination.y); i++) {
                        if(!board[back.x][back.y - i].equals(EMPTY)){ return false; }
                    }

                    int oldrow = (int) back.getX();
                    int oldcol = (int) back.getY();
                    back.setLocation(destination.x, destination.y);
                    for (int i = 0; i < length ; i++) {
                        board[oldrow][oldcol+i] = EMPTY;
                    }
                    for (int i = 0; i < length ; i++) {
                        board[back.x][back.y+i] = name;
                        if(i==length-1){
                            front.setLocation(back.x,back.y+i);
                        }
                    }
                    return true;
                }
                else{ // if the top/front is closer to the destination than the back\
                    //
                    //
                    for (int i = 1; i <= Math.abs(front.y-destination.y); i++) {
                        if(!board[front.x][front.y + i].equals(EMPTY)){
                            return false;
                        }
                    }
                    int oldrow = (int) front.getX();
                    int oldcol = (int) front.getY();
                    front.setLocation(destination.x, destination.y);
                    for (int i = 0; i < length ; i++) {
                        board[oldrow][oldcol-i] = EMPTY;
                    }
                    for (int i = 0; i < length ; i++) {
                        board[front.x][front.y-i] = name;
                        if(i==length-1){
                            back.setLocation(front.x,front.y-i);
                        }
                    }
                    return true;
                }
            }
        }
        else{
            if(destination.y!=front.y){
                return false;
            }
            else{
                // if the bottom/back is closer to the destination than the front
                //
                //
                if(Math.abs(back.x-destination.x)<Math.abs(front.x-destination.x)){
                    for (int i = 1; i <= Math.abs(back.x-destination.x); i++) {
                        if(!board[back.x + i][back.y].equals(EMPTY)){ return false; }
                    }
                    int oldrow = (int) back.getX();
                    int oldcol = (int) back.getY();
                    back.setLocation(destination.x, destination.y);

                    for (int i = 0; i < length ; i++) {
                        board[oldrow-i][oldcol] = EMPTY; }

                    for (int i = 0; i < length ; i++) {
                        board[back.x-i][back.y] = name;
                        if(i==length-1){ front.setLocation(back.x-i,back.y); }
                    }
                    return true;
                }
                else{ // if the front/top is closer to the destination
                    //
                    //
                    for (int i = 1; i <= Math.abs(front.x-destination.x); i++) {
                        if(!board[front.x - i][front.y].equals(EMPTY)){ return false; } }
                    int oldrow = (int) front.getX();
                    int oldcol = (int) front.getY();
                    front.setLocation(destination.x, destination.y);
                    for (int i = 0; i < length ; i++) {
                        board[oldrow+i][oldcol] = EMPTY;
                    }
                    for (int i = 0; i < length ; i++) {
                        board[front.x+i][front.y] = name;
                        if(i==length-1){ back.setLocation(front.x+i,front.y); }
                    }
                    return true;
                }
            }
        }
    }

    /**
     * checks if the Car is in range of the row or column that was selected
     * @param row
     * @param col
     * @return
     */
    public boolean inRange(int row, int col){
            if (RC) {
                if(row!=this.row.x){return false;}
                if (this.back.y <= col && this.front.y >= col) {
                    return true;
                }
            } else {
                if(col!=this.col.x){return false;}
                if (this.front.x <= row && this.back.x >= row) {
                    return true;
                }
            }
        return false;
    }

// returns the length of the car
    public int getLength(){
        return length;
    }
}
