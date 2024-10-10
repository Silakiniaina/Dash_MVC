package mg.dashFramework.handler.url;

import java.util.Set;

public class Mapping{
    private String className;
    private Set<VerbAction> listVerbActions;

    /* Constructor */
    public Mapping(String className, Set<VerbAction> list){
        this.setClassName(className);
        this.setListVerbActions(list);
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