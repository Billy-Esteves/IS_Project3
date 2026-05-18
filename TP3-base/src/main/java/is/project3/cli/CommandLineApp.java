package is.project3.cli;

import is.project3.rest.DatabaseClient;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class CommandLineApp {

    public static void main(String[] args) {
        start();
    }

    public static void start() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("\n=========================================");
        System.out.println("      ADMINISTRATION CLI STARTED         ");
        System.out.println("=========================================\n");

        while (running) {
            System.out.println("\n--- DATA ENTRY ---");
            System.out.println("1.  Add a new Item (Product)");
            System.out.println("2.  Add a new Country");

            System.out.println("\n--- REST API METRICS ---");
            System.out.println("3.  Get All Items (from DB)");
            System.out.println("4.  Get All Countries (from DB)");
            System.out.println("5.  Get Revenue per Item");
            System.out.println("6.  Get Expenses per Item");
            System.out.println("7.  Get Profit per Item");
            System.out.println("8.  Get Total Revenue");
            System.out.println("9.  Get Purchase Averages");
            System.out.println("10. Get Highest Profit Item");
            System.out.println("11. Get Revenue Last Hour");
            System.out.println("12. Get Top Countries per Item");

            System.out.println("\n0.  Exit Application");
            System.out.print("\nSelect an action (0-12): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Enter the name of the new item: ");
                    String item = scanner.nextLine().trim();
                    if (!item.isEmpty() && DatabaseClient.insertRecord("items", "item_name", item)) {
                        System.out.println("[SUCCESS] Item '" + item + "' added to PostgreSQL.");
                    }
                    break;
                case "2":
                    System.out.print("Enter the name of the new country: ");
                    String country = scanner.nextLine().trim();
                    if (!country.isEmpty() && DatabaseClient.insertRecord("countries", "country_name", country)) {
                        System.out.println("[SUCCESS] Country '" + country + "' added to PostgreSQL.");
                    }
                    break;
                case "3":
                    callRestApi("http://localhost:8080/api/items");
                    break;
                case "4":
                    callRestApi("http://localhost:8080/api/countries");
                    break;
                case "5":
                    callRestApi("http://localhost:8080/api/revenue-per-item");
                    break;
                case "6":
                    callRestApi("http://localhost:8080/api/expenses-per-item");
                    break;
                case "7":
                    callRestApi("http://localhost:8080/api/profit-per-item");
                    break;
                case "8":
                    callRestApi("http://localhost:8080/api/total-revenue");
                    break;
                case "9":
                    callRestApi("http://localhost:8080/api/purchase-averages");
                    break;
                case "10":
                    callRestApi("http://localhost:8080/api/highest-profit-item");
                    break;
                case "11":
                    callRestApi("http://localhost:8080/api/revenue-last-hour");
                    break;
                case "12":
                    callRestApi("http://localhost:8080/api/top-countries");
                    break;
                case "0":
                    System.out.println("Shutting down the CLI...");
                    running = false;
                    break;
                default:
                    System.out.println("[ERROR] Invalid option! Please select a number from 0 to 12.");
            }
        }
    }

    private static void callRestApi(String endpointUrl) {
        try {
            URL url = new URL(endpointUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                System.out.println("[ERROR] Failed : HTTP error code : " + conn.getResponseCode());
                return;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            System.out.println("\n[DATA FROM REST API]: " + endpointUrl);
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
            conn.disconnect();

        } catch (Exception e) {
            System.out.println("[ERROR] Failed to fetch data from API: " + e.getMessage());
        }
    }
}