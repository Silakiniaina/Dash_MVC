package mg.dash.mvc.util;

import jakarta.servlet.http.HttpServletRequest;

public class UrlUtils {
    
    public static String getOriginalUrl(HttpServletRequest request) {
        String originalUrl = (String) request.getAttribute("javax.servlet.forward.request_uri");
        if (originalUrl == null) {
            originalUrl = request.getHeader("Referer");
            if (originalUrl != null && originalUrl.startsWith("http://") || originalUrl.startsWith("https://")) {
                originalUrl = originalUrl.substring(originalUrl.indexOf(request.getContextPath()));
            }
        }
        if (originalUrl == null) {
            originalUrl = request.getRequestURI();
        }

        return originalUrl;
    }
}
