import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Main {
    private static final String API_KEY = "0167e912ee76235d6bfb61d1";
    private static final String BASE_URL = "https://api.exchangerate-api.com/v4/latest/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String welcome = "****************************************************\nSea bienvenid@ al Convertidor de Moneda =]";
        String menuSource =  "\n\nElige la moneda Original" +
                "\nDolar Americano --> USD" +
                "\nPeso Argentino ---> ARS" +
                "\nReal Brasileño ---> BRL" +
                "\nPeso Colombiano --> COP" +
                "\nPeso Mexicano ----> MXN" +
                "\n\nElija una opción valida ó escriba 'Exit' para salir:" +
                "\n****************************************************";

        String menuTarget =  "\n\nElige a que moneda convertir" +
                "\nDolar Americano --> USD" +
                "\nPeso Argentino ---> ARS" +
                "\nReal Brasileño ---> BRL" +
                "\nPeso Colombiano --> COP" +
                "\nPeso Mexicano ----> MXN" +
                "\n\nElija una opción valida ó escriba 'Exit' para salir:" +
                "\n****************************************************";


        while (true){
            String sourceCurrency = "";

            while (!sourceCurrency.equalsIgnoreCase("Exit")) {
                System.out.println(welcome + menuSource);
                sourceCurrency = scanner.nextLine();

                if (sourceCurrency.equalsIgnoreCase("Exit")) {
                    System.out.println("Saliendo del Programa...");
                    System.exit(0);
                }

                String targetCurrency;
                    System.out.println(menuTarget);
                    targetCurrency = scanner.nextLine();

                    if (targetCurrency.equalsIgnoreCase("Exit")) {
                        System.out.println("Saliendo del Programa...");
                        System.exit(0);
                    }


                        double amount = 0;
                        boolean validAmount = false;
                        while (!validAmount) {
                            System.out.println("Ingresa el monto a convertir: ");
                            try {
                                amount = scanner.nextDouble();
                                validAmount = true;
                            } catch (InputMismatchException e) {
                                System.out.println("Ingresó un valor incorrecto o no es número, favor de intentar nuevamente");
                                scanner.next(); // Clear the invalid input
                            }
                        }
                        try {
                            double exchangeRate = getExchangeRate(sourceCurrency, targetCurrency);
                            double convertedAmount = amount * exchangeRate;

                            System.out.printf("%.2f %s es igual a %.2f %s%n", amount, sourceCurrency, convertedAmount, targetCurrency);

                        } catch (IOException e) {
                            System.out.println("Error: No encontramos la moneda Original o la moneda a convertir, \n" +
                                    "por favor intenta nuevamente: " + e.getMessage());
                        }

                        scanner.nextLine(); // Consume the newline character
                    }
            }
        }


    private static double getExchangeRate(String sourceCurrency, String targetCurrency) throws IOException {
        String url = BASE_URL + sourceCurrency + "?apikey=" + API_KEY;
        URL apiUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);

            JsonObject rates = jsonObject.getAsJsonObject("rates");
            double exchangeRate = rates.getAsJsonPrimitive(targetCurrency).getAsDouble();
            return exchangeRate;
        } else {
            throw new IOException("Codigo de Respuesta HTTP: " + responseCode);
        }
    }
}