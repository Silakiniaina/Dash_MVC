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

import mg.dashFramework.handler.url.Mapping;
import mg.dashFramework.util.ClassUtils;

public class FrontController extends HttpServlet {
    HashMap<String, Mapping> URLMapping;

    private void processRequest(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String requestURL = request.getRequestURI().substring(request.getContextPath().length() + 1); 
        Mapping map = this.getURLMapping().get(requestURL);
        try {
            out.println("<h1>Welcome to Dash MVC Framework </h1>");
            out.println("<p>Your URL : <a href = \' \'> " + request.getRequestURI() + "</a><br>");
            if(map != null){
                out.println("<b> Here is the method and class associated with your URL : </b><br>");
                out.println("<u>className </u> : "+map.getClassName() +"<br>");
                out.println("<u>methodName </u> : "+map.getMethodName()+"<br>");
                String str = (String)ClassUtils.invokeMethod(map.getClassName(),map.getMethodName());
                out.println("<p>Output of the method :<b>"+str+"<b> </p>");
            }else{ 
                out.println("<b>There is no method associated with the URL that you entered</b>");
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