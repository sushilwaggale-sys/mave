import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.Executors;

public class FrontendServer {
    private static final Path FRONTEND_DIRECTORY = Path.of("frontend").toAbsolutePath().normalize();
    private static final Map<String, String> CONTENT_TYPES = Map.of(
            ".html", "text/html; charset=utf-8",
            ".css", "text/css; charset=utf-8",
            ".js", "application/javascript; charset=utf-8"
    );

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("FRONTEND_PORT", "3000"));
        String apiBaseUrl = System.getenv().getOrDefault("API_BASE_URL", "http://localhost:8080")
                .replaceAll("/+$", "");
        if (!Files.isDirectory(FRONTEND_DIRECTORY)) {
            throw new IllegalStateException("Frontend directory not found: " + FRONTEND_DIRECTORY);
        }

        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        server.setExecutor(Executors.newCachedThreadPool());
        server.createContext("/", exchange -> serve(exchange, apiBaseUrl));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop(0)));
        server.start();
        System.out.printf("Employee Management frontend running on http://localhost:%d%n", port);
        System.out.printf("API base URL: %s%n", apiBaseUrl);
    }

    private static void serve(HttpExchange exchange, String apiBaseUrl) throws IOException {
        if (!exchange.getRequestMethod().equals("GET") && !exchange.getRequestMethod().equals("HEAD")) {
            exchange.getResponseHeaders().set("Allow", "GET, HEAD");
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        String requestPath = exchange.getRequestURI().getPath();
        if (requestPath.equals("/config.js")) {
            byte[] config = ("window.APP_CONFIG = {\"API_BASE_URL\":\""
                    + escapeJavaScriptString(apiBaseUrl) + "\"};\n").getBytes(StandardCharsets.UTF_8);
            send(exchange, 200, "application/javascript; charset=utf-8", config);
            return;
        }

        if (requestPath.equals("/")) {
            requestPath = "/index.html";
        }
        Path requestedFile = FRONTEND_DIRECTORY.resolve(requestPath.substring(1)).normalize();
        if (!requestedFile.startsWith(FRONTEND_DIRECTORY) || !Files.isRegularFile(requestedFile)) {
            send(exchange, 404, "text/plain; charset=utf-8", "Not found".getBytes(StandardCharsets.UTF_8));
            return;
        }

        String fileName = requestedFile.getFileName().toString();
        int extensionStart = fileName.lastIndexOf('.');
        String extension = extensionStart < 0 ? "" : fileName.substring(extensionStart);
        String contentType = CONTENT_TYPES.getOrDefault(extension, "application/octet-stream");
        send(exchange, 200, contentType, Files.readAllBytes(requestedFile));
    }

    private static String escapeJavaScriptString(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\r", "\\r").replace("\n", "\\n")
                .replace("<", "\\u003c").replace(">", "\\u003e");
    }

    private static void send(HttpExchange exchange, int status, String contentType, byte[] body)
            throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        if (exchange.getRequestMethod().equals("HEAD")) {
            exchange.getResponseHeaders().set("Content-Length", Integer.toString(body.length));
            exchange.sendResponseHeaders(status, -1);
        } else {
            exchange.sendResponseHeaders(status, body.length);
            exchange.getResponseBody().write(body);
        }
        exchange.close();
    }
}
