package mg.dash.mvc.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import mg.dash.mvc.annotation.Email;
import mg.dash.mvc.annotation.Required;

public class ObjectUtils {
private static void setObjectAttributesValues(Object instance, String attributeName, String value)
            throws Exception {
        Field field = instance.getClass().getDeclaredField(attributeName);
        Object fieldValue = castObject(value, field.getType());
        String setterMethodName = ReflectUtils.getSetterMethod(attributeName);
        Method method = instance.getClass().getMethod(setterMethodName, field.getType());
        method.invoke(instance, fieldValue);
    }

    public static Object getParameterInstance(Class<?> classType, String annotationValue, HttpServletRequest request, HashMap<String, String> errors)
            throws Exception {
        // Create a map to store all field values before setting them
        Map<String, String> fieldValues = new HashMap<>();
        String className = annotationValue.split("\\.")[0];
        String regex = className + ".*";

        // First pass: collect all field values and validate
        Enumeration<String> requestParams = request.getParameterNames();
        while (requestParams.hasMoreElements()) {
            String requestParamName = requestParams.nextElement();
            String[] splitParamName = requestParamName.split("\\.");
            if (requestParamName.matches(regex) && splitParamName.length >= 2) {
                String attributeName = splitParamName[1];
                String value = request.getParameter(requestParamName);
                fieldValues.put(attributeName, value);
                
                // Validate the field
                try {
                    Field field = classType.getDeclaredField(attributeName);
                    validateField(field, value, errors);
                } catch (NoSuchFieldException e) {
                    errors.put(attributeName, "Field does not exist in class: " + attributeName);
                }
            }
        }

        // Only create and populate instance if there are no errors
        if (errors.isEmpty()) {
            Object instance = classType.getConstructor().newInstance();
            
            // Second pass: set all validated values
            for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
                setObjectAttributesValues(instance, entry.getKey(), entry.getValue());
            }
            return instance;
        }
        
        return null;
    }

    public static void validateField(Field f, String value, HashMap<String, String> errors) {
        try {
            if (StringUtils.isNull(value) && f.isAnnotationPresent(Required.class)) {
                errors.put(f.getName(), "value is required for input : " + f.getName());
            } else if (f.isAnnotationPresent(mg.dash.mvc.annotation.Numeric.class) && !StringUtils.isNumeric(value)) {
                errors.put(f.getName(), "value must be numeric for input " + f.getName());
            } else if (!StringUtils.isDate(value) && f.isAnnotationPresent(mg.dash.mvc.annotation.Date.class)) {
                errors.put(f.getName(), "value must be date for input " + f.getName());
            } else if (!StringUtils.isEmail(value) && f.isAnnotationPresent(Email.class)) {
                errors.put(f.getName(), "value must be email for input " + f.getName());
            }
        } catch (Exception e) {
            errors.put(f.getName(), "Validation error for field: " + f.getName() + " - " + e.getMessage());
        }
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
        } else if (clazz == Long.TYPE) {
            return Long.parseLong(value);
        } else if (clazz == Short.TYPE) {
            return Short.parseShort(value);
        } else if (clazz == Byte.TYPE) {
            return Byte.parseByte(value);
        } else if (clazz == Boolean.TYPE) {
            return Boolean.parseBoolean(value);
        } else if (clazz == Character.TYPE) {
            return value.charAt(0);
        } else if (clazz == String.class) {
            return value;
        } else if (clazz == Date.class) {
            return Date.valueOf(value);
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
}
