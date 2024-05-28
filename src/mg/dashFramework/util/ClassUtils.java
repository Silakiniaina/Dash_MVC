package mg.dashFramework.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;

import mg.dashFramework.annotation.Get;
import mg.dashFramework.shared.Mapping;

public class ClassUtils{

    /* Function to get all the methods in a class */
    public static ArrayList<Method> getListMethodsClass(Class c){
        ArrayList<Method> result = new ArrayList<Method>();
        Method[] ls = c.getDeclaredMethods();
        for(int i=0; i<ls.length; i++){
            result.add(ls[i]);
        }
        return result;
    }


    /* function to get the HashMap of the method having an annotation */
    public static HashMap<String, Mapping> includeMethodHavingAnnotationGet(ArrayList<Class<?>> listClasses){
        HashMap<String, Mapping> result = new HashMap<String, Mapping>();
        for (Class<?> c : listClasses){
            ArrayList<Method> listMethods = ClassUtils.getListMethodsClass(c);
            for (Method m : listMethods){
                if(m.isAnnotationPresent(Get.class)){
                    String URL = m.getAnnotation(Get.class).value();
                    Mapping map = new Mapping(c.getName(), m.getName());
                    result.put(URL, map);
                }
            }
        }
        return result;
    }
}