import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class Program {

  private static final int[] serverPorts = new int[] { 8841, 8842, 8843 };

  public static void main(String[] args) {
    MyLogger.getInstance().log("Starting program");

    // Create and start the servers
    MyLogger.getInstance().log("Program is starting servers");
    Arrays.stream(serverPorts).parallel().mapToObj(port -> {
      return new MyServer(port);
    }).map(myServer -> {
      return new Thread(myServer);
    }).forEach(serverThread -> serverThread.start());

    // Create and start the client
    MyLogger.getInstance().log("Program is starting the client");
    MyClient client = new MyClient(serverPorts);
    client.run();

  }
}
