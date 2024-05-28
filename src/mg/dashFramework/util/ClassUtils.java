package mg.dashFramework.util;

import java.lang.reflect.Method;

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
}