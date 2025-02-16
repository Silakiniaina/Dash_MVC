package mg.dash.mvc.security;

import mg.dash.mvc.annotation.Auth;
import mg.dash.mvc.controller.MySession;
import mg.dash.mvc.handler.exeption.AuthorizationException;

import java.lang.reflect.Method;

public class AuthorizationInterceptor {
public static void checkAuthorization(Method method, MySession mySession) throws AuthorizationException {
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
}