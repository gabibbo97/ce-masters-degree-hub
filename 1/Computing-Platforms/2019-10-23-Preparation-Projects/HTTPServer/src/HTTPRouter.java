import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class HTTPRouter extends HTTPHandler {

	private final Map<String, Class<? extends HTTPHandler>> routes = new HashMap<>();

	public HTTPRouter(SocketChannel clientSocket) {
		super(clientSocket);
	}

	@Override
	public void run() {
		System.out.println("Entered router");
		try {
			final HTTPRequest req = new HTTPRequest(clientSocket);
			final Class<? extends HTTPHandler> designatedHandlerClass = routes.getOrDefault(req.getPath(),
					HTTP404Handler.class);

			final HTTPHandler designatedHandler = designatedHandlerClass.getConstructor(SocketChannel.class)
					.newInstance(clientSocket);

			designatedHandler.run();
			if (clientSocket.isOpen()) {
				clientSocket.close();
			}
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
