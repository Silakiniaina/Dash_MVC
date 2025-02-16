package mg.dash.mvc.util;

import java.lang.reflect.Field;
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
    throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
    InvocationTargetException, InstantiationException, ClassNotFoundException, NoSuchFieldException, Exception {

        List<Object> objects = new ArrayList<>();
        Class<?> objClass = Class.forName(mapping.getClassName());
        Method method = mapping.getMethodByVerb(verb);
        int paramNumber = method.getParameters().length;
        int countAnnotation = 0;

        // First pass: validate all @RequestParam parameters
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(RequestParam.class)) {
                Class<?> clazz = parameter.getType();
                
                // Skip validation for special types (Part and MySession)
                if (!Part.class.isAssignableFrom(clazz) && !clazz.equals(MySession.class)) {
                    String annotationValue = parameter.getAnnotation(RequestParam.class).value();
                    // This will only validate and collect errors
                    Object validationCheck = ObjectUtils.getParameterInstance(clazz, annotationValue, request, errors);
                    
                    // If validation failed and it's a required parameter, we should stop
                    if (validationCheck == null) {
                        return null; // Or throw an exception, depending on your needs
                    }
                }
            }
        }

        // If there are validation errors, return null or throw an exception
        if (!errors.isEmpty()) {
            return null; // Or throw new ValidationException(errors)
        }

        // Second pass: actually create and populate the objects
        for (Parameter parameter : method.getParameters()) {
            Class<?> clazz = parameter.getType();
            Object object = ObjectUtils.getDefaultValue(clazz);
            
            if (parameter.isAnnotationPresent(RequestParam.class)) {
                RequestParam annotation = parameter.getAnnotation(RequestParam.class);
                
                if (Part.class.isAssignableFrom(parameter.getType())) {
                    object = request.getPart(annotation.value());
                } else if (parameter.getType().equals(MySession.class)) {
                    object = new MySession(request.getSession());
                } else {
                    String annotationValue = annotation.value();
                    object = ObjectUtils.getParameterInstance(clazz, annotationValue, request, errors);
                    
                    // This should not happen as we've already validated, but just in case
                    if (object == null) {
                        throw new IllegalStateException("Required parameter failed validation: " + parameter.getName());
                    }
                }
                countAnnotation++;
            }
            objects.add(object);
        }

        // Verify that we processed the expected number of parameters
        if (countAnnotation != paramNumber) {
            throw new IllegalArgumentException("Method parameters and annotations count mismatch");
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

    public static void injectMySession(Object controller, HttpServletRequest request) throws Exception{
        try {
            Field[] fields = controller.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getType().equals(MySession.class)) {
                    field.setAccessible(true);
                    MySession sessionInstance = new MySession(request.getSession());
                    field.set(controller, sessionInstance);
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
