package mg.dashFramework.util;

public class MethodUtils{

    /* function to check if a method has an annotation */
    public static boolean methodHasAnnotation(Method m, Annotation a){
        boolean result = false;
        if(m.getClass.isAnnotationPresent(a)){
            result = true;
        }
        return result;
    }
}