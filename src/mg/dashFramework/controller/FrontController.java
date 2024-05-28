package mg.dashFramework.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mg.dashFramework.util.PackageUtils;
import mg.dashFramework.annotation.Controller;
import mg.dashFramework.annotation.Get;

public class FrontController extends HttpServlet {
    HashMap<String, Mapping> URLMapping;

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        try {
            out.println("<h1>Welcome to Dash MVC Framework </h1>");
            out.println("<p>Your URL : <a href = \' \'> " + request.getRequestURI() + "</a></b>");
            out.println("<b> Here are the list of the controllers : </b>");
            
            for (Class<?> clazz : classes) {
                out.println("<li>" + clazz.getSimpleName() + "</li>");
            }
            out.println("</ul>");
        } catch (Exception e) {
            out.println(e);
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
        try {
            this.setURLMapping(new HashMap<String, Mapping>());
            String packageName = this.getInitParameter("package_name");
            ArrayList<Class<?>> classes = (ArrayList<Class<?>>)PackageUtils.getClassesWithAnnotation(packageName, Controller.class);

            foreach(Class c : classes){
                ArrayList<Method> listMethods = ClassUtils.getListMethodsClass(c);
                foreach(Method m : listMethods){
                    if(MethodUtils.methodHasAnnotation(m,Get.class)){
                        
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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