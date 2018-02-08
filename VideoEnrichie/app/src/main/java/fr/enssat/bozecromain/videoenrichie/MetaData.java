package fr.enssat.bozecromain.videoenrichie;

/**
 * Created by romainbozec on 11/01/2018.
 */

public class MetaData {
    private int pos;
    private String title;
    private String url;

    public MetaData(String rTitle, int rPos, String rPath){
        this.pos = rPos;
        this.title = rTitle;
        this.url = rPath;
    }

    public int getPos(){
        return this.pos;
    }
    public String getUrl() { return this.url; }
    public String getTitle(){ return this.title; }
}
