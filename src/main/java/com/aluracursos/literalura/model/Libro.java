package com.aluracursos.literalura.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "libros")

@JsonIgnoreProperties(ignoreUnknown = true)
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column (unique = true)
    private String title;
    @ElementCollection
    private List<String> languages;
    @JsonProperty("download_count") // Indica que este campo se mapea desde download_count en el JSON
    private int downloadCount;
    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Autor> authors;

    // Getters y setters


    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public List<Autor> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Autor> authors) {
        this.authors = authors;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Libro() {
        // Constructor sin argumentos requerido por Hibernate
    }

}
