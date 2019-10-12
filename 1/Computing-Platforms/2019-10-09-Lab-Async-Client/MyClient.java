import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class MyClient implements Runnable {

  private final int[] serverPorts;

  public MyClient(int[] serverPorts) {
    this.serverPorts = serverPorts;
    MyLogger.getInstance().log("Client received a list of " + Integer.toString(serverPorts.length) + " ports");
  }

  @Override
  public void run() {
    MyLogger.getInstance().log("Client thread starting");

    try {

      Selector selector = Selector.open();
      MyLogger.getInstance().log("Client has created selector");

      for (int port : serverPorts) {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        SelectionKey key = socketChannel.register(selector, SelectionKey.OP_CONNECT);
        key.attach(socketChannel);
        MyLogger.getInstance()
            .log("Client socket for port " + Integer.toString(port) + " has registered for OP_CONNECT events");

        SocketAddress serverAddress = new InetSocketAddress(InetAddress.getLoopbackAddress(), port);
        socketChannel.connect(serverAddress);
        MyLogger.getInstance().log("Client socket for port " + Integer.toString(port) + " is connecting");
      }

      while (true) {
        int eventCount = selector.select(1000);
        if (eventCount == 0) {
          // No events to handle
          continue;
        }
        MyLogger.getInstance().log("Client selector returned " + Integer.toString(eventCount) + " events");

        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        Iterator<SelectionKey> selectedKeysIterator = selectedKeys.iterator();

        while (selectedKeysIterator.hasNext()) {
          SelectionKey selectionKey = selectedKeysIterator.next();

          if (!selectionKey.isValid()) {
            MyLogger.getInstance().log("Client skipping invalid selection key");
            continue;
          }

          SocketChannel socketChannel = (SocketChannel) selectionKey.attachment();
          int destinationPort = socketChannel.socket().getPort();
          MyLogger.getInstance().log("Client selection key is for socket on port " + Integer.toString(destinationPort));

          if (selectionKey.isConnectable()) {
            MyLogger.getInstance().log("Client received connectable event");

            if (socketChannel.isConnectionPending()) {
              MyLogger.getInstance().log("Client connection pending, requesting to complete connection");
              socketChannel.finishConnect();
            }

            if (socketChannel.isConnected()) {
              MyLogger.getInstance().log("Client connection successful, registering for OP_READABLE events");
              selectionKey.interestOps(SelectionKey.OP_READ);
            }

            if (!(socketChannel.isConnected() || socketChannel.isConnectionPending())) {
              MyLogger.getInstance().log("Client connection failed");
            }

          }

          if (selectionKey.isReadable()) {
            MyLogger.getInstance().log("Client received readable event");

            ByteBuffer responseBuffer = ByteBuffer.allocate(2048);
            int responseReadBytesCount = socketChannel.read(responseBuffer);

            if (responseReadBytesCount == -1) {
              MyLogger.getInstance().log("Client has finished reading stream");
            } else {
              MyLogger.getInstance()
                  .log("Client performed read of " + Integer.toString(responseReadBytesCount) + " bytes");

              String message = new String(responseBuffer.array(), 0, responseReadBytesCount);

              MyLogger.getInstance().log("Client has received: " + message);

              if (message.contains("*")) {
                MyLogger.getInstance().log("Client has received end of transmission message");
                selectionKey.cancel();
                socketChannel.close();
              }
            }
          }

          selectedKeysIterator.remove();
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
