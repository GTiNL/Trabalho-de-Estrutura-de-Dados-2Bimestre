package br.edu.biblioteca.ui;

import br.edu.biblioteca.model.TipoUsuario;
import br.edu.biblioteca.model.Usuario;
import br.edu.biblioteca.service.UsuarioService;
import br.edu.biblioteca.structures.Vetor;

import java.util.Scanner;

public class TelaUsuarios {

    private final Scanner scanner;
    private final UsuarioService usuarioService;

    public TelaUsuarios(Scanner scanner, UsuarioService usuarioService) {
        this.scanner = scanner;
        this.usuarioService = usuarioService;
    }

    public void exibir() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- USUARIOS ---");
            System.out.println("  1. Cadastrar usuario");
            System.out.println("  2. Buscar por ID");
            System.out.println("  3. Buscar por nome");
            System.out.println("  4. Listar todos");
            System.out.println("  5. Bloquear usuario");
            System.out.println("  6. Desbloquear usuario");
            System.out.println("  0. Voltar");

            int opcao = EntradaConsole.lerInteiro(scanner, "Opcao: ");

            if (opcao == 1) cadastrar();
            else if (opcao == 2) buscarPorId();
            else if (opcao == 3) buscarPorNome();
            else if (opcao == 4) listar();
            else if (opcao == 5) bloquear();
            else if (opcao == 6) desbloquear();
            else if (opcao == 0) voltar = true;
            else System.out.println("  Opcao invalida.");
        }
    }

    private void cadastrar() {
        System.out.println("\n  -- Cadastrar Usuario --");
        String nome = EntradaConsole.lerString(scanner, "  Nome: ");
        System.out.println("  Tipo: 1=ALUNO  2=PROFESSOR  3=BIBLIOTECARIO");
        int t = EntradaConsole.lerInteiro(scanner, "  Escolha: ");
        TipoUsuario tipo = (t == 2) ? TipoUsuario.PROFESSOR : (t == 3) ? TipoUsuario.BIBLIOTECARIO : TipoUsuario.ALUNO;
        String email = EntradaConsole.lerString(scanner, "  E-mail: ");
        Usuario u = usuarioService.cadastrarUsuario(nome, tipo, email);
        System.out.println("  Usuario cadastrado: " + u);
    }

    private void buscarPorId() {
        Usuario u = usuarioService.buscarPorId(EntradaConsole.lerInteiro(scanner, "  ID: "));
        System.out.println(u != null ? "  " + u : "  Usuario nao encontrado.");
    }

    private void buscarPorNome() {
        Vetor<Usuario> res = usuarioService.buscarPorNome(EntradaConsole.lerString(scanner, "  Nome (parcial): "));
        if (res.isEmpty()) System.out.println("  Nenhum usuario encontrado.");
        else for (int i = 0; i < res.tamanho(); i++) System.out.println("  " + res.get(i));
    }

    private void listar() {
        Vetor<Usuario> lista = usuarioService.listar();
        System.out.println("\n  Total: " + lista.tamanho() + " usuario(s).");
        for (int i = 0; i < lista.tamanho(); i++) System.out.println("  " + lista.get(i));
    }

    private void bloquear() {
        int id = EntradaConsole.lerInteiro(scanner, "  ID do usuario: ");
        System.out.println(usuarioService.bloquear(id) ? "  Usuario bloqueado." : "  Nao encontrado.");
    }

    private void desbloquear() {
        int id = EntradaConsole.lerInteiro(scanner, "  ID do usuario: ");
        System.out.println(usuarioService.desbloquear(id) ? "  Usuario desbloqueado." : "  Nao encontrado.");
    }
}
