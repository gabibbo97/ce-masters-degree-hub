import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleAnnotationWithParams {
	String stringParam() default "Default string parameter";
	String[] stringArrayParam();
}
