package mg.dash.mvc.security;

import jakarta.servlet.http.HttpServletRequest;
import mg.dash.mvc.annotation.Auth;
import mg.dash.mvc.handler.exeption.AuthorizationException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public class AuthorizationInterceptor {

    private static boolean hasAnyRole(Set<String> userRoles, String[] requiredRoles) {
        return Arrays.stream(requiredRoles)
                    .anyMatch(userRoles::contains);
    }
}