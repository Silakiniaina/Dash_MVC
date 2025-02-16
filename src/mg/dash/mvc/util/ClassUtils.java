package mg.dash.mvc.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mg.dash.mvc.annotation.Post;
import mg.dash.mvc.annotation.Url;
import mg.dash.mvc.handler.url.Mapping;
import mg.dash.mvc.handler.url.VerbAction;

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
    public static HashMap<String, Mapping> includeMethodHavingUrlAnnotation(List<Class<?>> listClasses)
            throws Exception {
        HashMap<String, Mapping> result = new HashMap<String, Mapping>();
        for (Class<?> c : listClasses) {
            ArrayList<Method> listMethods = ClassUtils.getListMethodsClass(c);
            for (Method m : listMethods) {
                if (m.isAnnotationPresent(Url.class)) {
                    String url = m.getAnnotation(Url.class).value();
                    VerbAction action = new VerbAction();
                    action.setVerb("GET");
                    action.setMethod(m);
    
                    if(m.isAnnotationPresent(Post.class)){
                        action.setVerb("POST");
                    }
    
                    if (result.containsKey(url)) {
                        //throw new Exception("URL efa misy oooo");
                        if (!result.get(url).getListVerbActions().add(action)){
                            throw new Exception("Duplicate VerbAction for url : " + url);
                        }
                    } else if (!result.containsKey(url)) {
                        Mapping map = new Mapping(c.getName());
                        map.getListVerbActions().add(action);
                        result.put(url,map);
                    }
                }

            }
        }
        return result;
    }


    /* function to get an instance of a class by his name */
    public static Object getInstance(String className)throws Exception{
        return Class.forName(className).getConstructor().newInstance();
    }

    /* function to invoke a method in a class */
    public static Object invokeMethod(String className, String methName){
        Object result = null;
        try{
            Object o = ClassUtils.getInstance(className);
            if(o != null){
                Method m = MethodUtils.getMethodByName(o, methName);
                result = m.invoke(o);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
}