import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {
    private final HttpClient client;

    public ApiClient() {
        // Crear una instancia de HttpClient
        this.client = HttpClient.newHttpClient();
    }

    public String obtenerTasasDeCambio(String apiKey) throws Exception {
        // URL del endpoint de la API
        String url = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/USD"; // Cambia "USD" por la moneda base que desees

        // Crear la solicitud HTTP
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url)) // Establecer la URI
                .header("Accept", "application/json") // Establecer encabezados
                .GET() // Método GET
                .build();

        // Enviar la solicitud y obtener la respuesta
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Verificar el código de estado de la respuesta
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            // Si la respuesta es exitosa, retornar el cuerpo de la respuesta
            return response.body();
        } else {
            // Si hay un error, imprimir el código de estado y los encabezados
            System.out.println("Error en la solicitud: " + statusCode);
            System.out.println("Encabezados de la respuesta: " + response.headers());
            throw new RuntimeException("Error en la solicitud: " + statusCode);
        }
    }
}