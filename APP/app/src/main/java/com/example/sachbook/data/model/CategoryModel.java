package com.example.sachbook.data.model;

import java.util.List;

public class CategoryModel {
    private Long id;
    private String name;
    private String description;
    private List<BookModel> books;

    // Getters và Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<BookModel> getBooks() { return books; }
    public void setBooks(List<BookModel> books) { this.books = books; }
}