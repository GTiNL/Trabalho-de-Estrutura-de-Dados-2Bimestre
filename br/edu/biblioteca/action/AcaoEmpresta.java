package br.edu.biblioteca.action;

import br.edu.biblioteca.model.Emprestimo;
import br.edu.biblioteca.service.EmprestimoService;

public class AcaoEmpresta implements Acao {

    private final EmprestimoService emprestimoService;
    private final int usuarioId;
    private final int exemplarId;
    private Emprestimo emprestimoRealizado;

    public AcaoEmpresta(EmprestimoService emprestimoService, int usuarioId, int exemplarId) {
        this.emprestimoService = emprestimoService;
        this.usuarioId = usuarioId;
        this.exemplarId = exemplarId;
    }

    @Override
    public boolean executar() {
        emprestimoRealizado = emprestimoService.emprestarExemplar(usuarioId, exemplarId);
        if (emprestimoRealizado != null) {
            System.out.println("  Emprestimo realizado: " + emprestimoRealizado);
        }
        return emprestimoRealizado != null;
    }

    @Override
    public boolean desfazer() {
        if (emprestimoRealizado == null) return false;
        Emprestimo devolvido = emprestimoService.devolverExemplar(exemplarId);
        if (devolvido != null) System.out.println("  Emprestimo desfeito (exemplar devolvido).");
        return devolvido != null;
    }

    @Override
    public String descricao() {
        return "Emprestar exemplar #" + exemplarId + " para usuario #" + usuarioId;
    }
}
