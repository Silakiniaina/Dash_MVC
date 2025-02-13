package mg.dash.mvc.util;

import java.io.File;
import java.io.IOException;

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

    public static String uploadFile(Part part, String directory) throws IOException {
        if (part == null || directory == null || directory.isEmpty()) {
            throw new IllegalArgumentException("Part or directory cannot be null or empty.");
        }
        String fileName = getFileName(part);
        if (fileName == null || fileName.isEmpty()) {
            throw new IOException("Invalid file name.");
        }
        File uploadDir = new File(directory);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        String filePath = directory + File.separator + fileName;
        part.write(filePath);
        return filePath;
    }
}
