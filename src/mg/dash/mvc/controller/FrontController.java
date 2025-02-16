package mg.dash.mvc.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.dash.mvc.annotation.Controller;
import mg.dash.mvc.annotation.RestApi;
import mg.dash.mvc.handler.exeption.*;
import mg.dash.mvc.handler.url.Mapping;
import mg.dash.mvc.handler.views.ModelView;
import mg.dash.mvc.security.AuthorizationInterceptor;
import mg.dash.mvc.util.ClassUtils;
import mg.dash.mvc.util.PackageUtils;
import mg.dash.mvc.util.ReflectUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@MultipartConfig
public class FrontController extends HttpServlet {
    private static final Gson gson = new Gson();
    private Map<String, Mapping> urlMapping;
    private Exception initializationError;
    private MySession mySession;

    @Override
    public void init() throws ServletException {
        try {
            initializeUrlMapping();
        } catch (Exception e) {
            initializationError = e;
        }
    }

    private void initializeUrlMapping() throws PackageScanNotFoundException, Exception {
        this.urlMapping = new HashMap<>();
        String packageName = getInitParameter("controller_dir");
        
        if (packageName == null) {
            throw new PackageScanNotFoundException("Controller directory not specified in initialization parameters");
        }
        
        List<Class<?>> controllerClasses = PackageUtils.getClassesWithAnnotation(packageName, Controller.class);
        this.urlMapping = ClassUtils.includeMethodHavingUrlAnnotation(controllerClasses);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp, "GET");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp, "POST");
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response, String httpMethod) 
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        
        // Check for initialization errors
        if (initializationError != null) {
            handleError(response, out, initializationError, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        // Initialize or get session
        if (mySession == null) {
            mySession = new MySession(request.getSession());
        }

        // Get mapping for requested URL
        String requestURL = extractRequestUrl(request);
        Mapping mapping = urlMapping.get(requestURL);
        
        if (mapping == null) {
            handleUrlNotFound(response, out, requestURL);
            return;
        }

        processMapping(request, response, out, mapping, httpMethod);
    }

    private String extractRequestUrl(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }

    private void processMapping(HttpServletRequest request, HttpServletResponse response, 
                PrintWriter out, Mapping mapping, String httpMethod) throws ServletException, IOException {
        HashMap<String, String> validationErrors = new HashMap<>();
        
        try {
            // Check authorization before executing the method
            Method method = mapping.getMethodByVerb(httpMethod);
            AuthorizationInterceptor.checkAuthorization(method, this.getMySession());
            
            Object result = ReflectUtils.executeRequestMethod(mapping, request, httpMethod, validationErrors);
            
            if (!validationErrors.isEmpty()) {
                handleValidationErrors(request, response, out, mapping, httpMethod, validationErrors);
                return;
            }
            
            handleSuccessResponse(request, response, out, mapping, httpMethod, result);
            
        } catch (AuthorizationException e) {
            handleAuthorizationError(response, out, e);
        } catch (Exception e) {
            handleError(response, out, e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


    private void handleValidationErrors(HttpServletRequest request, HttpServletResponse response, 
            PrintWriter out, Mapping mapping, String httpMethod, Map<String, String> errors) 
            throws ServletException, IOException {
        Method method = mapping.getMethodByVerb(httpMethod);
        
        if (method.isAnnotationPresent(RestApi.class)) {
            sendJsonResponse(response, out, errors);
            return;
        }
        
        preserveFormDataAndErrors(request, errors);
        redirectToGetView(request, response, mapping);
    }

    private void handleSuccessResponse(HttpServletRequest request, HttpServletResponse response, 
            PrintWriter out, Mapping mapping, String httpMethod, Object result) 
            throws ServletException, IOException, UnknownReturnTypeException {
        if (result == null) {
            throw new UnknownReturnTypeException("Response cannot be null");
        }

        if (result instanceof String) {
            out.println(result);
        } else if (result instanceof ModelView) {
            handleModelViewResponse(request, response, out, mapping, httpMethod, (ModelView) result);
        } else {
            throw new UnknownReturnTypeException("Response type must be String or ModelView");
        }
    }

    private void handleModelViewResponse(HttpServletRequest request, HttpServletResponse response, 
            PrintWriter out, Mapping mapping, String httpMethod, ModelView modelView) 
            throws ServletException, IOException {
        if (mapping.getMethodByVerb(httpMethod).isAnnotationPresent(RestApi.class)) {
            sendJsonResponse(response, out, modelView.getData());
            return;
        }

        for (Map.Entry<String, Object> entry : modelView.getData().entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }
        
        request.getRequestDispatcher(modelView.getUrl()).forward(request, response);
    }

    private void preserveFormDataAndErrors(HttpServletRequest request, Map<String, String> errors) {
        request.setAttribute("validationErrors", errors);
        
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            request.setAttribute(paramName, request.getParameter(paramName));
        }
    }

    private void redirectToGetView(HttpServletRequest request, HttpServletResponse response, 
            Mapping mapping) throws ServletException, IOException {
        try {
            Method getMethod = Optional.ofNullable(mapping.getMethodByVerb("GET"))
                .orElseThrow(() -> new ServletException("No GET method found for error redirection"));

            Object viewObj = ReflectUtils.executeRequestMethod(mapping, request, "GET", new HashMap<>());
            
            if (viewObj instanceof ModelView) {
                request.getRequestDispatcher(((ModelView) viewObj).getUrl()).forward(request, response);
            }
        } catch (Exception e) {
            handleError(response, response.getWriter(), e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleUrlNotFound(HttpServletResponse response, PrintWriter out, String requestURL) {
        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        ErrorPage.displayError(out, new UrlNotFoundException("URL: " + requestURL + " not found"), 404);
    }

    private void handleError(HttpServletResponse response, PrintWriter out, Exception error, int statusCode) {
        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(statusCode);
        ErrorPage.displayError(out, error, statusCode);
    }

    private void sendJsonResponse(HttpServletResponse response, PrintWriter out, Object data) {
        response.setContentType("application/json");
        out.println(gson.toJson(data));
    }

    private void handleAuthorizationError(HttpServletResponse response, PrintWriter out, AuthorizationException e) {
        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        ErrorPage.displayError(out, e, 403);
    }

    // Getters and setters
    public Map<String, Mapping> getURLMapping() {
        return this.urlMapping;
    }

    public MySession getMySession() {
        return this.mySession;
    }

    public void setURLMapping(Map<String, Mapping> urlMapping) {
        this.urlMapping = urlMapping;
    }

    public void setMySession(MySession session) {
        this.mySession = session;
    }
}