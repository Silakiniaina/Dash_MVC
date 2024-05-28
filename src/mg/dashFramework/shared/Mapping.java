package mg.dashFramework.shared;

public class Mapping{
    private String className;
    private String methodName;

    /* Constructor */
    public Mapping(String className, String methodName){
        this.setClassName(className);
        this.setMethodName(methodName);
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