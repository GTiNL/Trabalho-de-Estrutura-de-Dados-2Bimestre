package br.edu.biblioteca.model;

import br.edu.biblioteca.structures.Vetor;

public class Livro {

    private String isbn;
    private String titulo;
    private String editora;
    private int ano;
    private Vetor<Categoria> categorias;
    private Vetor<Autor> autores;
    private Vetor<String> palavrasChave;

    public Livro(String isbn, String titulo, String editora, int ano) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.editora = editora;
        this.ano = ano;
        this.categorias = new Vetor<>();
        this.autores = new Vetor<>();
        this.palavrasChave = new Vetor<>();
    }

    // Getters e Setters
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getEditora() { return editora; }
    public void setEditora(String editora) { this.editora = editora; }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public Vetor<Categoria> getCategorias() { return categorias; }
    public void setCategorias(Vetor<Categoria> categorias) { this.categorias = categorias; }

    public Vetor<Autor> getAutores() { return autores; }
    public void setAutores(Vetor<Autor> autores) { this.autores = autores; }

    public Vetor<String> getPalavrasChave() { return palavrasChave; }
    public void setPalavrasChave(Vetor<String> palavrasChave) { this.palavrasChave = palavrasChave; }

    public void adicionarAutor(Autor autor) {
        autores.add(autor);
    }

    public void adicionarCategoria(Categoria categoria) {
        categorias.add(categoria);
    }

    public void adicionarPalavraChave(String palavra) {
        palavrasChave.add(palavra);
    }

    public String toString() {
        String resultado = "ISBN: " + isbn + " | Titulo: " + titulo + " | Editora: " + editora + " | Ano: " + ano;

        resultado += " | Autores: [";
        for (int i = 0; i < autores.tamanho(); i++) {
            if (i > 0) resultado += ", ";
            resultado += autores.get(i).getNome();
        }
        resultado += "]";

        resultado += " | Categorias: [";
        for (int i = 0; i < categorias.tamanho(); i++) {
            if (i > 0) resultado += ", ";
            resultado += categorias.get(i).getNome();
        }
        resultado += "]";

        return resultado;
    }
}
