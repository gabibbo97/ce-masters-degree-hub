import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.net.Socket;

public class MyServer implements Runnable {

  private final int portNumber;

  public MyServer(int portNumber) {
    this.portNumber = portNumber;
  }

  private ServerSocket createServerSocket() {
    ServerSocket serverSocket = null;
    try {
      serverSocket = new ServerSocket();
      SocketAddress listeningAddress = new InetSocketAddress(InetAddress.getLoopbackAddress(), portNumber);
      serverSocket.bind(listeningAddress);
      MyLogger.getInstance().log("Server bound to port " + Integer.toString(portNumber));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return serverSocket;
  }

  @Override
  public void run() {
    // Istantiate the socket
    ServerSocket serverSocket = createServerSocket();
    if (serverSocket == null) {
      MyLogger.getInstance().log("Failed creating socket for server on port " + Integer.toString(portNumber));
      return;
    }

    // Handle connections
    MyLogger.getInstance().log("Starting main loop for server listening on port " + Integer.toString(portNumber));
    while (true) {
      try {
        Socket clientSocket = serverSocket.accept();
        MyLogger.getInstance().log("Server accepted connection on port " + Integer.toString(portNumber));

        new MyServer.HandlerThread(clientSocket, portNumber).run();

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static class HandlerThread implements Runnable {

    private final Socket socket;
    private final int portNumber;

    public HandlerThread(Socket socket, int portNumber) {
      this.socket = socket;
      this.portNumber = portNumber;
    }

    private void sleep(long ms) {
      try {
        MyLogger.getInstance().log("Handler thread for port " + Integer.toString(portNumber) + " starting sleep");
        Thread.sleep(ms);
        MyLogger.getInstance().log("Handler thread for port " + Integer.toString(portNumber) + " woke up");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Override
    public void run() {
      MyLogger.getInstance().log("Handler thread for port " + Integer.toString(portNumber) + " has been called");
      // Wait for a while
      sleep(1000);
      try {
        // Send messages
        OutputStream outputStream = socket.getOutputStream();
        for (int i = 0; i < 10; i++) {
          String message = "This is message " + Integer.toString(i + 1) + " out of " + Integer.toString(10)
              + System.lineSeparator();
          outputStream.write(message.getBytes());
          outputStream.flush();
          MyLogger.getInstance()
              .log("Handler thread for port " + Integer.toString(portNumber) + " has sent: " + message);
          sleep(250);

        }
        // Send final message
        String finalMessage = "*** ending transmission ***" + System.lineSeparator();
        outputStream.write(finalMessage.getBytes());
        outputStream.flush();
        MyLogger.getInstance()
            .log("Handler thread for port " + Integer.toString(portNumber) + " has sent: " + finalMessage);

        // Close connection
        socket.close();
      } catch (IOException e) {
        MyLogger.getInstance().log("Handler thread for port " + Integer.toString(portNumber) + " had an I/O failure");
        return;
      }
    }
  }
}
