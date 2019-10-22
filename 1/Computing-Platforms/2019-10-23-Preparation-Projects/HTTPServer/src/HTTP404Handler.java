import java.io.IOException;
import java.nio.channels.SocketChannel;

public class HTTP404Handler extends HTTPHandler {

	public HTTP404Handler(SocketChannel clientSocket) {
		super(clientSocket);
	}

	@Override
	public void run() {
		System.out.println("Entered 404 handler");
		try {
			this.sendResponse(404, null, "<h1>Page not found</h1>");
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
