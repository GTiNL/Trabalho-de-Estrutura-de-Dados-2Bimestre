package br.edu.biblioteca.service;

import br.edu.biblioteca.model.TipoUsuario;
import br.edu.biblioteca.model.Usuario;
import br.edu.biblioteca.structures.Vetor;

// Servico de gerenciamento de usuarios
public class UsuarioService {

    private Vetor<Usuario> usuarios;
    private int proximoId;

    public UsuarioService() {
        this.usuarios = new Vetor<>();
        this.proximoId = 1;
    }

    // Cadastra um novo usuario
    public Usuario cadastrarUsuario(String nome, TipoUsuario tipo, String email) {
        Usuario usuario = new Usuario(proximoId, nome, tipo, email);
        proximoId++;
        usuarios.add(usuario);
        return usuario;
    }

    // Busca usuario pelo ID
    public Usuario buscarPorId(int id) {
        for (int i = 0; i < usuarios.tamanho(); i++) {
            if (usuarios.get(i).getId() == id) {
                return usuarios.get(i);
            }
        }
        return null;
    }

    // Busca usuarios pelo nome (busca parcial)
    public Vetor<Usuario> buscarPorNome(String nome) {
        Vetor<Usuario> resultado = new Vetor<>();
        for (int i = 0; i < usuarios.tamanho(); i++) {
            String nomeUsuario = usuarios.get(i).getNome().toLowerCase();
            if (nomeUsuario.contains(nome.toLowerCase())) {
                resultado.add(usuarios.get(i));
            }
        }
        return resultado;
    }

    // Bloqueia um usuario
    public boolean bloquear(int id) {
        Usuario u = buscarPorId(id);
        if (u != null) {
            u.setBloqueado(true);
            return true;
        }
        return false;
    }

    // Desbloqueia um usuario
    public boolean desbloquear(int id) {
        Usuario u = buscarPorId(id);
        if (u != null) {
            u.setBloqueado(false);
            return true;
        }
        return false;
    }

    // Lista todos os usuarios
    public Vetor<Usuario> listar() {
        return usuarios;
    }

    // Lista usuarios por tipo
    public Vetor<Usuario> listarPorTipo(TipoUsuario tipo) {
        Vetor<Usuario> resultado = new Vetor<>();
        for (int i = 0; i < usuarios.tamanho(); i++) {
            if (usuarios.get(i).getTipo() == tipo) {
                resultado.add(usuarios.get(i));
            }
        }
        return resultado;
    }

    // Carrega um usuario persistido diretamente, sem gerar novo ID
    public void carregarUsuario(Usuario usuario) {
        usuarios.add(usuario);
        if (usuario.getId() >= proximoId) proximoId = usuario.getId() + 1;
    }

    public int totalUsuarios() {
        return usuarios.tamanho();
    }
}
