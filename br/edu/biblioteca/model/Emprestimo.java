package br.edu.biblioteca.model;

import java.time.LocalDate;

public class Emprestimo {

    private int id;
    private int usuarioId;
    private int exemplarId;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevista;
    private LocalDate dataDevolucao;
    private StatusEmprestimo status;

    public Emprestimo(int id, int usuarioId, int exemplarId,
                      LocalDate dataEmprestimo, LocalDate dataPrevista) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.exemplarId = exemplarId;
        this.dataEmprestimo = dataEmprestimo;
        this.dataPrevista = dataPrevista;
        this.dataDevolucao = null;
        this.status = StatusEmprestimo.ATIVO;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public int getExemplarId() { return exemplarId; }
    public void setExemplarId(int exemplarId) { this.exemplarId = exemplarId; }

    public LocalDate getDataEmprestimo() { return dataEmprestimo; }
    public void setDataEmprestimo(LocalDate dataEmprestimo) { this.dataEmprestimo = dataEmprestimo; }

    public LocalDate getDataPrevista() { return dataPrevista; }
    public void setDataPrevista(LocalDate dataPrevista) { this.dataPrevista = dataPrevista; }

    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(LocalDate dataDevolucao) { this.dataDevolucao = dataDevolucao; }

    public StatusEmprestimo getStatus() { return status; }
    public void setStatus(StatusEmprestimo status) { this.status = status; }

    public String toString() {
        String devol = "Pendente";
        if (dataDevolucao != null) {
            devol = dataDevolucao.toString();
        }
        return "Emprestimo #" + id
                + " | UsuarioID: " + usuarioId
                + " | ExemplarID: " + exemplarId
                + " | Emprestimo: " + dataEmprestimo
                + " | Prevista: " + dataPrevista
                + " | Devolucao: " + devol
                + " | Status: " + status;
    }
}
