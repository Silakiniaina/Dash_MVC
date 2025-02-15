package mg.dash.mvc.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

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

    public static String uploadFile(Part file, String baseUploadDir) throws IOException {
        Path uploadPath = Paths.get(System.getProperty("user.home"), "Dash upload", baseUploadDir).toAbsolutePath();
        Files.createDirectories(uploadPath);
        String originalFilename = getFileName(file);
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        Path filePath = uploadPath.resolve(uniqueFilename);
        file.write(filePath.toString());
        return filePath.toString();
    }
}
