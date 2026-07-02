package br.edu.biblioteca.ui;

import br.edu.biblioteca.service.RelatorioService;

import java.util.Scanner;

public class TelaRelatorios {

    private final Scanner scanner;
    private final RelatorioService relatorioService;

    public TelaRelatorios(Scanner scanner, RelatorioService relatorioService) {
        this.scanner = scanner;
        this.relatorioService = relatorioService;
    }

    public void exibir() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- RELATORIOS ---");
            System.out.println("  1. Top livros mais emprestados (Selection Sort)");
            System.out.println("  2. Emprestimos em atraso");
            System.out.println("  3. Usuarios com mais atrasos (Insertion Sort)");
            System.out.println("  4. Estatisticas mensais (Matriz 12 x categorias)");
            System.out.println("  0. Voltar");

            int opcao = EntradaConsole.lerInteiro(scanner, "Opcao: ");

            if (opcao == 1) {
                int top = EntradaConsole.lerInteiro(scanner, "  Quantos livros no ranking? ");
                relatorioService.topMaisEmprestados(top);
            } else if (opcao == 2) {
                relatorioService.emAtraso();
            } else if (opcao == 3) {
                int top = EntradaConsole.lerInteiro(scanner, "  Quantos usuarios no ranking? ");
                relatorioService.usuariosComMaisAtrasos(top);
            } else if (opcao == 4) {
                relatorioService.estatisticasMensais();
            } else if (opcao == 0) {
                voltar = true;
            } else {
                System.out.println("  Opcao invalida.");
            }
        }
    }
}
