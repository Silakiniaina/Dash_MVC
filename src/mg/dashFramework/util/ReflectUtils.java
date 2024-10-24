package mg.dashFramework.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import mg.dashFramework.annotation.RequestParam;
import mg.dashFramework.handler.url.Mapping;

public class ReflectUtils {

    public static String getMethodName(String initial, String attributeName) {
        return initial + Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1);
    }

    public static String getSetterMethod(String attributeName) {
        return getMethodName("set", attributeName);
    }

    public static Object executeRequestMethod(Mapping mapping, HttpServletRequest request, String verb)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,InvocationTargetException, InstantiationException, ClassNotFoundException, NoSuchFieldException,Exception {
        List<Object> objects = new ArrayList<>();
        Class<?> objClass = Class.forName(mapping.getClassName());
        Method method = mapping.getMethodByVerb(verb);
        int paramNumber = method.getParameters().length;
        int countAnnotation = 0;
        for (Parameter parameter : method.getParameters()) {
            Class<?> clazz = parameter.getType();
            Object object = ObjectUtils.getDefaultValue(clazz);
            String strValue = null;
            if (ObjectUtils.isPrimitive(clazz)) {
                if (parameter.isAnnotationPresent(RequestParam.class)) {
                    strValue = request.getParameter(parameter.getAnnotation(RequestParam.class).value());
                    object = strValue != null ? ObjectUtils.castObject(strValue, clazz) : object;
                    countAnnotation++;
                } else {
                    String paramName = parameter.getName();
                    strValue = request.getParameter(paramName);
                    if (strValue != null) {
                        object = ObjectUtils.castObject(strValue, clazz);
                    }
                }
            } else {
                if (parameter.isAnnotationPresent(RequestParam.class)) {
                    String annotationValue = parameter.getAnnotation(RequestParam.class).value();
                    object = ObjectUtils.getParameterInstance(clazz, annotationValue, request);
                    countAnnotation++;
                }
            }
            objects.add(object);
        }
        if(countAnnotation != paramNumber){
            int n = paramNumber-countAnnotation;
            throw new Exception("ETU002611 : Tous les parametres de la fonction "+method.getName()+" doivent etre anotter , il y a: "+n+" parametre sans annotation");
        }
        return executeClassMethod(objClass, method.getName(), objects.toArray());
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

    public static Object executeMethod(Object object, String methodName, Object... args) throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method method = object.getClass().getMethod(methodName, getArgsClasses(args));
        return method.invoke(object, args);
    }

    public static Object executeClassMethod(Class<?> clazz, String methodName, Object... args)
            throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            InstantiationException {
        // Class<?>[] arguments = getArgsClasses(args);
        Object object = clazz.getConstructor().newInstance();
        return executeMethod(object, methodName, args);
    }
}
