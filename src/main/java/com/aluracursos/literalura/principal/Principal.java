package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.client.ClienteHttp;
import com.aluracursos.literalura.model.Autor;
import com.aluracursos.literalura.model.Libro;
import com.aluracursos.literalura.repository.LibroRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Principal implements CommandLineRunner {

    private final ClienteHttp clienteHttp;
    private final LibroRepository libroRepository;

    public Principal(ClienteHttp clienteHttp, LibroRepository libroRepository) {
        this.clienteHttp = clienteHttp;
        this.libroRepository = libroRepository;
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println("\nMenú:");
            System.out.println("1. Buscar un libro por título");
            System.out.println("2. Listar libros registrados");
            System.out.println("3. Listar autores registrados");
            System.out.println("4. Listar autores vivos en un determinado año");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    buscarLibroPorTitulo(scanner);
                    break;
                case "2":
                    listarLibros();
                    break;
                case "3":
                    listarAutores();
                    break;
                case "4":
                    listarAutoresVivos(scanner);
                    break;
                case "0":
                    System.out.println("Gracias por usar el programa.");
                    salir = true;
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
        }

        scanner.close();
    }

    @Transactional
    private void buscarLibroPorTitulo(Scanner scanner) {
        System.out.print("Ingrese el título del libro: ");
        String titulo = scanner.nextLine();

        try {
            List<Libro> libros = clienteHttp.buscarLibrosPorTitulo(titulo);

            if (!libros.isEmpty()) {
                for (Libro libro : libros) {
                    System.out.println("\n-------------------LIBRO---------------------");
                    System.out.println("Título: " + libro.getTitle());
                    System.out.println("Autor: " +
                            (libro.getAuthors() == null || libro.getAuthors().isEmpty() ? "Desconocido" :
                                    libro.getAuthors().get(0).getName()));
                    System.out.println("Idioma: " +
                            (libro.getLanguages() == null || libro.getLanguages().isEmpty() ? "Desconocido" :
                                    libro.getLanguages().get(0)));
                    System.out.println("Número de descargas: " + libro.getDownloadCount());
                    System.out.println("---------------------------------------------\n");

                    // Guardar en la base de datos
                    if (!libroRepository.existsByTitle(libro.getTitle())) {
                        for (Autor autor : libro.getAuthors()) {
                            autor.setLibro(libro);
                        }
                        libroRepository.save(libro);
                    }
                }
            } else {
                System.out.println("No se encontraron libros con ese título.");
            }

        } catch (Exception e) {
            System.out.println("Error al buscar libros: " + e.getMessage());
        }
    }

    private void listarLibros() {
        List<Libro> libros = libroRepository.findAll();

        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados en la base de datos.");
        } else {
            System.out.println("\nLibros registrados:");
            for (Libro libro : libros) {
                System.out.println("- " + libro.getTitle() + " | Descargas: " + libro.getDownloadCount());
            }
        }
    }

    private void listarAutores() {
        // Obtener todos los libros de la base de datos
        List<Libro> libros = libroRepository.findAll();

        // Usar un `Set` para evitar autores duplicados
        Set<String> autoresUnicos = new HashSet<>();

        // Iterar por cada libro y recolectar autores
        for (Libro libro : libros) {
            if (libro.getAuthors() != null) {
                for (Autor autor : libro.getAuthors()) {
                    autoresUnicos.add(autor.getName());
                }
            }
        }

        // Mostrar los autores únicos
        System.out.println("\nAutores registrados:");
        if (autoresUnicos.isEmpty()) {
            System.out.println("No se encontraron autores registrados.");
        } else {
            autoresUnicos.forEach(System.out::println);
        }
    }

    private void listarAutoresVivos(Scanner scanner) {
        System.out.print("Ingrese el año: ");
        int anio;
        try {
            anio = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un año válido.");
            return;
        }

        // Obtener todos los libros de la base de datos
        List<Libro> libros = libroRepository.findAll();

        // Usar un `Set` para evitar duplicados
        Set<String> autoresVivos = new HashSet<>();

        // Filtrar autores vivos en el año especificado
        for (Libro libro : libros) {
            if (libro.getAuthors() != null) {
                for (Autor autor : libro.getAuthors()) {
                    // Si el autor no tiene fecha de muerte o su año de muerte es después del año ingresado
                    if (autor.getDeathYear() == null || autor.getDeathYear() > anio) {
                        // Si nació antes o en el año especificado, consideramos que estaba vivo
                        if (autor.getBirthYear() != null && autor.getBirthYear() <= anio) {
                            autoresVivos.add(autor.getName());
                        }
                    }
                }
            }
        }

        // Mostrar los autores vivos
        System.out.println("\nAutores vivos en el año " + anio + ":");
        if (autoresVivos.isEmpty()) {
            System.out.println("No se encontraron autores vivos en el año especificado.");
        } else {
            autoresVivos.forEach(System.out::println);
        }
    }

    public void mostrarMenu() {
    }
}
