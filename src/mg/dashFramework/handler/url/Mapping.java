package mg.dashFramework.handler.url;

import java.lang.reflect.Method;

public class Mapping{
    private String className;
    private String methodName;

    /* Constructor */
    public Mapping(String className, String methodName){
        this.setClassName(className);
        this.setMethodName(methodName);
    }

    /* Function to get the method instance in the mapping */
    public Method getMethod()throws Exception{
        return this.getClass().getDeclaredMethod(this.getMethodName());
    }
    
    /* Getters */
    public String getClassName(){
        return this.className;
    }
    public String getMethodName(){
        return this.methodName;
    }

    /* Setters */
    public void setClassName(String className){
        this.className = className;
    }
    public void setMethodName(String methodName){
        this.methodName = methodName;
    }

}