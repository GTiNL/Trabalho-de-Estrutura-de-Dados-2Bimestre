package br.edu.biblioteca.action;

public interface Acao {
    boolean executar();
    boolean desfazer();
    String descricao();
}
