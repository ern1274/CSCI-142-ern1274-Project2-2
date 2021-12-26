package puzzles.jam.model;

import java.io.FileNotFoundException;

public class JamClientData {
    private String file;
    public JamClientData(String file){
        this.file = file;
    }
    private void load(String file){
        this.file = file;
    }
    public String getFile(){
        return file;
    }
    public void setFile(String newFile){
        this.file = newFile;
    }
}
