package br.edu.biblioteca.ui;

import br.edu.biblioteca.model.*;
import br.edu.biblioteca.repository.*;
import br.edu.biblioteca.service.*;
import br.edu.biblioteca.structures.Vetor;

import java.io.File;

import java.util.Scanner;

public class MenuPrincipal {

    private final Scanner scanner;

    // Servicos
    private final CatalogoService catalogoService;
    private final UsuarioService usuarioService;
    private final EmprestimoService emprestimoService;
    private final ReservaService reservaService;
    private final RelatorioService relatorioService;
    private final UndoRedoService undoRedoService;

    // Repositorios
    private final LivroRepository livroRepository;
    private final ExemplarRepository exemplarRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmprestimoRepository emprestimoRepository;
    private final ReservaRepository reservaRepository;

    // Telas
    private final TelaCatalogo telaCatalogo;
    private final TelaUsuarios telaUsuarios;
    private final TelaEmprestimos telaEmprestimos;
    private final TelaReservas telaReservas;
    private final TelaRelatorios telaRelatorios;

    public MenuPrincipal() {
        this.scanner = new Scanner(System.in);

        this.catalogoService   = new CatalogoService();
        this.usuarioService    = new UsuarioService();
        this.emprestimoService = new EmprestimoService(catalogoService, usuarioService);
        this.reservaService    = new ReservaService(catalogoService, usuarioService);
        this.relatorioService  = new RelatorioService(catalogoService, usuarioService, emprestimoService);
        this.undoRedoService   = new UndoRedoService();

        this.livroRepository      = new LivroRepository();
        this.exemplarRepository   = new ExemplarRepository();
        this.usuarioRepository    = new UsuarioRepository();
        this.emprestimoRepository = new EmprestimoRepository();
        this.reservaRepository    = new ReservaRepository();

        carregarDados();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[ShutdownHook] Salvando dados antes de encerrar...");
            salvarDados();
        }));

        this.telaCatalogo    = new TelaCatalogo(scanner, catalogoService, undoRedoService);
        this.telaUsuarios    = new TelaUsuarios(scanner, usuarioService);
        this.telaEmprestimos = new TelaEmprestimos(scanner, emprestimoService, catalogoService, undoRedoService);
        this.telaReservas    = new TelaReservas(scanner, reservaService, undoRedoService);
        this.telaRelatorios  = new TelaRelatorios(scanner, relatorioService);
    }

    public void exibir() {
        System.out.println("==============================================");
        System.out.println("   SISTEMA DE BIBLIOTECA");
        System.out.println("==============================================");

        boolean executando = true;
        while (executando) {
            exibirMenu();
            int opcao = EntradaConsole.lerInteiro(scanner, "Opcao: ");

            if (opcao == 1) telaCatalogo.exibir();
            else if (opcao == 2) telaUsuarios.exibir();
            else if (opcao == 3) telaEmprestimos.exibir();
            else if (opcao == 4) telaReservas.exibir();
            else if (opcao == 5) telaRelatorios.exibir();
            else if (opcao == 6) menuUndoRedo();
            else if (opcao == 0) {
                salvarDados();
                System.out.println("\nDados salvos. Ate logo!");
                executando = false;
            } else {
                System.out.println("  Opcao invalida.");
            }
        }
        scanner.close();
    }

    private void exibirMenu() {
        System.out.println("\n============= MENU PRINCIPAL =============");
        System.out.println("  1. Livros e Exemplares");
        System.out.println("  2. Usuarios");
        System.out.println("  3. Emprestimos e Devolucoes");
        System.out.println("  4. Reservas");
        System.out.println("  5. Relatorios");
        System.out.println("  6. Desfazer / Refazer (Undo/Redo)");
        System.out.println("  0. Sair (salva automaticamente)");
        System.out.println("==========================================");
    }

    private void menuUndoRedo() {
        boolean voltar = false;
        while (!voltar) {
            String proxUndo = undoRedoService.podeDesfazer() ? undoRedoService.proximoUndo() : "nenhum";
            String proxRedo = undoRedoService.podeRefazer() ? undoRedoService.proximoRedo() : "nenhum";
            System.out.println("\n--- DESFAZER / REFAZER ---");
            System.out.println("  Proximo undo: " + proxUndo);
            System.out.println("  Proximo redo: " + proxRedo);
            System.out.println("  1. Desfazer (Undo)");
            System.out.println("  2. Refazer  (Redo)");
            System.out.println("  0. Voltar");
            int op = EntradaConsole.lerInteiro(scanner, "Opcao: ");
            if (op == 1) undoRedoService.desfazer();
            else if (op == 2) undoRedoService.refazer();
            else if (op == 0) voltar = true;
        }
    }

    // ===== PERSISTENCIA =====

    private void carregarDados() {
        System.out.println("\nCarregando dados...");
        System.out.println("  Pasta de dados: " + new File(FileStorage.PASTA_DADOS).getAbsolutePath());

        Vetor<Livro> livros = livroRepository.carregar();
        for (int i = 0; i < livros.tamanho(); i++) catalogoService.cadastrarLivro(livros.get(i));

        Vetor<Exemplar> exemplares = exemplarRepository.carregar();
        for (int i = 0; i < exemplares.tamanho(); i++) catalogoService.carregarExemplar(exemplares.get(i));

        Vetor<Usuario> usuarios = usuarioRepository.carregar();
        for (int i = 0; i < usuarios.tamanho(); i++) usuarioService.carregarUsuario(usuarios.get(i));

        Vetor<Emprestimo> emprestimos = emprestimoRepository.carregarEmprestimos();
        for (int i = 0; i < emprestimos.tamanho(); i++) emprestimoService.carregarEmprestimo(emprestimos.get(i));

        Vetor<Multa> multas = emprestimoRepository.carregarMultas();
        for (int i = 0; i < multas.tamanho(); i++) emprestimoService.carregarMulta(multas.get(i));

        Vetor<Reserva> reservas = reservaRepository.carregar();
        for (int i = 0; i < reservas.tamanho(); i++) reservaService.carregarReserva(reservas.get(i));

        // Se nao havia dados persistidos, carrega exemplos para demonstracao
        if (catalogoService.totalLivros() == 0) {
            carregarDadosExemplo();
        } else {
            System.out.println("  Dados carregados do disco com sucesso!\n");
        }
    }

    private void salvarDados() {
        System.out.println("\nSalvando dados...");
        livroRepository.salvar(catalogoService.listarLivros());
        exemplarRepository.salvar(catalogoService.listarExemplares());
        usuarioRepository.salvar(usuarioService.listar());
        emprestimoRepository.salvarEmprestimos(emprestimoService.listarEmprestimos());
        emprestimoRepository.salvarMultas(emprestimoService.listarMultas());
        reservaRepository.salvar(reservaService.listarReservas());
    }

    private void carregarDadosExemplo() {
        System.out.println("  Nenhum dado encontrado. Carregando dados de exemplo...");

        Livro l1 = new Livro("978-85-333-0227-3", "Dom Casmurro", "Atica", 1899);
        l1.adicionarAutor(new Autor(1, "Machado de Assis"));
        l1.adicionarCategoria(new Categoria(1, "Literatura"));
        l1.adicionarPalavraChave("romance"); l1.adicionarPalavraChave("classico");

        Livro l2 = new Livro("978-85-359-0277-1", "Engenharia de Software", "Pearson", 2019);
        l2.adicionarAutor(new Autor(2, "Ian Sommerville"));
        l2.adicionarCategoria(new Categoria(2, "Computacao"));
        l2.adicionarPalavraChave("software");

        Livro l3 = new Livro("978-85-508-0148-5", "Estruturas de Dados", "UFU", 2020);
        l3.adicionarAutor(new Autor(3, "Andre Backes"));
        l3.adicionarCategoria(new Categoria(2, "Computacao"));
        l3.adicionarPalavraChave("algoritmos");

        Livro l4 = new Livro("978-85-7522-073-1", "O Pequeno Principe", "Agir", 1943);
        l4.adicionarAutor(new Autor(4, "Antoine de Saint-Exupery"));
        l4.adicionarCategoria(new Categoria(1, "Literatura"));
        l4.adicionarPalavraChave("fantasia");

        Livro l5 = new Livro("978-85-352-8534-5", "Calculo Vol. 1", "Cengage", 2013);
        l5.adicionarAutor(new Autor(5, "James Stewart"));
        l5.adicionarCategoria(new Categoria(3, "Matematica"));
        l5.adicionarPalavraChave("calculo");

        catalogoService.cadastrarLivro(l1); catalogoService.cadastrarLivro(l2);
        catalogoService.cadastrarLivro(l3); catalogoService.cadastrarLivro(l4);
        catalogoService.cadastrarLivro(l5);

        catalogoService.cadastrarExemplar(l1.getIsbn()); catalogoService.cadastrarExemplar(l1.getIsbn());
        catalogoService.cadastrarExemplar(l2.getIsbn()); catalogoService.cadastrarExemplar(l2.getIsbn());
        catalogoService.cadastrarExemplar(l3.getIsbn()); catalogoService.cadastrarExemplar(l3.getIsbn());
        catalogoService.cadastrarExemplar(l4.getIsbn());
        catalogoService.cadastrarExemplar(l5.getIsbn());

        usuarioService.cadastrarUsuario("Ana Silva",     TipoUsuario.ALUNO,     "ana@email.com");
        usuarioService.cadastrarUsuario("Prof. Carlos",  TipoUsuario.PROFESSOR, "carlos@email.com");
        usuarioService.cadastrarUsuario("Maria Souza",   TipoUsuario.BIBLIOTECARIO, "maria@email.com");
        usuarioService.cadastrarUsuario("Joao Santos",   TipoUsuario.ALUNO,     "joao@email.com");

        System.out.println("  5 livros, 8 exemplares e 4 usuarios de exemplo carregados.\n");
    }
}
