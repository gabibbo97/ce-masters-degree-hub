import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import logger.MyLogger;

public class MyServer {

	public static void main(String[] args) {
		try {
			final ServerSocketChannel serverSocket = ServerSocketChannel.open();
			serverSocket.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 8000));
			final MyThreadPool pool = new MyThreadPool();
			MyLogger.getInstance().log("Starting server");
			while (true) {
				final SocketChannel clientSocket = serverSocket.accept();
				pool.execute(new MyHandler(clientSocket));
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

}
