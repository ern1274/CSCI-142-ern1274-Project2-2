package puzzles.jam.solver;

import puzzles.Configuration;
import puzzles.clock.ClockConfig;
import puzzles.common.solver.Solver;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class Jam {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java Jam filename");
        }
        JamConfig jam = new JamConfig(args[0]);
        Solver BigBrain = new Solver();
        Optional<List<Configuration>> sol = BigBrain.doBFS(jam);
        System.out.println("Total configs: "+BigBrain.getTotal()+"\n"+"Unique Configs: "+BigBrain.getUnique());
        if(sol.isPresent()){
            List<Configuration> path = sol.get();
            int steps = 0;
            for (Configuration config: path) {
                JamConfig con = (JamConfig) config;
                System.out.println("Step "+steps+": \n"+con.toString());
                steps++;
            }
        }
        else{
            System.out.println("No solution");
        }

    }
}