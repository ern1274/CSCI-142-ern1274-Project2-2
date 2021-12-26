package puzzles.clock;

import puzzles.Configuration;
import puzzles.common.solver.Solver;

import java.util.List;
import java.util.Optional;

public class Clock {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java Clock hours start end");
        }
        else{
            ClockConfig start = new ClockConfig(Integer.parseInt(args[0]),Integer.parseInt(args[1]),Integer.parseInt(args[2]));
            Solver bigbrain = new Solver();
            System.out.println("Hours: "+start.getHours()+", Start: "+start.getCurrent()+", End: "+start.getEnd());
            Optional<List<Configuration>> sol = bigbrain.doBFS(start);
            if(sol.isPresent()){
                List<Configuration> path = sol.get();
                int steps = 0;
                for (Configuration config: path) {
                    ClockConfig con = (ClockConfig) config;
                    System.out.println("Step "+steps+": "+con.getCurrent());
                    steps++;
                }
            }
            else{
                System.out.println("No solution");
            }
        }
    }
}
