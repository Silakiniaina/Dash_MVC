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


    /* function to get the HashMap of the method having an annotation */
    public static void includeMethodHavingAnnotation(ArrayList<Class> listClasses, Class<? extends Annotation> a, HashMap<String, Mapping> mapping){
        foreach(Class c : listClasses){
            ArrayList<Method> listMethods = ClassUtils.getListMethodsClass(c);
            foreach(Method m : listMethods){
                if(MethodUtils.methodHasAnnotation(m, a)){
                    String URL = m.getAnnotation(Get.class).value;
                    Mapping map = new Mapping(c.getName(), m.getName());
                    mapping.put(URL, map);
                }
            }
        }
    }
}