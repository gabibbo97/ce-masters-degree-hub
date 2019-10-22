import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;

public class MyClassAnalyzer {
	
	private static void analyzeAnnotation(Annotation annotation) {
		System.out.println(String.format("Annotation: %s", annotation.getClass().getSimpleName()));
		
		if (annotation instanceof SimpleAnnotation) {
			System.out.println("SimpleAnnotation");
		}
		
		if (annotation instanceof SimpleAnnotationWithParams) {
			
			SimpleAnnotationWithParams simpleAnnotation = (SimpleAnnotationWithParams) annotation;
			
			System.out.println("SimpleAnnotationWithParams");
			System.out.println("SimpleAnnotationWithParams stringParam: " + simpleAnnotation.stringParam());
			for (String stringArrayLine : simpleAnnotation.stringArrayParam()) {
				System.out.println("SimpleAnnotationWithParams stringArrayParam: " + stringArrayLine);
			}
		}
	}
	
	public static void analyzeClass (Class<?> loadedClass) {
		// Class
		System.out.println(String.format("Class name: %s",loadedClass.getSimpleName()));
		Arrays.stream(loadedClass.getAnnotations()).forEach(MyClassAnalyzer::analyzeAnnotation);
		// Fields
		for (Field field : loadedClass.getDeclaredFields()) {
			
			Arrays.stream(field.getAnnotations()).forEach(MyClassAnalyzer::analyzeAnnotation);
			
			System.out.print(Modifier.toString(field.getModifiers()));
			System.out.print(" ");
			System.out.print(field.getType().getSimpleName());
			System.out.print(" ");
			System.out.print(field.getName());
			System.out.println();
		}
		// Constructors
		for (Constructor<?> constructor : loadedClass.getDeclaredConstructors()) {
			
			Arrays.stream(constructor.getAnnotations()).forEach(MyClassAnalyzer::analyzeAnnotation);
			
			System.out.print("Constructor: ");
			System.out.print(constructor.getName());
			System.out.print(" (");
			
			for (Parameter parameter : constructor.getParameters()) {
				
				Arrays.stream(parameter.getAnnotations()).forEach(MyClassAnalyzer::analyzeAnnotation);
				
				System.out.print(parameter.getType().getSimpleName());
				System.out.print(" ");
				System.out.print(parameter.getName());
				System.out.print(", ");
			}
			
			System.out.println(")");
			
		}
		// Methods
		for (Method method : loadedClass.getDeclaredMethods()) {
			
			Arrays.stream(method.getAnnotations()).forEach(MyClassAnalyzer::analyzeAnnotation);
			
			System.out.print("Method: ");
			System.out.print(Modifier.toString(method.getModifiers()));
			System.out.print(" ");
			System.out.print(method.getReturnType().getSimpleName());
			System.out.print(" ");
			System.out.print(method.getName());
			System.out.print(" (");
			
			for (Parameter parameter : method.getParameters()) {
				
				Arrays.stream(parameter.getAnnotations()).forEach(MyClassAnalyzer::analyzeAnnotation);
				
				System.out.print(parameter.getType().getSimpleName());
				System.out.print(" ");
				System.out.print(parameter.getName());
				System.out.print(", ");
			}
			
			System.out.println(")");
			
		}
	}
}
