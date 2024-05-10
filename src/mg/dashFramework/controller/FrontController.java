package mg.dashFramework.controller;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet{
    
    /* Function where to control all the url entered */
    public void processRequest(HttpServletRequest request, HttpServletResponse response){
        try{
            PrintWriter out = response.getWriter();
            out.println("welcome to frontController");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /* Overriding doGet and doPost to point it to processRequest */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}