package mg.dash.mvc.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PackageUtils {
    public static List<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException {
        List<Class<?>> classes = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');

        URL resource = classLoader.getResource(path);

        if (resource == null) {
            return classes;
        }

        File packageDir = new File(resource.getFile().replace("%20", " "));

        for (File file : packageDir.listFiles()) {
            if (file.isDirectory()) {
                classes.addAll(PackageUtils.getClasses(packageName + "." + file.getName()));
            } else {
                String className = packageName + "." + FileUtils.getSimpleFileName(file.getName(), "class");
                classes.add(Class.forName(className));
            }
        }
        
        return classes;
    }

    public static List<Class<?>> getClassesWithAnnotation(String packageName, Class<? extends Annotation> annotationClass){
        ArrayList<Class<?>> result = new ArrayList<Class<?>>();
        try{
            List<Class<?>> classes = getClasses(packageName);
            for(Class<?> c : classes) {
                if (c.isAnnotationPresent(annotationClass)) {
                    result.add(c);
                }
            }
        }catch(Exception e){
        
        }
        return result;
    }
}
