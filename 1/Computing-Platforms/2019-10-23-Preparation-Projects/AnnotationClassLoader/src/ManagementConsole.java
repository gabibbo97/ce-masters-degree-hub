import java.util.Scanner;

public class ManagementConsole implements Runnable {
	
	private final MyClassLoader classLoader = new MyClassLoader();
	
	private final static String[] helpText = new String[] {
			"ls: list classes",
			"classpath <cp>: set class search path",
			"analyze <classname>: print informations about a class"
	};

	@Override
	public void run() {
		Scanner inputScanner = new Scanner(System.in);
		while (true) {
			System.out.print("Input command: ");
			String command = inputScanner.nextLine();
			String[] commandComponents = command.split(" ");
			
			if (command.equals("ls")) {
				for (String className : this.classLoader.listClassesInPath()) {
					System.out.println(className);
				}
			} else if (command.equals("help")) {
				for (String help : this.helpText) {
					System.out.println(help);
				}			
			} else if (commandComponents.length == 2) {
				if (commandComponents[0].equals("classpath")) {
					this.classLoader.setClassSearchPath(commandComponents[1]);
				} else if (commandComponents[0].equals("analyze")) {
					this.classLoader.analyzeClass(commandComponents[1]);
				} else {
					System.out.println("Unknown command");
				}
			} else {
				System.out.println("Unknown command");
			}
		}
	}

}
