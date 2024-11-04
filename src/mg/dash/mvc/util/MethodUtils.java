package mg.dash.mvc.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import mg.dash.mvc.annotation.RequestParam;
import mg.dash.mvc.handler.url.Mapping;

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
    public static Object executeRequestMethod(Mapping mapping, HttpServletRequest request,String verb) throws Exception {
        List<Object> objects = new ArrayList<>();
        Class<?> objClass = Class.forName(mapping.getClassName());
        Method method = mapping.getMethodByVerb(verb);
        int paramNumber = method.getParameters().length;
        int countAnnotation = 0;
        for(Parameter parameter : method.getParameters()) {
            Object object = "";
            if (parameter.isAnnotationPresent(RequestParam.class)) {
                object = request.getParameter(parameter.getAnnotation(RequestParam.class).value());
                countAnnotation++;
            }
            objects.add(object);
        }
        if(countAnnotation != paramNumber){
            int n = paramNumber-countAnnotation;
            throw new Exception("ETU002611 : Tous les parametres de la fonction "+method.getName()+" doivent etre anotter , il y : "+n+" parametre sans annotation");
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