package mg.dash.mvc.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Auth {
    String[] roles() default {};  
    boolean required() default true; 
}
