package mg.dashFramework.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import mg.dashFramework.handler.url.Mapping;

import java.lang.annotation.Annotation;

public class MethodUtils{

    /* function to check if a method has an annotation */
    public static boolean methodHasAnnotation(Method m, Class<? extends Annotation> a){
        boolean result = false;
        if(m.getClass().isAnnotationPresent(a)){
            result = true;
        }
        return result;
    }

    /* function get a method by his name in a class */
    public static Method getMethodByName(Object o, String methName)throws Exception{
        return o.getClass().getMethod(methName);
    }
}