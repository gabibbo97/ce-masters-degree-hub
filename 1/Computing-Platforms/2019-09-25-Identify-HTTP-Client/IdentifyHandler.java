import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.stream.Collectors;

import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class IdentifyHandler implements HttpHandler {
    
    private Map<String, Integer> identifyMap;
    private Integer hitCount;
    
    public IdentifyHandler() {
        super();
        this.identifyMap = new HashMap<>();
        this.hitCount = 0;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        // Add content-type header
        exchange.getResponseHeaders().set("Content-Type", "text/html");

        this.hitCount += 1;

        if (extractIdentifier(exchange) != null) {
            // Identification available
            existingClientHandler(exchange);
        } else {
            // No identification found
            newClientHandler(exchange);
        }
    }

    private String extractIdentifier(HttpExchange exchange) {
        if (!exchange.getRequestHeaders().containsKey("Cookie"))
            return null;
        
        String cookieString = exchange.getRequestHeaders().getFirst("Cookie");

        Map<String,String> cookies = Arrays.stream(cookieString.split(";"))
            .map((item) -> item.split("="))
            .collect(Collectors.toMap((item) -> item[0], (item) -> item[1]));
        
        return cookies.containsKey("X-Client-Identifier") ? cookies.get("X-Client-Identifier") : null;
    }

    private String createHtmlPayload(String body) {
        StringBuilder page = new StringBuilder();
        page.append("<!DOCTYPE HTML>");
        page.append(
            getTag(
                "html",
                getTag(
                    "head",
                    getTag("title", "Identify from cookie")
                ),
                getTag(
                    "body", 
                    body,
                    getTag("h6", "Total hit count: " + Integer.toString(this.hitCount))
                )
            )
        );

        return page.toString();
    }

    private String getTag(String tag, String... content) {
        return '<' + tag + '>'
            + Arrays.stream(content).collect(Collectors.joining())
            + "</" + tag + '>';
    }

    private void sendString(HttpExchange exchange, String payload) throws IOException {
        exchange.getResponseHeaders().set("Encoding","UTF-8");

        byte[] responseBytes = payload.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, responseBytes.length);

        exchange.getResponseBody().write(responseBytes);

        exchange.close();
    }

    private static String newClientString = "You have never visited this website";
    private void newClientHandler(HttpExchange exchange) throws IOException {
        // Generate client UUID
        UUID clientUUID = UUID.randomUUID();
        System.out.println("Adding client " + clientUUID.toString());

        // Set client header UUID
        exchange.getResponseHeaders().set("Set-Cookie", "X-Client-Identifier" + '=' + clientUUID.toString());

        // Register the client
        identifyMap.put(clientUUID.toString(), 1);
        
        sendString(
            exchange,
            createHtmlPayload(getTag("h1", newClientString))
        );
    }

    private static String greeting = "Hi {{ code }}";
    private static String foundHitCount = "You have visited this website {{ count }} times";

    private void existingClientHandler(HttpExchange exchange) throws IOException {
        String uuid = extractIdentifier(exchange);
        System.out.println("Client identifier: " + uuid.toString());

        String greet = greeting.replace("{{ code }}", uuid);

        if (identifyMap.containsKey(uuid)) {
            // Visitor found

            // Increment hit count
            identifyMap.put(uuid, identifyMap.get(uuid) + 1);

            // Generate hit count text
            String hitCount = foundHitCount.replace("{{ count }}", Integer.toString(identifyMap.get(uuid)));

            sendString(exchange,
                createHtmlPayload(
                    getTag("h1", greet) + "<br/>" + getTag("h2", hitCount)
                )
            );
        } else {
            // Visitor not found
            exchange.getResponseHeaders().set("Set-Cookie", "X-Client-Identifier" + '=' + "invalid; expires=Thu, 01 Jan 1970 00:00:00 GMT");
            newClientHandler(exchange);
        }
    }
}