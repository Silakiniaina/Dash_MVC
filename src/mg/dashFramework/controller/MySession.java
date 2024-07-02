package mg.dashFramework.controller;

import java.util.Enumeration;

import jakarta.servlet.http.HttpSession;

public class MySession {
    HttpSession session;

    /* Constructor */
    public MySession(HttpSession s){
        this.setSession(s);
    }

    /* Basical methods  */
    public Object get(String key){
        return this.getSession().getAttribute(key);
    }

    public void put(String key, Object value){
        this.getSession().setAttribute(key, value);
    }

    public void delete(String key){
        this.getSession().removeAttribute(key);
    }

    public void clear(){
        Enumeration<String> keys = this.getSession().getAttributeNames();
        while (keys.hasMoreElements()) {
            this.delete(keys.nextElement());
        }
    }

    /* Setters */
    public void setSession(HttpSession s){
        this.session = s;
    }

    /* Getters */
    public HttpSession getSession(){
        return this.session;
    }
}
