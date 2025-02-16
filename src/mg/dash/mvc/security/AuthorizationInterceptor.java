package mg.dash.mvc.security;

import jakarta.servlet.http.HttpServletRequest;
import mg.dash.mvc.annotation.Auth;
import mg.dash.mvc.controller.MySession;
import mg.dash.mvc.handler.exeption.AuthorizationException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public class AuthorizationInterceptor {
public static void checkAuthorization(Method method, MySession mySession) throws AuthorizationException {
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