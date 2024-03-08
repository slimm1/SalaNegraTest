package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiHandler {
    public static void main(String[] args) {
        try {
            // URL de la API
            String apiUrl = "https://sala-negra.com/actua_public_api_v1/get_events";

            // Parámetros de la solicitud
            String parametros = ""; // No hay parámetros en este caso

            // Convertir la cadena de parámetros a bytes
            byte[] postData = parametros.getBytes();

            // Crear la conexión
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postData.length));

            // Escribir los parámetros en el cuerpo de la solicitud
            try (OutputStream os = conn.getOutputStream()) {
                os.write(postData);
            }

            // Leer la respuesta de la API
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                System.out.println("Respuesta de la API:");
                System.out.println(response.toString());
            }

            // Cerrar la conexión
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}