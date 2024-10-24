package mg.dash.mvc.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;    
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.dash.mvc.annotation.Controller;
import mg.dash.mvc.annotation.RestApi;
import mg.dash.mvc.handler.exeption.PackageScanNotFoundException;
import mg.dash.mvc.handler.exeption.UrlNotFoundException;
import mg.dash.mvc.handler.exeption.ErrorPage;
import mg.dash.mvc.handler.url.Mapping;
import mg.dash.mvc.handler.views.ModelView;
import mg.dash.mvc.util.ClassUtils;
import mg.dash.mvc.util.PackageUtils;
import mg.dash.mvc.util.ReflectUtils;

public class FrontController extends HttpServlet {
    HashMap<String, Mapping> URLMapping;
    private Exception error;
    private MySession mySession;

    private void processRequest(HttpServletRequest request, HttpServletResponse response, String verb) 
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String requestURL = request.getRequestURI().substring(request.getContextPath().length()); 
        Mapping map = this.getURLMapping().get(requestURL);
        response.setContentType("text/json");

        if(this.getMySession() == null) {
            this.setMySession(new MySession(request.getSession()));
        }
        
        if(error != null) {
            response.setContentType("text/html;charset=UTF-8");
            ErrorPage.displayError(out, error, 500);  
            return;
        }

        try {
            // Check for 404 error
            if(map == null) {
                response.setContentType("text/html;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                UrlNotFoundException notFoundException = new UrlNotFoundException("Resource not found: " + requestURL);
                ErrorPage.displayError(out, notFoundException, 404);
                return;
            }

            Object obj = ReflectUtils.executeRequestMethod(map, request, verb);
            
            // Check for 500 error
            if(!(obj instanceof String) && !(obj instanceof ModelView)) {
                response.setContentType("text/html;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                Exception invalidTypeException = new Exception("Response type must be either String or ModelView");
                ErrorPage.displayError(out, invalidTypeException, 500);
                return;
            }

            // Process valid responses...
            if(obj instanceof String) {
                out.println((String)obj);
            } else if(obj instanceof ModelView) {
                ModelView mv = (ModelView)obj;
                HashMap<String, Object> data = mv.getData();
                if(map.getMethodByVerb(verb).isAnnotationPresent(RestApi.class)) {
                    response.setContentType("text/json");
                    out.println(new Gson().toJson(data));
                } else {
                    for(String key : data.keySet()) {
                        request.setAttribute(key, data.get(key));
                    }
                    request.getRequestDispatcher(mv.getUrl()).forward(request, response);
                }
            }
        } catch (Exception e) {
            response.setContentType("text/html;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ErrorPage.displayError(out, e, 500);
        }
    }

    // Override methods
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String verb = "GET";
        processRequest(req, resp, verb);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String verb = "POST";
        processRequest(req, resp, verb);
    }

    @Override
    public void init() throws ServletException {
        this.setURLMapping(new HashMap<String, Mapping>());
        String packageName = this.getInitParameter("controller_dir");
        try{
            if(packageName == null ){
                throw new PackageScanNotFoundException();
            }
            ArrayList<Class<?>> classes = (ArrayList<Class<?>>)PackageUtils.getClassesWithAnnotation(packageName, Controller.class);
            HashMap<String, Mapping> mapping = ClassUtils.includeMethodHavingUrlAnnotation(classes);
            this.setURLMapping(mapping);
        }catch(Exception e){
            error = e;
        }
    }

    /* Getters */
    public HashMap<String, Mapping> getURLMapping(){
        return this.URLMapping;
    }
    public MySession getMySession(){
        return this.mySession;
    }

    /* Setters */
    public void setURLMapping(HashMap<String, Mapping> u){
        this.URLMapping = u;
    }
    public void setMySession(MySession s){
        this.mySession = s;
    }
}