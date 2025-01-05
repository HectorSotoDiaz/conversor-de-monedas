import com.google.gson.Gson;
import java.util.Map;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Main {
    public static void main(String[] args) {
        ApiClient apiClient = new ApiClient();
        String apiKey = "3f5ec58d3d83dc9621647c21"; // Reemplaza con tu clave de API
        Scanner scanner = new Scanner(System.in);
        TasaCambioResponse response = null;

        while (true) {
            System.out.println("\n--- Conversor de Monedas ---");
            System.out.println("1. Realizar una conversión de moneda");
            System.out.println("2. Mostrar tasas de cambio para monedas específicas");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");
            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    // Obtener tasas de cambio si no se han obtenido aún
                    if (response == null) {
                        response = obtenerTasasDeCambio(apiClient, apiKey);
                    }
                    realizarConversion(scanner, response);
                    break;
                case 2:
                    // Mostrar tasas de cambio
                    if (response == null) {
                        response = obtenerTasasDeCambio(apiClient, apiKey);
                    }
                    mostrarTasasDeCambio(response);
                    break;
                case 3:
                    System.out.println("Saliendo del programa. ¡Hasta luego!");
                    return; // Salir del programa
                default:
                    System.out.println("Opción no válida. Por favor, seleccione una opción válida.");
            }
        }
    }

    private static TasaCambioResponse obtenerTasasDeCambio(ApiClient apiClient, String apiKey) {
        try {
            String jsonResponse = apiClient.obtenerTasasDeCambio(apiKey);
            Gson gson = new Gson();
            return gson.fromJson(jsonResponse, TasaCambioResponse.class);
        } catch (Exception e) {
            System.out.println("Ocurrió un error al obtener las tasas de cambio: " + e.getMessage());
            return null;
        }
    }

    private static void realizarConversion(Scanner scanner, TasaCambioResponse response) {
        double cantidad = 0;
        boolean entradaValida = false;

        // Bucle para solicitar una cantidad válida
        while (!entradaValida) {
            System.out.println("Ingrese la cantidad a convertir:");
            try {
                cantidad = scanner.nextDouble();
                if (cantidad < 0) {
                    System.out.println("La cantidad debe ser un número positivo. Intente de nuevo.");
                } else {
                    entradaValida = true; // Salir del bucle si la entrada es válida
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, ingrese un número.");
                scanner.next(); // Limpiar el buffer del scanner
            }
        }

        System.out.println("Ingrese la moneda de origen (ej. USD):");
        String monedaOrigen = scanner.next().toUpperCase();
        System.out.println("Ingrese la moneda de destino (ej. EUR):");
        String monedaDestino = scanner.next().toUpperCase();

        try {
            double resultado = convertirMoneda(cantidad, monedaOrigen, monedaDestino, response.conversion_rates);
            System.out.printf("%.2f %s son %.2f %s%n", cantidad, monedaOrigen, resultado, monedaDestino);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void mostrarTasasDeCambio(TasaCambioResponse response) {
        String[] monedasInteres = {"ARS", "BOB", "BRL", "CLP", "COP", "USD"};
        System.out.println("Tasas de cambio para las monedas seleccionadas:");
        for (String moneda : monedasInteres) {
            Double tasa = response.conversion_rates.get(moneda);
            if (tasa != null) {
                System.out.printf("%s: %.2f%n", moneda, tasa);
            } else {
                System.out.printf("%s: No disponible%n", moneda);
            }
        }
    }

    // Método para convertir monedas
    public static double convertirMoneda(double cantidad, String monedaOrigen, String monedaDestino, Map<String, Double> conversionRates) {
        Double tasaOrigen = conversionRates.get(monedaOrigen);
        Double tasaDestino = conversionRates.get(monedaDestino);

        if (tasaOrigen == null || tasaDestino == null) {
            throw new IllegalArgumentException("Una de las monedas ingresadas no es válida.");
        }

        // Realizar la conversión
        return (cantidad / tasaOrigen) * tasaDestino;
    }
}