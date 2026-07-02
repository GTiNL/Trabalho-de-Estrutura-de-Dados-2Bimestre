package br.edu.biblioteca.repository;

import br.edu.biblioteca.model.TipoUsuario;
import br.edu.biblioteca.model.Usuario;
import br.edu.biblioteca.structures.Vetor;

public class UsuarioRepository {

    private static final String ARQUIVO = "usuarios.csv";

    // Formato: id;nome;tipo;email;bloqueado
    public void salvar(Vetor<Usuario> usuarios) {
        Vetor<String> linhas = new Vetor<>();
        for (int i = 0; i < usuarios.tamanho(); i++) {
            Usuario u = usuarios.get(i);
            linhas.add(u.getId() + ";" + u.getNome() + ";" + u.getTipo().name()
                    + ";" + u.getEmail() + ";" + u.isBloqueado());
        }
        FileStorage.escrever(ARQUIVO, linhas);
        System.out.println("  [UsuarioRepository] " + usuarios.tamanho() + " usuario(s) salvos.");
    }

    public Vetor<Usuario> carregar() {
        Vetor<Usuario> usuarios = new Vetor<>();
        Vetor<String> linhas = FileStorage.ler(ARQUIVO);
        for (int i = 0; i < linhas.tamanho(); i++) {
            try {
                String[] p = linhas.get(i).split(";", 5);
                if (p.length < 5) continue;
                int id = Integer.parseInt(p[0].trim());
                String nome = p[1].trim();
                TipoUsuario tipo = TipoUsuario.valueOf(p[2].trim());
                String email = p[3].trim();
                boolean bloqueado = Boolean.parseBoolean(p[4].trim());
                Usuario u = new Usuario(id, nome, tipo, email);
                u.setBloqueado(bloqueado);
                usuarios.add(u);
            } catch (Exception e) {
                System.out.println("  [UsuarioRepository] Linha invalida: " + linhas.get(i));
            }
        }
        System.out.println("  [UsuarioRepository] " + usuarios.tamanho() + " usuario(s) carregados.");
        return usuarios;
    }
}
