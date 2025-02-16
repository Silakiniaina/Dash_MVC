package mg.dash.mvc.annotation;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Auth {

    String[] roles() default {};  
    boolean required() default true; 
}
