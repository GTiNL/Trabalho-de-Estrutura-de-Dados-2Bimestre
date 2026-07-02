package br.edu.biblioteca.ui;

import br.edu.biblioteca.action.AcaoCancelarReserva;
import br.edu.biblioteca.action.AcaoReservar;
import br.edu.biblioteca.model.Reserva;
import br.edu.biblioteca.service.ReservaService;
import br.edu.biblioteca.service.UndoRedoService;
import br.edu.biblioteca.structures.Vetor;

import java.util.Scanner;

public class TelaReservas {

    private final Scanner scanner;
    private final ReservaService reservaService;
    private final UndoRedoService undoRedoService;

    public TelaReservas(Scanner scanner, ReservaService reservaService, UndoRedoService undoRedoService) {
        this.scanner = scanner;
        this.reservaService = reservaService;
        this.undoRedoService = undoRedoService;
    }

    public void exibir() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- RESERVAS ---");
            System.out.println("  1. Reservar livro");
            System.out.println("  2. Cancelar reserva");
            System.out.println("  3. Atender proxima reserva (por ISBN)");
            System.out.println("  4. Listar reservas ativas");
            System.out.println("  5. Listar todas as reservas");
            System.out.println("  0. Voltar");

            int opcao = EntradaConsole.lerInteiro(scanner, "Opcao: ");

            if (opcao == 1) reservar();
            else if (opcao == 2) cancelar();
            else if (opcao == 3) atender();
            else if (opcao == 4) listarAtivas();
            else if (opcao == 5) listarTodas();
            else if (opcao == 0) voltar = true;
            else System.out.println("  Opcao invalida.");
        }
    }

    private void reservar() {
        System.out.println("\n  -- Reservar Livro --");
        int uid = EntradaConsole.lerInteiro(scanner, "  ID do usuario: ");
        String isbn = EntradaConsole.lerString(scanner, "  ISBN do livro: ");
        undoRedoService.executarAcao(new AcaoReservar(reservaService, uid, isbn));
    }

    private void cancelar() {
        int id = EntradaConsole.lerInteiro(scanner, "  ID da reserva: ");
        undoRedoService.executarAcao(new AcaoCancelarReserva(reservaService, id));
    }

    private void atender() {
        String isbn = EntradaConsole.lerString(scanner, "  ISBN do livro: ");
        reservaService.atenderProximaReserva(isbn);
    }

    private void listarAtivas() {
        Vetor<Reserva> lista = reservaService.listarReservasAtivas();
        System.out.println("\n  Reservas ativas (" + lista.tamanho() + "):");
        for (int i = 0; i < lista.tamanho(); i++) System.out.println("  " + lista.get(i));
    }

    private void listarTodas() {
        Vetor<Reserva> lista = reservaService.listarReservas();
        System.out.println("\n  Total de reservas: " + lista.tamanho());
        for (int i = 0; i < lista.tamanho(); i++) System.out.println("  " + lista.get(i));
    }
}
