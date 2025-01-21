package com.aluracursos.literalura.client;

import com.aluracursos.literalura.model.Autor;
import com.aluracursos.literalura.model.Libro;
import com.aluracursos.literalura.model.RespuestaLibros;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ClienteHttp {
    private static final String BASE_URL = "https://gutendex.com/books/";

    public List<Libro> buscarLibrosPorTitulo(String titulo) {
        try {
            // Codificar la URL para manejar caracteres especiales
            String url = BASE_URL + "?search=" + URLEncoder.encode(titulo, StandardCharsets.UTF_8);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(java.net.URI.create(url)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Error al buscar libros: Código " + response.statusCode());
            }

            ObjectMapper mapper = new ObjectMapper();
            RespuestaLibros respuesta = mapper.readValue(response.body(), RespuestaLibros.class);

            List<Libro> librosProcesados = new ArrayList<>();
            for (Libro libro : respuesta.getResults()) {
                // Solo conservar el primer idioma
                if (libro.getLanguages() != null && !libro.getLanguages().isEmpty()) {
                    libro.setLanguages(Collections.singletonList(libro.getLanguages().get(0))); // Primer idioma
                }

                // Solo conservar el primer autor
                if (libro.getAuthors() != null && !libro.getAuthors().isEmpty()) {
                    libro.setAuthors(Collections.singletonList(libro.getAuthors().get(0))); // Primer autor
                }

                librosProcesados.add(libro);
            }

            return librosProcesados;

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error al conectar con la API", e);
        }
    }
}
