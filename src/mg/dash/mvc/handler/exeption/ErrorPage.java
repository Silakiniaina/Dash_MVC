package mg.dash.mvc.handler.exeption;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorPage {

    /* -------------------------- displaying the error -------------------------- */
    public static void displayError(PrintWriter out, Exception e) {
        StringBuilder errorContent = new StringBuilder();
        
        // Set content type and headers (if using HttpServletResponse)
        // response.setContentType("text/html;charset=UTF-8");
        
        // Add HTML header with enhanced styling
        errorContent.append("<!DOCTYPE html>\n")
            .append("<html>\n")
            .append("<head>\n")
            .append("    <title>Error Page</title>\n")
            .append("    <style>\n")
            .append("        body {\n")
            .append("            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n")
            .append("            margin: 0;\n")
            .append("            padding: 20px;\n")
            .append("            background-color: #f5f5f5;\n")
            .append("            line-height: 1.6;\n")
            .append("        }\n")
            .append("        .error-container {\n")
            .append("            max-width: 1000px;\n")
            .append("            margin: 40px auto;\n")
            .append("            background: white;\n")
            .append("            border-radius: 8px;\n")
            .append("            box-shadow: 0 2px 4px rgba(0,0,0,0.1);\n")
            .append("            padding: 30px;\n")
            .append("        }\n")
            .append("        .error-title {\n")
            .append("            color: #e74c3c;\n")
            .append("            font-size: 24px;\n")
            .append("            margin: 0 0 20px 0;\n")
            .append("            padding-bottom: 10px;\n")
            .append("            border-bottom: 2px solid #e74c3c;\n")
            .append("        }\n")
            .append("        .error-info {\n")
            .append("            background-color: #fff8f8;\n")
            .append("            border-left: 4px solid #e74c3c;\n")
            .append("            padding: 15px;\n")
            .append("            margin: 20px 0;\n")
            .append("        }\n")
            .append("        .stack-trace {\n")
            .append("            background-color: #2d3436;\n")
            .append("            color: #fff;\n")
            .append("            padding: 20px;\n")
            .append("            border-radius: 6px;\n")
            .append("            overflow-x: auto;\n")
            .append("            margin-top: 20px;\n")
            .append("        }\n")
            .append("        .stack-trace pre {\n")
            .append("            margin: 0;\n")
            .append("            font-family: 'Consolas', monospace;\n")
            .append("            font-size: 14px;\n")
            .append("            white-space: pre-wrap;\n")
            .append("            word-wrap: break-word;\n")
            .append("        }\n")
            .append("        .label {\n")
            .append("            font-weight: bold;\n")
            .append("            color: #2c3e50;\n")
            .append("            margin-right: 10px;\n")
            .append("        }\n")
            .append("        .timestamp {\n")
            .append("            color: #7f8c8d;\n")
            .append("            font-size: 14px;\n")
            .append("            margin-bottom: 20px;\n")
            .append("        }\n")
            .append("    </style>\n")
            .append("</head>\n")
            .append("<body>\n");
    
        // Add error message content with timestamp
        errorContent.append("    <div class='error-container'>\n")
            .append("        <h1 class='error-title'>⚠️ Application Error</h1>\n")
            .append("        <div class='timestamp'>")
            .append("            Timestamp: ").append(new java.util.Date()).append("\n")
            .append("        </div>\n")
            .append("        <div class='error-info'>\n")
            .append("            <p><span class='label'>Error Type:</span>").append(e.getClass().getName()).append("</p>\n")
            .append("            <p><span class='label'>Error Message:</span>").append(e.getMessage() != null ? e.getMessage() : "No message available").append("</p>\n")
            .append("        </div>\n")
            .append("        <div class='stack-trace'>\n")
            .append("            <h3 style='color: #dfe6e9; margin-top: 0;'>Stack Trace:</h3>\n")
            .append("            <pre>");
    
        // Add stack trace
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        errorContent.append(sw.toString().replace("<", "&lt;").replace(">", "&gt;"));
    
        // Close HTML tags
        errorContent.append("            </pre>\n")
            .append("        </div>\n")
            .append("    </div>\n")
            .append("</body>\n")
            .append("</html>");
    
        // Write to PrintWriter
        out.println(errorContent.toString());
    }
}
