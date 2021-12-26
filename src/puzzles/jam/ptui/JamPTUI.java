package puzzles.jam.ptui;

import puzzles.common.Observer;
import puzzles.jam.model.JamClientData;
import puzzles.jam.model.JamModel;
import puzzles.jam.solver.Jam;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class JamPTUI implements Observer<JamModel, JamClientData> {
    private JamModel model;
    private JamClientData data;

    /**
     * makes a model and gets the file from the arguments and model is based off that
     * initializes the view/ observers
     * @param file
     * @throws IOException
     */
    public JamPTUI(JamClientData file) throws IOException {
        this.model = new JamModel(file);
        this.data = file;
        initializeView(data);
    }
    /**
     * the update method that changes anything thats been typed into the controller
     */
    @Override
    public void update(JamModel jamModel, JamClientData jamClientData) {
        // if arg is null then the user did not ask to load
        displayBoard(this.model.getSelect(), this.model.getHint(), this.model.isSolved(), this.model.isReset(), this.model.loaded(), this.model.isLoadfail());// update all the cars movement on the display

        // put an if statement here saying if car X touches the end row
        //then you win.
    }

    /**
     * the commands for the controller
     */
    private void displayHelp(){
        System.out.println("h(int)              -- hint next move\n" +
                "l(oad) filename     -- load new puzzle file\n" +
                "s(elect) r c        -- select cell at r, c\n" +
                "q(uit)              -- quit the game\n" +
                "r(eset)             -- reset the current game");
    }

    /**
     * the controller
     * @throws IOException
     */
    private void run() throws IOException {
        Scanner in = new Scanner(System.in);
        displayHelp();
        for (; ; ) {
            System.out.print("> ");
            String line = in.nextLine();
            String[] words = line.split("\\s+");
            if (words.length > 0) {
                if (words[0].startsWith("q")) {
                    break;
                } else if (words[0].startsWith("r")) {
                    this.model.reset();
                    //(r)eset reset the whole configuration back to step one
                } else if (words[0].startsWith("l")) {
                    this.data.setFile(words[1]);
                    this.model.load(words[1]);
                    // (l)oad: load a different game
                } else if (words[0].startsWith("h")) {
                    this.model.hint();
                    //(h)int: show the next step configuration
                } else if (words[0].startsWith("s")) {
                    int row = Integer.parseInt(words[1]);
                    int col = Integer.parseInt(words[2]);
                    model.selectLoc(row, col);
                    //(s)elect: select a car and move it, will have take two
                    // args one for current car and other for the destination
                } else {
                        displayHelp();
                }
            }
        }
    }

    /**
     * adds observer to the model and updates it
     * @param file
     */
    public void initializeView(JamClientData file){
        this.model.addObserver(this);
        update(this.model,file);
    }

    /**
     * displays the board, changes to tell you whats happening everytime a button is pressed
     * @param select
     * @param hint
     * @param solved
     * @param restarted
     * @param loaded
     * @param loadfail
     */
    public void displayBoard(int select, boolean hint, boolean solved, boolean restarted, boolean loaded, String loadfail) {
        if(restarted) {
            System.out.println("Puzzle reset!");
        }
        else if(!solved) {
            switch (select) {
                case 1:
                    System.out.println("Selected " + "(" + this.model.getCurrentLoc().x + ", " + this.model.getCurrentLoc().y + ")");
                    break;
                case 2:
                    System.out.println("Moved from " + "(" + this.model.getCurrentLoc().x + ", " + this.model.getCurrentLoc().y + ")"
                            + " to " + "(" + this.model.getFutureLoc().x + ", " + this.model.getFutureLoc().y + ")");
                    break;
                case -1:
                    System.out.println("No car at " + "(" + this.model.getCurrentLoc().x + ", " + this.model.getCurrentLoc().y + ")");
                    break;
                case -2:
                    System.out.println("Can't move from " + "(" + this.model.getCurrentLoc().x + ", " + this.model.getCurrentLoc().y + ")"
                            + " to " + "(" + this.model.getFutureLoc().x + ", " + this.model.getFutureLoc().y + ")");
            }
            if (hint) {
                System.out.println("Next step!");
            }
            if(loaded){
                if(loadfail==null) {
                    System.out.println("Loaded: " + data.getFile().substring(9));
                }
                else{
                    System.out.println("failed to load: "+loadfail);
                }
            }
        }
        else{
            System.out.println("Already solved");
        }
        System.out.println(this.model.getCurrentConfig().toString());
    }
/**
 * starts the PTUI
 */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java JamPTUI filename");
        }
        else{
            System.out.println("Loaded: "+args[0].substring(9));
            JamClientData file = new JamClientData(args[0]);
            JamPTUI ptui = new JamPTUI(file);
            ptui.run();
        }
    }

}
