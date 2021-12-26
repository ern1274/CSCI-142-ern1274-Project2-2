package puzzles.water;

import puzzles.Configuration;
import puzzles.clock.ClockConfig;

import java.util.*;


public class WaterConfig implements Configuration {
    private int goal;
    private ArrayList<Integer> caps;
    private ArrayList<Integer> bucks;

    /**
     * The constructor where it starts with an empty arraylist and gets filled by later configs
     * takes any number of buckets/caps
     * @param goal: the number any of the buckets has to reach
     * @param caps: the buckets with their capacities
     * @param bucks: the arraylist of buckets and their current holding
     */
    public WaterConfig(int goal, ArrayList<Integer> caps, ArrayList<Integer>bucks){
        this.goal = goal;
        this.caps = caps;
        this.bucks = new ArrayList<>(bucks);

    }

    /**
     * fills a bucket to the max using the caps/capacities number
     * @param index: the bucket in buck/buckets which calls caps index because they are correllated
     */
    public void fill(int index){
        this.bucks.set(index,caps.get(index));
    }

    /**
     * dumps a bucket out using the bucks index number
     * @param index: the bucket in buck/buckets
     */
    public void dump(int index){
        this.bucks.set(index,0);
    }

    /**
     * pours from one bucket to another
     * fir pours into sec
     * makes sure that the amount poured gets capped out at that buckets max
     * @param fir: the bucket pouring
     * @param sec: the bucket being poured into
     */
    public void pour(int fir, int sec){
        int cap1 = caps.get(fir); int cap2 = caps.get(sec);
        int buc1 = bucks.get(fir); int buc2 = bucks.get(sec);
        if(buc1+buc2<=cap2){
            bucks.set(sec,Math.abs(buc1+buc2));
            dump(fir);
        }
        else{
            bucks.set(fir,Math.abs((cap2-buc2)-buc1));
            fill(sec);
        }
    }

    /**
     * makes a nested loop where the inner loop mainly focuses on pouring and the outer focuses on a new bucket
     * filling and dumping
     * @return A collection of Configs
     */
    @Override
    public Collection<Configuration> getSuccessors() {
        List<Configuration> successors = new ArrayList<>();
        for (int i = 0; i < bucks.size(); i++) {
            ArrayList<Integer> copy = (ArrayList<Integer>) bucks.clone();
            for (int j = 0; j < bucks.size(); j++) {
                WaterConfig pour = new WaterConfig(this.goal,this.caps,copy);
                if(i!=j) {
                    pour.pour(i, j);
                    successors.add(pour);
                }

            }
            WaterConfig fill = new WaterConfig(this.goal,this.caps,copy);
            fill.fill(i);
            WaterConfig dump = new WaterConfig(this.goal,this.caps,copy);
            dump.dump(i);
            successors.add(fill);successors.add(dump);

        }
        return successors;
    }

    /**
     * Checks to make sure that the config did not go over its capacity or below 0
     * @return boolean true or false
     */
    @Override
    public boolean isValid() {
        for (int i = 0; i < caps.size(); i++) {
            if(bucks.get(i)>caps.get(i)&&bucks.get(i)<0){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks each buckets current and returns true if any of it matches the goal number
     * @return boolean true or false
     */
    @Override
    public boolean isGoal() {
        for (int i = 0; i < bucks.size(); i++) {
            if(bucks.get(i)==goal){
                return true;
            }
        }
        return false;
    }

    /**
     * returns the arraylist of integers
     * @return
     */
    public ArrayList<Integer> getBucks(){
        return this.bucks;
    }


    /**
     * uses mostly comparing each buckets current and cap
     * @param other: the comparing object
     * @return boolean true or false
     */
    @Override
    public boolean equals(Object other){
        boolean result = true;
        if(other instanceof WaterConfig ){
            WaterConfig second = (WaterConfig) other;
            ArrayList<Integer> buckscop = second.getBucks();
            for (int i = 0; i < caps.size(); i++) {
                if(!bucks.get(i).equals(buckscop.get(i))){
                    result = false;
                }
            }

        }
        return result;
    }

    /**
     * uses integers to decide its hashcode
     * @return
     */
    @Override
    public int hashCode(){
        int total = 0;
        for (int i = 0; i < caps.size(); i++) {
            total+= bucks.get(i)+caps.get(i);
        }
        return total;
    }


    /**
     * the toString returns all of the buckets currents in order
     * @return String
     */
    @Override
    public String toString() {
        String bucaneer = "[";
        for (int i = 0; i < bucks.size(); i++) {
            bucaneer += bucks.get(i);
            if(i+1!=bucks.size()){
                bucaneer += ",";
            }
        }
        bucaneer += "]";
        return bucaneer;
    }
}
