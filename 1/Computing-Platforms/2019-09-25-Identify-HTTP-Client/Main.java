import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.*;

public class Main {
    public static void main(String[] args) throws IOException {
        // Create the HTTP server
        HttpServer httpServer = HttpServer.create();

        // Bind to localhost
        httpServer.bind(
            new InetSocketAddress(Inet6Address.getLocalHost(), 8080), 0
        );

        // Bind an handler to the / context
        httpServer.createContext("/", new IdentifyHandler());

        // Start up the server
        httpServer.start();
    }
}