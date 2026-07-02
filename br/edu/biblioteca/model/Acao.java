package br.edu.biblioteca.model;

// Representa uma acao realizada no sistema (para historico de Undo/Redo)
public class Acao {

    private String tipo;      // ex: "CADASTRO_LIVRO", "EMPRESTIMO", "DEVOLUCAO"
    private String descricao; // ex: "Cadastrou livro Dom Casmurro"

    public Acao(String tipo, String descricao) {
        this.tipo = tipo;
        this.descricao = descricao;
    }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String toString() {
        return "[" + tipo + "] " + descricao;
    }
}
