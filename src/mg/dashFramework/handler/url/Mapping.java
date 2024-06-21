package mg.dashFramework.handler.url;

import java.lang.reflect.Method;

public class Mapping{
    private String className;
    private Method meth;

    /* Constructor */
    public Mapping(String className, Method m){
        this.setClassName(className);
        this.setMethod(m);
    }
    
    /* Getters */
    public String getClassName(){
        return this.className;
    }
    public Method getMethod(){
        return this.meth;
    }

    /* Setters */
    public void setClassName(String className){
        this.className = className;
    }
    public void setMethod(Method m){
        this.meth = m;
    }

}