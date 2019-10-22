import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HTTPRequest {
	private final String method;
	private final String path;
	private final String HTTPVersion;
	private final Map<String, String> headers;
	private final ByteBuffer requestBody;

	public HTTPRequest(SocketChannel clientSocket) throws IOException {
		// Request line
		System.out.println("Parsing request line");
		final String reqLine = HTTPHandler
				.stringFromByteBuffer(HTTPHandler.getIncomingStringUpTo(clientSocket, "\r\n"));
		method = reqLine.split(" ")[0];
		path = reqLine.split(" ")[1];
		HTTPVersion = reqLine.split(" ")[2];
		// Headers
		System.out.println("Parsing headers");
		headers = new HashMap<>();
		while (true) {
			final String headerLine = HTTPHandler
					.stringFromByteBuffer(HTTPHandler.getIncomingStringUpTo(clientSocket, "\r\n"));
			if (headerLine.startsWith("\r\n")) {
				break;
			}

			final String headerKey = headerLine.substring(0, headerLine.indexOf('\r')).split(":")[0].trim();
			final String headerValue = headerLine.substring(0, headerLine.indexOf('\r')).split(":")[1].trim();

			headers.put(headerKey, headerValue);
		}
		// Body
		System.out.println("Skipping body");
		requestBody = ByteBuffer.allocate(0);
		// End
		System.out.println("Parsed request");
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public String getHTTPVersion() {
		return HTTPVersion;
	}

	public String getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public List<String> getPathComponents() {
		return Arrays.asList(path.split("/"));
	}

	public ByteBuffer getRequestBody() {
		return requestBody;
	}

}
