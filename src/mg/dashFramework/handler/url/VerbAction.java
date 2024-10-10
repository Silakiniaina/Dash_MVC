package mg.dashFramework.handler.url;

import java.lang.reflect.Method;

public class VerbAction {
    private Method method;
    private String verb;

    /* Constructor */
    public VerbAction(String verb, Method m){
        this.setVerb(verb);
        this.setMethod(m);
    }
    
    /* Getters  */
    public Method getMethod() {
        return method;
    }
    public String getVerb() {
        return verb;
    }
    
    /* Setters */
    public void setMethod(Method method) {
        this.method = method;
    }
    public void setVerb(String verb) {
        this.verb = verb;
    }
}
