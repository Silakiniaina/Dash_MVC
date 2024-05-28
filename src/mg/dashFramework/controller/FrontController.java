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

public class FrontController extends HttpServlet {
    private ArrayList<Class<?>> controllerClasses;
    private boolean checked = false;

    // Class methods
    private void initVariables() throws ClassNotFoundException, IOException {
        String packageName = this.getInitParameter("package_name");
        ArrayList<Class<?>> classes = (ArrayList<Class<?>>)PackageUtils.getClassesWithAnnotation(packageName, Controller.class);
        setControllerClasses(classes);
        setChecked(true);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        try {
            out.println("<h1>Welcome to Dash MVC Framework </h1>");
            out.println("<p>Your URL : <a href = \' \'> " + request.getRequestURI() + "</a></b>");
            out.println("<b> Here are the list of the controllers : </b>");

            if (!isChecked()) {
                initVariables();
            }

            ArrayList<Class<?>> classes = getControllerClasses();

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
            initVariables();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getters and setters
    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public ArrayList<Class<?>> getControllerClasses() {
        return controllerClasses;
    }

    public void setControllerClasses(ArrayList<Class<?>> controllerClasses) {
        this.controllerClasses = controllerClasses;
    }
}