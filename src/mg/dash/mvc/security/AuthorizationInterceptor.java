package mg.dash.mvc.security;

import jakarta.servlet.http.HttpServletRequest;
import mg.dash.mvc.annotation.Auth;
import mg.dash.mvc.handler.exeption.AuthorizationException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public class AuthorizationInterceptor {
    public static void checkAuthorization(Method method, HttpServletRequest request) throws AuthorizationException {
        Auth auth = method.getAnnotation(Auth.class);
        if (auth == null) {
            return; 
        }

        Object user = request.getSession().getAttribute("user");
        if (user == null && auth.required()) {
            throw new AuthorizationException("Authentication required");
        }

        if (auth.roles().length > 0) {
            @SuppressWarnings("unchecked")
            Set<String> userRoles = (Set<String>) request.getSession().getAttribute("userRoles");
            
            if (userRoles == null || !hasAnyRole(userRoles, auth.roles())) {
                throw new AuthorizationException("Insufficient permissions");
            }
        }
    }

    private static boolean hasAnyRole(Set<String> userRoles, String[] requiredRoles) {
        return Arrays.stream(requiredRoles)
                    .anyMatch(userRoles::contains);
    }
}