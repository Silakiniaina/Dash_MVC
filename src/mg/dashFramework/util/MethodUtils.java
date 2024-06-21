package mg.dashFramework.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import mg.dashFramework.annotation.RequestParam;
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

    /* Function to execute the method with parameters */
    public static Object executeRequestMethod(Mapping mapping, HttpServletRequest request) throws Exception {
        List<Object> objects = new ArrayList<>();
        Class<?> objClass = Class.forName(mapping.getClassName());
        Method method = mapping.getMethod();
        for(Parameter parameter : method.getParameters()) {
            Object object = "";
            if (parameter.isAnnotationPresent(RequestParam.class)) {
                object = request.getParameter(parameter.getAnnotation(RequestParam.class).value());
            }
            objects.add(object);
        }
        return executeClassMethod(objClass, method.getName(), objects.toArray());
    }

    /* Function to execute a method */
    public static Object executeMethod(Object object, String methodName, Object... args) throws NoSuchMethodException,
        SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method method = object.getClass().getMethod(methodName, getArgsClasses(args));
        return method.invoke(object, args);
    }

    /* Function to execute a class method */
    public static Object executeClassMethod(Class<?> clazz, String methodName, Object... args)throws NoSuchMethodException,
        SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
        InstantiationException {
        Object object = clazz.getConstructor().newInstance();
        return executeMethod(object, methodName, args);
    }

    public static Class<?>[] getArgsClasses(Object... args) {
        Class<?>[] classes = new Class[args.length];
        int i = 0;

        for (Object object : args) {
            classes[i] = object.getClass();
            i++;
        }

        return classes;
    }
}