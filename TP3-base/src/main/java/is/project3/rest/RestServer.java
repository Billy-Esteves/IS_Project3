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

            server.createContext("/api/revenue-per-item",   e -> sendResponse(e, StatsController.getRevenuePerItem()));
            server.createContext("/api/expenses-per-item",  e -> sendResponse(e, StatsController.getExpensesPerItem()));
            server.createContext("/api/profit-per-item",    e -> sendResponse(e, StatsController.getProfitPerItem()));
            server.createContext("/api/total-revenue",      e -> sendResponse(e, StatsController.getTotalRevenue()));
            server.createContext("/api/purchase-averages",  e -> sendResponse(e, StatsController.getPurchaseAverages()));
            server.createContext("/api/highest-profit-item",e -> sendResponse(e, StatsController.getHighestProfitItem()));
            server.createContext("/api/revenue-last-hour",  e -> sendResponse(e, StatsController.getRevenueLastHour()));
            server.createContext("/api/top-countries",      e -> sendResponse(e, StatsController.getTopCountryPerItem()));


            server.createContext("/api/items",     e -> sendResponse(e, StatsController.getItems()));
            server.createContext("/api/countries", e -> sendResponse(e, StatsController.getCountries()));

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