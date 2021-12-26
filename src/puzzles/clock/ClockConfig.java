package puzzles.clock;

import puzzles.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClockConfig implements Configuration {

    private int hours;
    private int current;
    private int end;

    /**
     * The Constructor for Clock, initializing everything
     * @param hours: the amount of hours the clock has
     * @param start: the starting config where the clock is
     * @param end: the end config where the clock needs to be
     */
    public ClockConfig(int hours,int start,int end){
        this.hours = hours;
        if(start <= 0) {
            this.current = hours;
        }
        else if(start >= hours){
            this.current = 1;
        }
        else{
            this.current = start;
        }
        this.end = end;
    }

    /**
     * Gets two successors, one hours ahead and behind but only returns one
     * if the two ways are equal in terms of length
     * @return A list of Configs
     */
    @Override
    public Collection<Configuration> getSuccessors() {
        List<Configuration> successors = new ArrayList<>();
        ClockConfig counter = new ClockConfig(hours, current-1, end);
        ClockConfig clockwise = new ClockConfig(hours, current+1, end);
        if(end-current == hours+current-end){
            successors.add(clockwise);
        }
        else {
            successors.add(clockwise);
            successors.add(counter);
        }
        return successors;
    }

    /**
     * Dont need isValid() since there are only two configs to get from one config
     * @return
     */
    @Override
    public boolean isValid() {
        return false;
    }

    /**
     * checks if the config is the end goal by comparing the hours of start and the end
     * @return Boolean true or false
     */
    @Override
    public boolean isGoal() {
        return this.equals(new ClockConfig(this.hours, end, end));
    }

    /**
     * gets the hours the clocks have
     * @return
     */
    public int getHours(){
        return this.hours;
    }

    /**
     * gets the current hour the config is on
     * @return
     */
    public int getCurrent(){
        return current;
    }

    /**
     * gets the ending hour
     * @return
     */
    public int getEnd() {
        return end;
    }

    /**
     * checks if is instance of clockConfig then checking the hashcode
     * @param other
     * @return
     */
    @Override
    public boolean equals(Object other){
        boolean result = false;
        if(other instanceof ClockConfig ){
            ClockConfig second = (ClockConfig) other;
            result = second.hashCode() == this.hashCode();
        }
        return result;
    }

    /**
     * returns the hours, current and end as a int because
     * the only way to match this is if you have the same hours
     * @return
     */
    @Override
    public int hashCode(){
        return this.hours+this.current+this.end;
    }
}
