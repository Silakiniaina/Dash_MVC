package mg.dash.mvc.handler.exeption;

public class AuthorizationException extends Exception {
    public AuthorizationException(String message) {
        super(message);
    }
}