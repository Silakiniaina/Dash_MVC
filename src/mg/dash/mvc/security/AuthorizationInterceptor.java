package mg.dash.mvc.security;

import mg.dash.mvc.annotation.Auth;
import mg.dash.mvc.controller.MySession;
import mg.dash.mvc.handler.exeption.AuthorizationException;

import java.lang.reflect.Method;

public class AuthorizationInterceptor {

    private static void checkMethodAuthorization(Method method, MySession mySession) throws AuthorizationException {
        if(method.isAnnotationPresent(Auth.class)) {
            Auth auth = method.getAnnotation(Auth.class);
            if (auth == null) {
                return; // No authorization required
            }
    
            if (!mySession.isAuthenticated() && auth.required()) {
                throw new AuthorizationException("Authentication required");
            }
    
            if (auth.roles().length > 0 && !mySession.hasAnyRole(auth.roles())) {
                throw new AuthorizationException("Insufficient permissions");
            }
        }       
    }

    private static void checkClassAuthorization(Object o, MySession session) throws AuthorizationException {
        if(o.getClass().isAnnotationPresent(Auth.class)) {
            Auth auth = o.getClass().getAnnotation(Auth.class);
            if (auth == null) {
                return; 
            }
    
            if (!session.isAuthenticated() && auth.required()) {
                throw new AuthorizationException("Authentication required for this class");
            }
    
            if (auth.roles().length > 0 && !session.hasAnyRole(auth.roles())) {
                throw new AuthorizationException("Insufficient permissions for class : "+o.getClass().getName()+" , require : "+auth.roles()+" role to access to it");
            }
        }
    }

    public static void checkAuthorization(Object o, Method m,  MySession session) throws AuthorizationException {
        checkClassAuthorization(o, session);
        checkMethodAuthorization(m, session);
    }
}