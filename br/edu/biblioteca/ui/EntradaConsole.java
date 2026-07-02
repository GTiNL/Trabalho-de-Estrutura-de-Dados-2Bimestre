package br.edu.biblioteca.ui;

import java.util.Scanner;

public class EntradaConsole {

    public static String lerString(Scanner scanner, String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine().trim();
    }

    public static int lerInteiro(Scanner scanner, String mensagem) {
        while (true) {
            System.out.print(mensagem);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Valor invalido. Digite um numero inteiro.");
            }
        }
    }
}
