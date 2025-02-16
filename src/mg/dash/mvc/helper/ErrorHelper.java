package mg.dash.mvc.helper;

import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;

public class ErrorHelper {
    public static String getOldValue(HttpServletRequest request, String name) {
        Object value = request.getAttribute(name);
        return value != null ? value.toString() : "";
    }
    
    public static String getError(HashMap<String,String> errors, String field) {
        return errors != null && errors.containsKey(field) ? errors.get(field) : null;
    }
}
