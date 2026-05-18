package is.project3.rest;

import com.google.gson.Gson;
import java.util.List;
import java.util.Map;

public class StatsController {
    private static final Gson gson = new Gson();

    public static String getProfitPerItem() {
        String query = "SELECT * FROM \"output-profit-per-item\";";
        List<Map<String, Object>> result = DatabaseClient.executeQuery(query);
        return gson.toJson(result);
    }

    public static String getTotalRevenue() {
        String query = "SELECT * FROM \"output-total-revenue\";";
        List<Map<String, Object>> result = DatabaseClient.executeQuery(query);
        return gson.toJson(result);
    }

    public static String getTopCountryPerItem() {
        String query = "SELECT * FROM \"output-highest-sales-country\";";
        List<Map<String, Object>> result = DatabaseClient.executeQuery(query);
        return gson.toJson(result);
    }

    public static String getRevenuePerItem() {
        String query = "SELECT * FROM \"output-revenue-per-item\";";
        return gson.toJson(DatabaseClient.executeQuery(query));
    }

    public static String getHighestProfitItem() {
        String query = "SELECT * FROM \"output-highest-profit-item\";";
        return gson.toJson(DatabaseClient.executeQuery(query));
    }

    public static String getExpensesPerItem() {
        String query = "SELECT * FROM \"output-expenses-per-item\";";
        return gson.toJson(DatabaseClient.executeQuery(query));
    }

    public static String getRevenueLastHour() {
        String query = "SELECT * FROM \"output-revenue-1hour\";";
        return gson.toJson(DatabaseClient.executeQuery(query));
    }

    public static String getPurchaseAverages() {
        String query = "SELECT * FROM \"output-purchase-averages\";";
        return gson.toJson(DatabaseClient.executeQuery(query));
    }

    public static String getItems() {
        return gson.toJson(DatabaseClient.executeQuery("SELECT * FROM items;"));
    }

    public static String getCountries() {
        return gson.toJson(DatabaseClient.executeQuery("SELECT * FROM countries;"));
    }

    public static String getGenericTable(String tableName) {
        if (tableName == null || !tableName.matches("[a-zA-Z0-9-]+")) {
            return "{\"error\": \"Invalid table name\"}";
        }
        String query = "SELECT * FROM \"" + tableName + "\";";
        List<Map<String, Object>> result = DatabaseClient.executeQuery(query);
        return gson.toJson(result);
    }
}