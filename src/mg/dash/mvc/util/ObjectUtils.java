package mg.dash.mvc.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import mg.dash.mvc.annotation.Email;
import mg.dash.mvc.annotation.Required;
import mg.dash.mvc.annotation.Numeric;

public class ObjectUtils {
    private static void setObjectAttributesValues(Object instance, String attributeName, String value)
            throws NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Field field = instance.getClass().getDeclaredField(attributeName);

        Object fieldValue = castObject(value, field.getType());
        String setterMethodName = ReflectUtils.getSetterMethod(attributeName);
        Method method = instance.getClass().getMethod(setterMethodName, field.getType());
        method.invoke(instance, fieldValue);
    }

    public static Object getParameterInstance(Class<?> classType, String annotationValue, HttpServletRequest request)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        Object instance = classType.getConstructor().newInstance();
        Enumeration<String> requestParams = request.getParameterNames();
        String attributeName = null, className = null, requestParamName = null, regex = null;
        String[] splitParamName = null;
        className = annotationValue.split("\\.")[0];
        regex = className + ".*";       

        while (requestParams.hasMoreElements()) {
            requestParamName = requestParams.nextElement();
            splitParamName = requestParamName.split("\\.");
            if (requestParamName.matches(regex) && splitParamName.length >= 2) {
                attributeName = splitParamName[1];
                setObjectAttributesValues(instance, attributeName, request.getParameter(requestParamName));
            }
        }

        return instance;
    }

    public static Object castObject(String value, Class<?> clazz) {
        if (value == null) {
            return null;
        } else if (clazz == Integer.TYPE) {
            return Integer.parseInt(value);
        } else if (clazz == Double.TYPE) {
            return Double.parseDouble(value);
        } else if (clazz == Float.TYPE) {
            return Float.parseFloat(value);
        } else {
            return value;
        }
    }

    public static boolean isPrimitive(Class<?> clazz) {
        List<Class<?>> primitiveTypes = new ArrayList<>();
        primitiveTypes.add(Integer.TYPE);
        primitiveTypes.add(Double.TYPE);
        primitiveTypes.add(String.class);

        return primitiveTypes.contains(clazz);
    }

    public static Object getDefaultValue(Class<?> clazz) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        HashMap<Class<?>, Object> keyValues = new HashMap<>();
        keyValues.put(Integer.TYPE, 0);
        keyValues.put(Double.TYPE, 0.0);
        keyValues.put(String.class, "");
        keyValues.put(Date.class, null);
        if (Part.class.isAssignableFrom(clazz)) {
            keyValues.put(Part.class, null); 
        }
        return keyValues.get(clazz);
    }

    public static void validateField(Field f, String value)throws Exception{
        if (StringUtils.isNull(value)) {
            if (f.isAnnotationPresent(Required.class)) {
                throw new Exception("value required " + f.getName());
            }
        } else {
            if (f.isAnnotationPresent(mg.dash.mvc.annotation.Numeric.class)) {
                if (!StringUtils.isNumeric(value)) {
                    throw new Exception("value must be numeric " + f.getName());
                }
            } else if (f.isAnnotationPresent(mg.dash.mvc.annotation.Date.class)) {
                if (!StringUtils.isDate(value)) {
                    throw new Exception("value must be date " + f.getName());
                }
            } else if (f.isAnnotationPresent(Email.class)) {
                if (!StringUtils.isEmail(value)) {
                    throw new Exception("value must be email " + f.getName());
                }
            }
        }
    }
}
