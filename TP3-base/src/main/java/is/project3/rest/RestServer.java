package is.project3.rest;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class RestServer {

    public static void start() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/api/profit", exchange -> sendResponse(exchange, StatsController.getProfitPerItem()));
            server.createContext("/api/revenue", exchange -> sendResponse(exchange, StatsController.getTotalRevenue()));
            server.createContext("/api/top-countries", exchange -> sendResponse(exchange, StatsController.getTopCountryPerItem()));

            server.createContext("/api/stats", exchange -> {
                String query = exchange.getRequestURI().getQuery(); // eg. table=output-total-expenses
                String response = "[]";
                if (query != null && query.startsWith("table=")) {
                    String tableName = query.substring(6);
                    response = StatsController.getGenericTable(tableName);
                }
                sendResponse(exchange, response);
            });

            server.setExecutor(null);
            server.start();
            System.out.println("REST API Server started on http://localhost:8080");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

        byte[] bytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}