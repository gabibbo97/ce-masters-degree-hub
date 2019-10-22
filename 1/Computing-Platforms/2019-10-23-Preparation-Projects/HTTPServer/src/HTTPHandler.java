import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public abstract class HTTPHandler implements Runnable {

	protected static ByteBuffer getIncomingStringUpTo(SocketChannel socketChannel, String delimiter)
			throws IOException {
		final byte[] stringBytes = delimiter.getBytes();
		ByteBuffer buffer = ByteBuffer.allocate(stringBytes.length == 0 ? 8 : stringBytes.length);
		System.out.println("Started read up to delimiter");

		do {
			if (buffer.position() + 2 > buffer.capacity()) {
				final int newCapacity = buffer.capacity() * 2;
				System.out.println(String.format("Growing buffer to %d bytes", newCapacity));
				buffer.flip();
				final ByteBuffer newBuffer = ByteBuffer.allocate(newCapacity).put(buffer);
				buffer = newBuffer;
			}
			buffer.limit(buffer.position() + 1);

			if (socketChannel.read(buffer) < 0) {
				System.out.println("End of read");
				break;
			}

			if (buffer.position() >= stringBytes.length) {
				boolean differs = false;
				for (int i = 0; i < stringBytes.length; i++) {
					if (stringBytes[i] != buffer.get(buffer.position() - stringBytes.length + i)) {
						differs = true;
						break;
					}
				}
				if (!differs) {
					break;
				}
			}
		} while (true);

		System.out.println("Terminated read up to delimiter");
		return buffer;
	}

	private static String getStatusDescription(int statusCode) {
		switch (statusCode) {
		default:
		case 200:
			return "OK";
		case 404:
			return "NOT FOUND";
		}
	}

	protected static String stringFromByteBuffer(ByteBuffer buffer) {
		return new String(buffer.array());
	}

	protected final SocketChannel clientSocket;

	public HTTPHandler(SocketChannel clientSocket) {
		this.clientSocket = clientSocket;
	}

	protected void sendResponse(int statusCode, Map<String, String> headers, ByteBuffer body) throws IOException {
		// First line
		final ByteBuffer firstLine = ByteBuffer
				.wrap(String.format("HTTP/1.1 %d %s\r\n", statusCode, getStatusDescription(statusCode)).getBytes());
		clientSocket.write(firstLine);
		System.out.println("Sent response line");
		// Headers
		if (headers == null) {
			headers = new HashMap<>();
		}
		headers.putIfAbsent("Content-Length", Integer.toString(body.limit()));
		for (final Map.Entry<String, String> header : headers.entrySet()) {
			final ByteBuffer headerLine = ByteBuffer
					.wrap(String.format("%s: %s\r\n", header.getKey(), header.getValue()).getBytes());
			clientSocket.write(headerLine);
		}
		System.out.println("Sent headers");
		// Blank
		clientSocket.write(ByteBuffer.wrap(new byte[] { '\r', '\n' }));
		System.out.println("Sent separator");
		// Body
		clientSocket.write(body);
		System.out.println("Sent body");
	}

	protected void sendResponse(int statusCode, Map<String, String> headers, String body) throws IOException {
		this.sendResponse(statusCode, headers, ByteBuffer.wrap(body.getBytes()));
	}

}
