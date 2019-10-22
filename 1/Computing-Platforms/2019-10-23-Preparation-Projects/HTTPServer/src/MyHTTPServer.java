import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MyHTTPServer implements Runnable {

	@Override
	public void run() {
		final Executor executor = Executors.newFixedThreadPool(8);
		try {

			final ServerSocketChannel serverSocket = ServerSocketChannel.open();
			serverSocket.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 8000));

			System.out.println("Started listening with HTTPServer");

			while (true) {
				final SocketChannel clientSocket = serverSocket.accept();
				System.out.println("Accepted connection");

				final HTTPRouter router = new HTTPRouter(clientSocket);
				executor.execute(router);
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
