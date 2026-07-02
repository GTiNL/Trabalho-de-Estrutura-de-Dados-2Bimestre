package br.edu.biblioteca.action;

import br.edu.biblioteca.model.Livro;
import br.edu.biblioteca.service.CatalogoService;

public class AcaoRemoverLivro implements Acao {

    private final CatalogoService catalogoService;
    private final String isbn;
    private Livro livroRemovido;

    public AcaoRemoverLivro(CatalogoService catalogoService, String isbn) {
        this.catalogoService = catalogoService;
        this.isbn = isbn;
    }

    @Override
    public boolean executar() {
        livroRemovido = catalogoService.removerLivro(isbn);
        if (livroRemovido == null) System.out.println("  [ERRO] Livro nao encontrado: " + isbn);
        else System.out.println("  Livro removido: " + livroRemovido.getTitulo());
        return livroRemovido != null;
    }

    @Override
    public boolean desfazer() {
        if (livroRemovido == null) return false;
        boolean ok = catalogoService.cadastrarLivro(livroRemovido);
        if (ok) System.out.println("  Livro restaurado: " + livroRemovido.getTitulo());
        return ok;
    }

    @Override
    public String descricao() {
        return "Remover livro ISBN: " + isbn;
    }
}
