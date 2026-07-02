package br.edu.biblioteca.model;

public class Usuario {

    private int id;
    private String nome;
    private TipoUsuario tipo;
    private String email;
    private boolean bloqueado;

    public Usuario(int id, String nome, TipoUsuario tipo, String email) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.email = email;
        this.bloqueado = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public TipoUsuario getTipo() { return tipo; }
    public void setTipo(TipoUsuario tipo) { this.tipo = tipo; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isBloqueado() { return bloqueado; }
    public void setBloqueado(boolean bloqueado) { this.bloqueado = bloqueado; }

    public String toString() {
        String statusBloq = "Nao";
        if (bloqueado) {
            statusBloq = "Sim";
        }
        return "ID: " + id + " | Nome: " + nome + " | Tipo: " + tipo
                + " | Email: " + email + " | Bloqueado: " + statusBloq;
    }
}
