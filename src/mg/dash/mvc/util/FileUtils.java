package mg.dash.mvc.util;

import jakarta.servlet.http.Part;

public class FileUtils {
    public static String getSimpleFileName(String fileName, String extension) {
        return fileName.substring(0, (fileName.length() - extension.length()) - 1);
    }

    public static String getFileName(Part part) {
        if (part == null) {
            return null;
        }
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf("=") + 2, content.length() - 1);
            }
        }
        return null;
    }
}
