package mg.dashFramework.util;

import java.util.ArrayList;
import java.util.HashMap;

import java.lang.reflect.Method;

import mg.dashFramework.annotation.Url;
import mg.dashFramework.handler.url.Mapping;
import mg.dashFramework.handler.url.VerbAction;

public class ClassUtils {

    /* Function to get all the methods in a class */
    public static ArrayList<Method> getListMethodsClass(@SuppressWarnings("rawtypes") Class c) {
        ArrayList<Method> result = new ArrayList<Method>();
        Method[] ls = c.getDeclaredMethods();
        for (int i = 0; i < ls.length; i++) {
            result.add(ls[i]);
        }
        return result;
    }

    /* function to get the HashMap of the method having an annotation */
    public static HashMap<String, Mapping> includeMethodHavingUrlAnnotation(ArrayList<Class<?>> listClasses)
            throws Exception {
        HashMap<String, Mapping> result = new HashMap<String, Mapping>();
        for (Class<?> c : listClasses) {
            ArrayList<Method> listMethods = ClassUtils.getListMethodsClass(c);
            for (Method m : listMethods) {
                String url = "";
                if (m.isAnnotationPresent(Url.class)) {
                    url = m.getAnnotation(Url.class).value();
                }

                VerbAction action = new VerbAction();
                action.setVerb("GET");
                action.setMethod(m);

                if (url != "" && result.containsKey(url)) {

                    if (!result.get(url).getListVerbActions().add(action))
                        throw new Exception("Duplicate VerbAction");
                } else if (url != "" && !result.containsKey(url)) {
                    Mapping map = new Mapping(c.getName());
                    map.getListVerbActions().add(action);
                    result.put(url,map);
                }
            }
        }
        return result;
    }

    /* function to get an instance of a class by his name */
    public static Object getInstance(String className) throws Exception {
        return Class.forName(className).getConstructor().newInstance();
    }

    /* function to invoke a method in a class */
    public static Object invokeMethod(String className, String methName) {
        Object result = null;
        try {
            Object o = ClassUtils.getInstance(className);
            if (o != null) {
                Method m = MethodUtils.getMethodByName(o, methName);
                result = m.invoke(o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}