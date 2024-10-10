package mg.dashFramework.handler.url;

import java.lang.reflect.Method;

public class Mapping{
    private String className;

    /* Constructor */
    public Mapping(String className){
        this.setClassName(className);
    }
    
    /* Getters */
    public String getClassName(){
        return this.className;
    }

    /* Setters */
    public void setClassName(String className){
        this.className = className;
    }

}