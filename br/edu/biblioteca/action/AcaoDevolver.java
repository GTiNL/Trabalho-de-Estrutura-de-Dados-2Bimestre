package br.edu.biblioteca.action;

import br.edu.biblioteca.model.Emprestimo;
import br.edu.biblioteca.model.Exemplar;
import br.edu.biblioteca.model.StatusEmprestimo;
import br.edu.biblioteca.model.StatusExemplar;
import br.edu.biblioteca.service.CatalogoService;
import br.edu.biblioteca.service.EmprestimoService;

public class AcaoDevolver implements Acao {

    private final EmprestimoService emprestimoService;
    private final CatalogoService catalogoService;
    private final int exemplarId;
    private Emprestimo emprestimoDevolvido;
    private StatusEmprestimo statusAnterior;

    public AcaoDevolver(EmprestimoService emprestimoService, CatalogoService catalogoService, int exemplarId) {
        this.emprestimoService = emprestimoService;
        this.catalogoService = catalogoService;
        this.exemplarId = exemplarId;
    }

    @Override
    public boolean executar() {
        emprestimoDevolvido = emprestimoService.buscarEmprestimoAtivoPorExemplar(exemplarId);
        if (emprestimoDevolvido == null) return false;
        statusAnterior = emprestimoDevolvido.getStatus();
        Emprestimo resultado = emprestimoService.devolverExemplar(exemplarId);
        if (resultado != null) System.out.println("  Devolucao registrada: " + resultado);
        return resultado != null;
    }

    @Override
    public boolean desfazer() {
        if (emprestimoDevolvido == null) return false;
        // Restaura o estado do emprestimo
        emprestimoDevolvido.setStatus(statusAnterior);
        emprestimoDevolvido.setDataDevolucao(null);
        // Restaura o exemplar para EMPRESTADO
        Exemplar ex = catalogoService.buscarExemplarPorId(exemplarId);
        if (ex != null) ex.setStatus(StatusExemplar.EMPRESTADO);
        System.out.println("  Devolucao desfeita: exemplar #" + exemplarId + " reativado.");
        return true;
    }

    @Override
    public String descricao() {
        return "Devolver exemplar #" + exemplarId;
    }
}
