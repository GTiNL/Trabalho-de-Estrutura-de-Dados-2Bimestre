package br.edu.biblioteca.ui;

import br.edu.biblioteca.action.AcaoDevolver;
import br.edu.biblioteca.action.AcaoEmpresta;
import br.edu.biblioteca.model.Emprestimo;
import br.edu.biblioteca.model.Multa;
import br.edu.biblioteca.model.Notificacao;
import br.edu.biblioteca.service.CatalogoService;
import br.edu.biblioteca.service.EmprestimoService;
import br.edu.biblioteca.service.UndoRedoService;
import br.edu.biblioteca.structures.Vetor;

import java.util.Scanner;

public class TelaEmprestimos {

    private final Scanner scanner;
    private final EmprestimoService emprestimoService;
    private final CatalogoService catalogoService;
    private final UndoRedoService undoRedoService;

    public TelaEmprestimos(Scanner scanner, EmprestimoService emprestimoService,
                           CatalogoService catalogoService, UndoRedoService undoRedoService) {
        this.scanner = scanner;
        this.emprestimoService = emprestimoService;
        this.catalogoService = catalogoService;
        this.undoRedoService = undoRedoService;
    }

    public void exibir() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- EMPRESTIMOS E DEVOLUCOES ---");
            System.out.println("  1. Realizar emprestimo");
            System.out.println("  2. Devolver exemplar");
            System.out.println("  3. Renovar emprestimo");
            System.out.println("  4. Listar emprestimos ativos");
            System.out.println("  5. Listar emprestimos em atraso");
            System.out.println("  6. Listar todos os emprestimos");
            System.out.println("  7. Emprestimos por usuario");
            System.out.println("  8. Multas");
            System.out.println("  9. Notificacoes");
            System.out.println("  0. Voltar");

            int opcao = EntradaConsole.lerInteiro(scanner, "Opcao: ");

            if (opcao == 1) realizarEmprestimo();
            else if (opcao == 2) devolverExemplar();
            else if (opcao == 3) renovar();
            else if (opcao == 4) listarAtivos();
            else if (opcao == 5) listarEmAtraso();
            else if (opcao == 6) listarTodos();
            else if (opcao == 7) listarPorUsuario();
            else if (opcao == 8) menuMultas();
            else if (opcao == 9) menuNotificacoes();
            else if (opcao == 0) voltar = true;
            else System.out.println("  Opcao invalida.");
        }
    }

    private void realizarEmprestimo() {
        System.out.println("\n  -- Realizar Emprestimo --");
        int usuId = EntradaConsole.lerInteiro(scanner, "  ID do usuario: ");
        int exId  = EntradaConsole.lerInteiro(scanner, "  ID do exemplar: ");
        undoRedoService.executarAcao(new AcaoEmpresta(emprestimoService, usuId, exId));
    }

    private void devolverExemplar() {
        System.out.println("\n  -- Devolver Exemplar --");
        int exId = EntradaConsole.lerInteiro(scanner, "  ID do exemplar: ");
        undoRedoService.executarAcao(new AcaoDevolver(emprestimoService, catalogoService, exId));
    }

    private void renovar() {
        int id = EntradaConsole.lerInteiro(scanner, "  ID do emprestimo: ");
        boolean ok = emprestimoService.renovar(id);
        if (ok) System.out.println("  Emprestimo #" + id + " renovado.");
    }

    private void listarAtivos() {
        Vetor<Emprestimo> lista = emprestimoService.listarEmprestimosAtivos();
        System.out.println("\n  Emprestimos ativos (" + lista.tamanho() + "):");
        for (int i = 0; i < lista.tamanho(); i++) System.out.println("  " + lista.get(i));
    }

    private void listarEmAtraso() {
        Vetor<Emprestimo> lista = emprestimoService.listarEmprestimosEmAtraso();
        System.out.println("\n  Em atraso (" + lista.tamanho() + "):");
        for (int i = 0; i < lista.tamanho(); i++) System.out.println("  " + lista.get(i));
    }

    private void listarTodos() {
        Vetor<Emprestimo> lista = emprestimoService.listarEmprestimos();
        System.out.println("\n  Total de emprestimos: " + lista.tamanho());
        for (int i = 0; i < lista.tamanho(); i++) System.out.println("  " + lista.get(i));
    }

    private void listarPorUsuario() {
        int uid = EntradaConsole.lerInteiro(scanner, "  ID do usuario: ");
        Vetor<Emprestimo> lista = emprestimoService.listarEmprestimosPorUsuario(uid);
        System.out.println("\n  Emprestimos do usuario " + uid + " (" + lista.tamanho() + "):");
        for (int i = 0; i < lista.tamanho(); i++) System.out.println("  " + lista.get(i));
    }

    private void menuMultas() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n  --- MULTAS ---");
            System.out.println("    1. Multas pendentes");
            System.out.println("    2. Todas as multas");
            System.out.println("    3. Quitar multa");
            System.out.println("    0. Voltar");
            int op = EntradaConsole.lerInteiro(scanner, "  Opcao: ");
            if (op == 1) {
                Vetor<Multa> p = emprestimoService.listarMultasPendentes();
                System.out.println("\n  Pendentes (" + p.tamanho() + "):");
                for (int i = 0; i < p.tamanho(); i++) System.out.println("  " + p.get(i));
            } else if (op == 2) {
                Vetor<Multa> todas = emprestimoService.listarMultas();
                System.out.println("\n  Total (" + todas.tamanho() + "):");
                for (int i = 0; i < todas.tamanho(); i++) System.out.println("  " + todas.get(i));
            } else if (op == 3) {
                int id = EntradaConsole.lerInteiro(scanner, "  ID da multa: ");
                System.out.println(emprestimoService.quitarMulta(id) ? "  Multa quitada!" : "  Nao encontrada.");
            } else if (op == 0) {
                voltar = true;
            }
        }
    }

    private void menuNotificacoes() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n  --- NOTIFICACOES ---");
            System.out.println("    1. Por usuario");
            System.out.println("    2. Todas");
            System.out.println("    0. Voltar");
            int op = EntradaConsole.lerInteiro(scanner, "  Opcao: ");
            if (op == 1) {
                int uid = EntradaConsole.lerInteiro(scanner, "  ID do usuario: ");
                Vetor<Notificacao> n = emprestimoService.listarNotificacoesPorUsuario(uid);
                System.out.println("\n  Notificacoes (" + n.tamanho() + "):");
                for (int i = 0; i < n.tamanho(); i++) System.out.println("  " + n.get(i));
            } else if (op == 2) {
                Vetor<Notificacao> n = emprestimoService.listarNotificacoes();
                System.out.println("\n  Total (" + n.tamanho() + "):");
                for (int i = 0; i < n.tamanho(); i++) System.out.println("  " + n.get(i));
            } else if (op == 0) {
                voltar = true;
            }
        }
    }
}
