package org.hhu.chatgpt.domain.model;

public class Betreuende {
    
    private String name;
    private String tag;

    public Betreuende(String name, String tag){
        this.name = name;
        this.tag = tag;
    }

    public String getName(){
        return name;
    }

    public String getTag(){
        return tag;
    }
}
