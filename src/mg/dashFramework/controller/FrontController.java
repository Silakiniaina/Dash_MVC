package mg.dashFramework.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mg.dashFramework.util.PackageUtils;
import mg.dashFramework.util.ReflectUtils;
import mg.dashFramework.annotation.Controller;
import mg.dashFramework.handler.exeption.PackageScanNotFoundException;
import mg.dashFramework.handler.url.Mapping;
import mg.dashFramework.util.ClassUtils;
import mg.dashFramework.handler.views.ModelView;

public class FrontController extends HttpServlet {
    HashMap<String, Mapping> URLMapping;
    private Exception error;
    private MySession mySession;

    private void processRequest(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String requestURL = request.getRequestURI().substring(request.getContextPath().length() + 1); 
        Mapping map = this.getURLMapping().get(requestURL);
        response.setContentType("text/html");

        if(this.getMySession() == null) this.setMySession(new MySession(request.getSession()));
        
        if(error != null){
            out.println(error.getMessage());
        }
        try {
            if(map != null){
                Object obj = ReflectUtils.executeRequestMethod(map, request);
                if (obj instanceof String) {
                    out.println(obj.toString());
                }else if (obj instanceof ModelView){
                    ModelView modelView = ((ModelView)obj);
                    HashMap<String, Object> data = modelView.getData();
                    for (String key : data.keySet()) {
                        request.setAttribute(key, data.get(key));
                    }
                    request.getRequestDispatcher(modelView.getUrl()).forward(request, response);
                }else{
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unknown return type");
                }
            }else{ 
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Url not Found");
            }
        }catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    // Override methods
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
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
            HashMap<String, Mapping> mapping = ClassUtils.includeMethodHavingAnnotationGet(classes);
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