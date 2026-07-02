package br.edu.biblioteca.service;

import br.edu.biblioteca.model.*;
import br.edu.biblioteca.structures.ArvoreBST;
import br.edu.biblioteca.structures.Vetor;

// Servico de gerenciamento de livros e exemplares
public class CatalogoService {

    private Vetor<Livro> livros;
    private Vetor<Exemplar> exemplares;
    private ArvoreBST<String, Livro> indicePorIsbn;
    private ArvoreBST<String, Livro> indicePorTitulo;
    private int proximoExemplarId;

    public CatalogoService() {
        this.livros = new Vetor<>();
        this.exemplares = new Vetor<>();
        this.indicePorIsbn = new ArvoreBST<>();
        this.indicePorTitulo = new ArvoreBST<>();
        this.proximoExemplarId = 1;
    }

    // ========== LIVROS ==========

    // Cadastra um livro. Retorna true se cadastrou, false se ISBN ja existe.
    public boolean cadastrarLivro(Livro livro) {
        if (indicePorIsbn.containsKey(livro.getIsbn())) {
            return false;
        }
        livros.add(livro);
        indicePorIsbn.put(livro.getIsbn(), livro);
        indicePorTitulo.put(livro.getTitulo().toLowerCase(), livro);
        return true;
    }

    // Remove um livro pelo ISBN e seus exemplares
    public Livro removerLivro(String isbn) {
        Livro livro = indicePorIsbn.remove(isbn);
        if (livro == null) {
            return null;
        }

        indicePorTitulo.remove(livro.getTitulo().toLowerCase());

        // Remove da lista de livros
        for (int i = 0; i < livros.tamanho(); i++) {
            if (livros.get(i).getIsbn().equals(isbn)) {
                livros.remove(i);
                break;
            }
        }

        // Remove exemplares desse livro
        for (int i = exemplares.tamanho() - 1; i >= 0; i--) {
            if (exemplares.get(i).getIsbnLivro().equals(isbn)) {
                exemplares.remove(i);
            }
        }

        return livro;
    }

    // Busca livro pelo ISBN (usa arvore)
    public Livro buscarPorIsbn(String isbn) {
        return indicePorIsbn.get(isbn);
    }

    // Busca livro pelo titulo (usa arvore)
    public Livro buscarPorTitulo(String titulo) {
        return indicePorTitulo.get(titulo.toLowerCase());
    }

    // Lista todos os livros
    public Vetor<Livro> listarLivros() {
        return livros;
    }

    // Lista livros ordenados por titulo (percurso in-order da arvore)
    public Vetor<Livro> listarLivrosOrdenadosPorTitulo() {
        return indicePorTitulo.inOrder();
    }

    // Lista livros ordenados por ISBN (percurso in-order da arvore)
    public Vetor<Livro> listarLivrosOrdenadosPorIsbn() {
        return indicePorIsbn.inOrder();
    }

    // Lista livros ordenados por ano (Bubble Sort)
    public Vetor<Livro> listarLivrosOrdenadosPorAno() {
        // Copia a lista para nao alterar a original
        Vetor<Livro> copia = new Vetor<>();
        for (int i = 0; i < livros.tamanho(); i++) {
            copia.add(livros.get(i));
        }

        // Bubble Sort por ano
        for (int i = 0; i < copia.tamanho() - 1; i++) {
            for (int j = 0; j < copia.tamanho() - 1 - i; j++) {
                if (copia.get(j).getAno() > copia.get(j + 1).getAno()) {
                    Livro temp = copia.get(j);
                    copia.set(j, copia.get(j + 1));
                    copia.set(j + 1, temp);
                }
            }
        }
        return copia;
    }

    // ========== EXEMPLARES ==========

    // Cadastra um exemplar para um livro existente
    public Exemplar cadastrarExemplar(String isbn) {
        if (!indicePorIsbn.containsKey(isbn)) {
            return null;
        }
        Exemplar exemplar = new Exemplar(proximoExemplarId, isbn);
        proximoExemplarId++;
        exemplares.add(exemplar);
        return exemplar;
    }

    // Busca exemplar pelo ID
    public Exemplar buscarExemplarPorId(int id) {
        for (int i = 0; i < exemplares.tamanho(); i++) {
            if (exemplares.get(i).getId() == id) {
                return exemplares.get(i);
            }
        }
        return null;
    }

    // Lista exemplares de um livro pelo ISBN
    public Vetor<Exemplar> listarExemplaresPorIsbn(String isbn) {
        Vetor<Exemplar> resultado = new Vetor<>();
        for (int i = 0; i < exemplares.tamanho(); i++) {
            if (exemplares.get(i).getIsbnLivro().equals(isbn)) {
                resultado.add(exemplares.get(i));
            }
        }
        return resultado;
    }

    // Lista todos os exemplares
    public Vetor<Exemplar> listarExemplares() {
        return exemplares;
    }

    // Busca um exemplar disponivel para um livro
    public Exemplar buscarExemplarDisponivel(String isbn) {
        for (int i = 0; i < exemplares.tamanho(); i++) {
            Exemplar ex = exemplares.get(i);
            if (ex.getIsbnLivro().equals(isbn) && ex.getStatus() == StatusExemplar.DISPONIVEL) {
                return ex;
            }
        }
        return null;
    }

    // Carrega um exemplar persistido diretamente, sem gerar novo ID
    public void carregarExemplar(Exemplar exemplar) {
        exemplares.add(exemplar);
        if (exemplar.getId() >= proximoExemplarId) proximoExemplarId = exemplar.getId() + 1;
    }

    public int totalLivros() {
        return livros.tamanho();
    }

    public int totalExemplares() {
        return exemplares.tamanho();
    }
}
