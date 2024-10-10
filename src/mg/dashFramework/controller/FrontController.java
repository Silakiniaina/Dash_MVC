package mg.dashFramework.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;    
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mg.dashFramework.util.PackageUtils;
import mg.dashFramework.util.ReflectUtils;
import mg.dashFramework.annotation.Controller;
import mg.dashFramework.annotation.RestApi;
import mg.dashFramework.handler.exeption.PackageScanNotFoundException;
import mg.dashFramework.handler.url.Mapping;
import mg.dashFramework.util.ClassUtils;
import mg.dashFramework.handler.views.ModelView;

public class FrontController extends HttpServlet {
    HashMap<String, Mapping> URLMapping;
    private Exception error;
    private MySession mySession;

    private void processRequest(HttpServletRequest request, HttpServletResponse response, String verb)throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String requestURL = request.getRequestURI().substring(request.getContextPath().length()); 
        Mapping map = this.getURLMapping().get(requestURL);
        response.setContentType("text/json");

        if(this.getMySession() == null) this.setMySession(new MySession(request.getSession()));
        
        if(error != null){
            out.println(error.getMessage());
        }
        try {
            if(map != null){
                Object obj = ReflectUtils.executeRequestMethod(map,request);
                if(obj instanceof String){
                    out.println((String)obj);
                }else if(obj instanceof ModelView){
                    ModelView mv = (ModelView)obj;
                    HashMap<String, Object> data = mv.getData();
                    if(map.getMethod().isAnnotationPresent(RestApi.class)){
                        out.println(new Gson().toJson(data));
                    }else{
                        for(String key : data.keySet()){
                            request.setAttribute(key, data.get(key));
                        }
                        request.getRequestDispatcher(mv.getUrl()).forward(request, response);
                    }
                }else{
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unkown return type");
                }
            }else{
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Url : "+requestURL +" not found");
            }
        }catch (Exception e) {
            out.println(e.getMessage());
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