package br.edu.biblioteca.ui;

import br.edu.biblioteca.action.AcaoCadastrarLivro;
import br.edu.biblioteca.action.AcaoRemoverLivro;
import br.edu.biblioteca.model.*;
import br.edu.biblioteca.service.CatalogoService;
import br.edu.biblioteca.service.UndoRedoService;
import br.edu.biblioteca.structures.Vetor;

import java.util.Scanner;

public class TelaCatalogo {

    private final Scanner scanner;
    private final CatalogoService catalogoService;
    private final UndoRedoService undoRedoService;
    private int proximoAutorId;
    private int proximoCategoriaId;

    public TelaCatalogo(Scanner scanner, CatalogoService catalogoService, UndoRedoService undoRedoService) {
        this.scanner = scanner;
        this.catalogoService = catalogoService;
        this.undoRedoService = undoRedoService;
        this.proximoAutorId = calcularProximoAutorId();
        this.proximoCategoriaId = calcularProximoCategoriaId();
    }

    public void exibir() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- LIVROS E EXEMPLARES ---");
            System.out.println("  1. Cadastrar livro");
            System.out.println("  2. Cadastrar exemplar");
            System.out.println("  3. Buscar livro por ISBN");
            System.out.println("  4. Buscar livro por titulo");
            System.out.println("  5. Listar todos os livros");
            System.out.println("  6. Listar por titulo (BST in-order)");
            System.out.println("  7. Listar por ano (Bubble Sort)");
            System.out.println("  8. Listar exemplares de um livro");
            System.out.println("  9. Remover livro");
            System.out.println("  0. Voltar");

            int opcao = EntradaConsole.lerInteiro(scanner, "Opcao: ");

            if (opcao == 1) cadastrarLivro();
            else if (opcao == 2) cadastrarExemplar();
            else if (opcao == 3) buscarPorIsbn();
            else if (opcao == 4) buscarPorTitulo();
            else if (opcao == 5) listarLivros();
            else if (opcao == 6) listarOrdenadosPorTitulo();
            else if (opcao == 7) listarOrdenadosPorAno();
            else if (opcao == 8) listarExemplares();
            else if (opcao == 9) removerLivro();
            else if (opcao == 0) voltar = true;
            else System.out.println("  Opcao invalida.");
        }
    }

    private void cadastrarLivro() {
        System.out.println("\n  -- Cadastrar Livro --");
        String isbn    = EntradaConsole.lerString(scanner, "  ISBN: ");
        String titulo  = EntradaConsole.lerString(scanner, "  Titulo: ");
        String editora = EntradaConsole.lerString(scanner, "  Editora: ");
        int ano        = EntradaConsole.lerInteiro(scanner, "  Ano: ");

        Livro livro = new Livro(isbn, titulo, editora, ano);

        int nAutores = EntradaConsole.lerInteiro(scanner, "  Quantos autores? ");
        for (int i = 0; i < nAutores; i++) {
            String nome = EntradaConsole.lerString(scanner, "  Nome do autor " + (i + 1) + ": ");
            livro.adicionarAutor(new Autor(proximoAutorId++, nome));
        }

        int nCat = EntradaConsole.lerInteiro(scanner, "  Quantas categorias? ");
        for (int i = 0; i < nCat; i++) {
            String nome = EntradaConsole.lerString(scanner, "  Categoria " + (i + 1) + ": ");
            livro.adicionarCategoria(new Categoria(proximoCategoriaId++, nome));
        }

        int nPc = EntradaConsole.lerInteiro(scanner, "  Quantas palavras-chave? ");
        for (int i = 0; i < nPc; i++) {
            livro.adicionarPalavraChave(EntradaConsole.lerString(scanner, "  Palavra-chave " + (i + 1) + ": "));
        }

        undoRedoService.executarAcao(new AcaoCadastrarLivro(catalogoService, livro));
    }

    private void cadastrarExemplar() {
        System.out.println("\n  -- Cadastrar Exemplar --");
        String isbn = EntradaConsole.lerString(scanner, "  ISBN do livro: ");
        Exemplar ex = catalogoService.cadastrarExemplar(isbn);
        if (ex != null) System.out.println("  Exemplar criado: " + ex);
        else System.out.println("  [ERRO] Livro nao encontrado com ISBN: " + isbn);
    }

    private void buscarPorIsbn() {
        Livro livro = catalogoService.buscarPorIsbn(EntradaConsole.lerString(scanner, "  ISBN: "));
        System.out.println(livro != null ? "  " + livro : "  Livro nao encontrado.");
    }

    private void buscarPorTitulo() {
        Livro livro = catalogoService.buscarPorTitulo(EntradaConsole.lerString(scanner, "  Titulo: "));
        System.out.println(livro != null ? "  " + livro : "  Livro nao encontrado.");
    }

    private void listarLivros() {
        Vetor<Livro> livros = catalogoService.listarLivros();
        System.out.println("\n  Total: " + livros.tamanho() + " livro(s).");
        for (int i = 0; i < livros.tamanho(); i++) System.out.println("  " + livros.get(i));
    }

    private void listarOrdenadosPorTitulo() {
        Vetor<Livro> livros = catalogoService.listarLivrosOrdenadosPorTitulo();
        System.out.println("\n  Livros por titulo (" + livros.tamanho() + "):");
        for (int i = 0; i < livros.tamanho(); i++) System.out.println("  " + livros.get(i));
    }

    private void listarOrdenadosPorAno() {
        Vetor<Livro> livros = catalogoService.listarLivrosOrdenadosPorAno();
        System.out.println("\n  Livros por ano (" + livros.tamanho() + "):");
        for (int i = 0; i < livros.tamanho(); i++) System.out.println("  " + livros.get(i));
    }

    private void listarExemplares() {
        String isbn = EntradaConsole.lerString(scanner, "  ISBN do livro: ");
        Vetor<Exemplar> exemplares = catalogoService.listarExemplaresPorIsbn(isbn);
        if (exemplares.isEmpty()) System.out.println("  Nenhum exemplar encontrado.");
        else for (int i = 0; i < exemplares.tamanho(); i++) System.out.println("  " + exemplares.get(i));
    }

    private void removerLivro() {
        String isbn = EntradaConsole.lerString(scanner, "  ISBN do livro a remover: ");
        undoRedoService.executarAcao(new AcaoRemoverLivro(catalogoService, isbn));
    }

    // Recalcula contadores a partir dos dados ja carregados no servico
    public void recarregarContadores() {
        this.proximoAutorId = calcularProximoAutorId();
        this.proximoCategoriaId = calcularProximoCategoriaId();
    }

    private int calcularProximoAutorId() {
        int max = 0;
        Vetor<Livro> livros = catalogoService.listarLivros();
        for (int i = 0; i < livros.tamanho(); i++) {
            Vetor<Autor> autores = livros.get(i).getAutores();
            for (int j = 0; j < autores.tamanho(); j++) {
                if (autores.get(j).getId() > max) max = autores.get(j).getId();
            }
        }
        return max + 1;
    }

    private int calcularProximoCategoriaId() {
        int max = 0;
        Vetor<Livro> livros = catalogoService.listarLivros();
        for (int i = 0; i < livros.tamanho(); i++) {
            Vetor<Categoria> cats = livros.get(i).getCategorias();
            for (int j = 0; j < cats.tamanho(); j++) {
                if (cats.get(j).getId() > max) max = cats.get(j).getId();
            }
        }
        return max + 1;
    }
}
