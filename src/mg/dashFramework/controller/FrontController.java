package mg.dashFramework.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import java.lang.reflect.Method;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mg.dashFramework.util.PackageUtils;
import mg.dashFramework.annotation.Controller;
import mg.dashFramework.annotation.Get;
import mg.dashFramework.handler.exeption.UnknownReturnTypeException;
import mg.dashFramework.handler.exeption.UrlNotFoundException;
import mg.dashFramework.handler.url.Mapping;
import mg.dashFramework.util.ClassUtils;
import mg.dashFramework.handler.views.ModelView;

public class FrontController extends HttpServlet {
    HashMap<String, Mapping> URLMapping;

    private void processRequest(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String requestURL = request.getRequestURI().substring(request.getContextPath().length() + 1); 
        Mapping map = this.getURLMapping().get(requestURL);
        try {
            if(map != null){
                Object obj = ClassUtils.invokeMethod(map.getClassName(),map.getMethodName());
                if (obj instanceof String) {
                    out.println(obj.toString());
                } else if (obj instanceof ModelView) {
                    ModelView modelView = ((ModelView)obj);
                    HashMap<String, Object> data = modelView.getData();

                    for (String key : data.keySet()) {
                        request.setAttribute(key, data.get(key));
                    }
                    request.getRequestDispatcher(modelView.getUrl()).forward(request, response);
                } else {
                    throw new UnknownReturnTypeException();
                }
            }else{ 
                throw new UrlNotFoundException();
            }
        } catch (Exception e) {
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
        ArrayList<Class<?>> classes = (ArrayList<Class<?>>)PackageUtils.getClassesWithAnnotation(packageName, Controller.class);
        HashMap<String, Mapping> mapping = ClassUtils.includeMethodHavingAnnotationGet(classes);
        this.setURLMapping(mapping);
    }

    /* Getters */
    public HashMap<String, Mapping> getURLMapping(){
        return this.URLMapping;
    }

    /* Setters */
    public void setURLMapping(HashMap<String, Mapping> u){
        this.URLMapping = u;
    }
}