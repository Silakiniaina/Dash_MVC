package mg.dashFramework.handler.url;

import java.util.Set;
import java.util.HashSet;

public class Mapping{
    private String className;
    private Set<VerbAction> listVerbActions = new HashSet<>();

    /* Constructor */
    public Mapping(String className){
        this.setClassName(className);
    }
    
    /* Getters */
    public String getClassName(){
        return this.className;
    }
    public Set<VerbAction> getListVerbActions(){
        return this.listVerbActions;
    }

    /* Setters */
    public void setClassName(String className){
        this.className = className;
    }
    public void setListVerbActions(Set<VerbAction> ls){
        this.listVerbActions = ls;
    }

}