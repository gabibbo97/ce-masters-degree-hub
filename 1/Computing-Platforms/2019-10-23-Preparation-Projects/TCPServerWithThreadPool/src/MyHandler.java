import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;

import logger.MyLogger;

public class MyHandler implements Runnable {

	private final SocketChannel clientSocket;

	public MyHandler(SocketChannel clientSocket) {
		super();
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {
		MyLogger.getInstance().log("Handling client connection");
		try {
			// Greet the client
			final ByteBuffer greeting = ByteBuffer.wrap("Hi there!\n".getBytes());
			clientSocket.write(greeting);

			while (true) {
				// Show the menu
				clientSocket.write(ByteBuffer.wrap("Menu:\n".getBytes()));
				clientSocket.write(ByteBuffer.wrap("  random      Get a random number\n".getBytes()));
				clientSocket.write(ByteBuffer.wrap("  echo <word> Get back <word>\n".getBytes()));
				clientSocket.write(ByteBuffer.wrap("  exit        Close connection\n".getBytes()));
				// Prompt the user
				clientSocket.write(ByteBuffer.wrap("Choose: ".getBytes()));
				// Wait for command
				ByteBuffer readBuffer = ByteBuffer.allocate(4);
				do {
					if (readBuffer.position() + 2 > readBuffer.capacity()) {
						readBuffer.flip();
						final ByteBuffer newBuffer = ByteBuffer.allocate(readBuffer.capacity() * 2).put(readBuffer);
						readBuffer = newBuffer;
						MyLogger.getInstance().log("Resized read buffer to " + Integer.toString(readBuffer.capacity()));
					}
					readBuffer.limit(readBuffer.position() + 1);
					final int readBytes = clientSocket.read(readBuffer);
					if (readBytes < 1) {
						MyLogger.getInstance().log("Short read");
						break;
					}
					final byte[] readBytesArray = readBuffer.array();
					if (readBytesArray.length > 0 && readBytesArray[readBuffer.limit() - 1] == '\n') {
						MyLogger.getInstance().log("Found newline character");
						break;
					}
				} while (true);
				String command = new String(readBuffer.array());
				if (command.indexOf('\n') == -1) {
					continue;
				}
				command = command.substring(0, command.indexOf('\n'));
				// Parse command
				if (command.equals("random")) {
					final Random rng = new Random();
					final int randInt = rng.nextInt(101);
					clientSocket.write(ByteBuffer.wrap(Integer.toString(randInt).getBytes()));
					clientSocket.write(ByteBuffer.wrap("\n".getBytes()));
				} else if (command.startsWith("echo")) {
					clientSocket.write(ByteBuffer.wrap(command.substring(5).getBytes()));
					clientSocket.write(ByteBuffer.wrap("\n".getBytes()));
				} else if (command.equals("exit")) {
					clientSocket.write(ByteBuffer.wrap("Goodbye\n".getBytes()));
					break;
				} else {
					clientSocket.write(ByteBuffer.wrap("Unknown command\n".getBytes()));
				}
			}
			clientSocket.close();
		} catch (final IOException e) {
			e.printStackTrace();
			return;
		}
		MyLogger.getInstance().log("Handled client connection");

	}

}
