package mg.dash.mvc.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import mg.dash.mvc.annotation.RequestParam;
import mg.dash.mvc.controller.MySession;
import mg.dash.mvc.handler.url.Mapping;

public class ReflectUtils {

    public static String getMethodName(String initial, String attributeName) {
        return initial + Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1);
    }

    public static String getSetterMethod(String attributeName) {
        return getMethodName("set", attributeName);
    }

    public static Object executeRequestMethod(Mapping mapping, HttpServletRequest request, String verb, HashMap<String, String> errors)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,InvocationTargetException, InstantiationException, ClassNotFoundException, NoSuchFieldException,Exception {
        List<Object> objects = new ArrayList<>();
        Class<?> objClass = Class.forName(mapping.getClassName());
        Method method = mapping.getMethodByVerb(verb);
        int paramNumber = method.getParameters().length;
        int countAnnotation = 0;
        for (Parameter parameter : method.getParameters()) {
            Class<?> clazz = parameter.getType();
            Object object = ObjectUtils.getDefaultValue(clazz);
            if (parameter.isAnnotationPresent(RequestParam.class)) {
                if (Part.class.isAssignableFrom(parameter.getType())) {
                    object = request.getPart(parameter.getAnnotation(RequestParam.class).value());
                } else if(parameter.getType().equals(MySession.class)) {
                    object = new MySession(request.getSession());
                } else {
                    String annotationValue = parameter.getAnnotation(RequestParam.class).value();
                    object = ObjectUtils.getParameterInstance(clazz, annotationValue, request, errors);
                }
                countAnnotation++;
            }
            objects.add(object);
        }
        return executeClassMethod(objClass, method.getName(), objects.toArray());
    }

    public static Class<?>[] getArgsClasses(Object... args) {
        Class<?>[] classes = new Class[args.length];
        int i = 0;
        for (Object object : args) {
            Class<?> actualClass = object.getClass();
            if (actualClass.getName().startsWith("org.apache.catalina")) {
                for (Class<?> iface : actualClass.getInterfaces()) {
                    if (iface.getName().startsWith("jakarta.servlet")) {
                        actualClass = iface;
                        break;
                    }
                }
            }
            
            classes[i] = actualClass;
            i++;
        }
        return classes;
    }

    // public static Object executeMethod(Object object, String methodName, Object... args) throws NoSuchMethodException,
    //         SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    //     Method method = object.getClass().getMethod(methodName, getArgsClasses(args));
    //     return method.invoke(object, args);
    // }

    public static Object executeMethod(Object object, String methodName, Object... args) throws NoSuchMethodException,
    SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        System.out.println("Method name: " + methodName);
        System.out.println("Args length: " + args.length);
        for (Object arg : args) {
            System.out.println("Arg class: " + (arg != null ? arg.getClass().getName() : "null"));
        }

        Class<?>[] argClasses = getArgsClasses(args);
        for (Class<?> cls : argClasses) {
            System.out.println("Arg class after processing: " + cls.getName());
        }

        Method method = object.getClass().getMethod(methodName, argClasses);
        return method.invoke(object, args);
    }

    public static Object executeClassMethod(Class<?> clazz, String methodName, Object... args)
            throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            InstantiationException {
        Object object = clazz.getConstructor().newInstance();
        return executeMethod(object, methodName, args);
    }
}
