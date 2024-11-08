package mg.dash.mvc.handler.url;

import java.lang.reflect.Method;

public class VerbAction {
    private Method method;
    private String verb;

    /* Constructor */
    public VerbAction() {
    }

    public VerbAction(String verb, Method m) {
        this.setVerb(verb);
        this.setMethod(m);
    }

    /* Getters */
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

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj == null)
            result = true;

        if (obj instanceof VerbAction && this.getVerb().equals(((VerbAction) obj).getVerb())) {
            result = true;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return 2611;
    }

}
