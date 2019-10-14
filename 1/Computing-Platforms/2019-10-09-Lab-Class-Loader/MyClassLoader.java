import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Scanner;

public class MyClassLoader {

  private static void describeMethod(Method method) {
    System.out.print("Method: " + method.getName() + "(");
    boolean isFirst = true;
    for (Parameter parameter : method.getParameters()) {
      if (isFirst) {
        isFirst = false;
      } else {
        System.out.print(", ");
      }
      System.out.print(parameter.getType().getName());
    }
    System.out.println(")");
  }

  private static void describeField(Field field) {
    System.out.println("Field: " + field.getType().getName() + " " + field.getName());
  }

  public static void main(String[] args) {

    System.out.print("Insert required class name: ");

    Scanner scanner = new Scanner(System.in);
    String requestedClassName = scanner.nextLine();

    try {
      Class<?> loadedClass = Class.forName(requestedClassName);

      if (loadedClass instanceof Class) {
        System.out.println("Object is a Class");
        Class<?> classIntrospection = (Class<?>) loadedClass;

        System.out.println("Class name: " + classIntrospection.getName());

        for (Class<?> interfaceClass : classIntrospection.getInterfaces()) {
          System.out.println("Implements: " + interfaceClass.getName());
        }

        System.out.println("--- FIELDS ---");
        for (Field field : classIntrospection.getDeclaredFields()) {
          describeField(field);
        }

        System.out.println("--- METHODS ---");
        for (Method method : classIntrospection.getDeclaredMethods()) {
          describeMethod(method);
        }
      }

      Object loadedClassInstance = loadedClass.newInstance();

      if (loadedClassInstance instanceof IntegerSumInterface) {
        System.out.println("Class does implement IntegerSumInterface!");
        IntegerSumInterface integerSummer = (IntegerSumInterface) loadedClassInstance;

        int testSumResult = integerSummer.add(2, 2);

        if (testSumResult == 4) {
          System.out.println("Class can add numbers correctly!");
        } else {
          System.out.println("Class is broken! 2+2 = " + Integer.toString(testSumResult));
        }

      } else {
        System.out.println("Class does not implement IntegerSumInterface!");

        System.out.println("Trying to find method in class");
        try {
          Method add = loadedClass.getMethod("add", Integer.TYPE, Integer.TYPE);
          try {
            int testAdditionResult = (int) add.invoke(loadedClassInstance, Integer.valueOf(2), Integer.valueOf(2));

            if (testAdditionResult == 4) {
              System.out.println("Class did implement correctly IntegerSumInterface!");
            } else {
              System.out.println("Class does not implement correctly IntegerSumInterface!");
            }
          } catch (InvocationTargetException e) {
            e.printStackTrace();
          }

        } catch (NoSuchMethodException e) {
          System.out.println("Method unavailable");
        }

      }

    } catch (ClassNotFoundException e) {

      e.printStackTrace();
      System.out.println("Class loading failed!");

    } catch (InstantiationException e) {
      e.printStackTrace();
      System.out.println("Class instantiation failed!");
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      System.out.println("Class constructor is not available!");
    }

    scanner.close();

  }
}
