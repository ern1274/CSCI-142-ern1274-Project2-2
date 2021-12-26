package puzzles.water;

import puzzles.Configuration;
import puzzles.common.solver.Solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Water {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(("Usage: java Water amount bucket1 bucket2 ..."));
        }
        else{
            int goal = Integer.parseInt(args[0]);
            ArrayList<Integer> fy = new ArrayList<>();
            ArrayList<Integer> ty = new ArrayList<>();
            for (int i = 1; i < args.length; i++) {
                fy.add(Integer.parseInt(args[i]));
                ty.add(0);
            }
            WaterConfig start = new WaterConfig(goal,fy,ty);

            Solver bigbrain = new Solver();
            Optional<List<Configuration>> sol = bigbrain.doBFS(start);

            if(sol.get().size()!=0){
                List<Configuration> path = sol.get();
                int steps = 0;
                for (Configuration config: path) {
                    WaterConfig con = (WaterConfig) config;
                    System.out.println("Step "+steps+": "+con.toString());
                    steps++;
                }
            }
            else{
                System.out.println("No solution");
            }
        }
    }
}
