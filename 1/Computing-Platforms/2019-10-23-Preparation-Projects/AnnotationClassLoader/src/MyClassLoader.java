import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SimpleAnnotation
@SimpleAnnotationWithParams(stringParam="test", stringArrayParam= {"test1","test2","test3"})
public class MyClassLoader {
	
	@SimpleAnnotation
	private Optional<File> classSearchPath = Optional.empty();
	
	@SimpleAnnotationWithParams(stringParam="test", stringArrayParam= {})
	
	public void setClassSearchPath(String classPath) {
		File classDir = new File(classPath);
		
		if (!classDir.exists()) {
			System.out.println("Could not find classPath");
			return;
		}
		
		if (!classDir.isDirectory()) {
			System.out.println("classPath is not a directory");
			return;
		}
		
		this.classSearchPath = Optional.of(classDir);
	}
	
	public List<String> listClassesInPath() {
		
		if (!this.classSearchPath.isPresent()) {
			System.out.println("Classpath is absent");
			return new ArrayList<>();
		}
		
		List<String> classList = new ArrayList<>();
		for (File dirEntry : this.classSearchPath.get().listFiles()) {
			if (!dirEntry.isFile()) {
				continue;
			}
			String fileName = dirEntry.getAbsoluteFile().getName();
			String extension = fileName.substring(fileName.lastIndexOf('.')).substring(1);
			if (extension.equals("class")) {
				classList.add(fileName.substring(0, fileName.indexOf('.')));
			}
		}
		
		return classList;
	}
	
	public void analyzeClass(String className) {
		
		if (!this.classSearchPath.isPresent()) {
			System.out.println("Classpath is absent");
			return;
		}
		
		try {
			URLClassLoader loader = new URLClassLoader(
				new URL[] {
					new URL("file://" + classSearchPath.get().getAbsolutePath())
				}
			);
			Class<?> loadedClass = loader.loadClass(className);
			MyClassAnalyzer.analyzeClass(loadedClass);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
