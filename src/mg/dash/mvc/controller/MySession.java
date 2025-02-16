package mg.dash.mvc.controller;

import java.util.Enumeration;

import jakarta.servlet.http.HttpSession;

public class MySession {
    HttpSession session;

    /* Constructor */
    public MySession(HttpSession s){
        this.setSession(s);
    }

    /* Authorization management */
    public void setUser(Object user) {
        session.setAttribute("user", user);
    }

    public Object getUser() {
        return session.getAttribute("user");
    }

    public boolean isAuthenticated() {
        return getUser() != null;
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
