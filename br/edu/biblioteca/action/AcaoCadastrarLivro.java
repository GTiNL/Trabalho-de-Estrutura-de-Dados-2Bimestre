package br.edu.biblioteca.action;

import br.edu.biblioteca.model.Livro;
import br.edu.biblioteca.service.CatalogoService;

public class AcaoCadastrarLivro implements Acao {

    private final CatalogoService catalogoService;
    private final Livro livro;

    public AcaoCadastrarLivro(CatalogoService catalogoService, Livro livro) {
        this.catalogoService = catalogoService;
        this.livro = livro;
    }

    @Override
    public boolean executar() {
        boolean ok = catalogoService.cadastrarLivro(livro);
        if (!ok) System.out.println("  [ERRO] Ja existe um livro com ISBN: " + livro.getIsbn());
        return ok;
    }

    @Override
    public boolean desfazer() {
        return catalogoService.removerLivro(livro.getIsbn()) != null;
    }

    @Override
    public String descricao() {
        return "Cadastrar livro: " + livro.getTitulo() + " (ISBN: " + livro.getIsbn() + ")";
    }
}
