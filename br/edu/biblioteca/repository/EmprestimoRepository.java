package br.edu.biblioteca.repository;

import br.edu.biblioteca.model.Emprestimo;
import br.edu.biblioteca.model.Multa;
import br.edu.biblioteca.model.StatusEmprestimo;
import br.edu.biblioteca.structures.Vetor;

import java.time.LocalDate;

public class EmprestimoRepository {

    private static final String ARQUIVO_EMP = "emprestimos.csv";
    private static final String ARQUIVO_MULTA = "multas.csv";

    // Formato: id;usuarioId;exemplarId;dataEmprestimo;dataPrevista;dataDevolucao;status
    public void salvarEmprestimos(Vetor<Emprestimo> emprestimos) {
        Vetor<String> linhas = new Vetor<>();
        for (int i = 0; i < emprestimos.tamanho(); i++) {
            Emprestimo e = emprestimos.get(i);
            String dataDev = e.getDataDevolucao() != null ? e.getDataDevolucao().toString() : "";
            linhas.add(e.getId() + ";" + e.getUsuarioId() + ";" + e.getExemplarId()
                    + ";" + e.getDataEmprestimo() + ";" + e.getDataPrevista()
                    + ";" + dataDev + ";" + e.getStatus().name());
        }
        FileStorage.escrever(ARQUIVO_EMP, linhas);
        System.out.println("  [EmprestimoRepository] " + emprestimos.tamanho() + " emprestimo(s) salvos.");
    }

    public Vetor<Emprestimo> carregarEmprestimos() {
        Vetor<Emprestimo> lista = new Vetor<>();
        Vetor<String> linhas = FileStorage.ler(ARQUIVO_EMP);
        for (int i = 0; i < linhas.tamanho(); i++) {
            try {
                String[] p = linhas.get(i).split(";", 7);
                if (p.length < 7) continue;
                int id = Integer.parseInt(p[0].trim());
                int usuId = Integer.parseInt(p[1].trim());
                int exId = Integer.parseInt(p[2].trim());
                LocalDate dataEmp = LocalDate.parse(p[3].trim());
                LocalDate dataPrev = LocalDate.parse(p[4].trim());
                LocalDate dataDev = p[5].trim().isEmpty() ? null : LocalDate.parse(p[5].trim());
                StatusEmprestimo status = StatusEmprestimo.valueOf(p[6].trim());
                Emprestimo e = new Emprestimo(id, usuId, exId, dataEmp, dataPrev);
                e.setDataDevolucao(dataDev);
                e.setStatus(status);
                lista.add(e);
            } catch (Exception e) {
                System.out.println("  [EmprestimoRepository] Linha invalida: " + linhas.get(i));
            }
        }
        System.out.println("  [EmprestimoRepository] " + lista.tamanho() + " emprestimo(s) carregados.");
        return lista;
    }

    // Formato multa: id;emprestimoId;valor;diasAtraso;quitada
    public void salvarMultas(Vetor<Multa> multas) {
        Vetor<String> linhas = new Vetor<>();
        for (int i = 0; i < multas.tamanho(); i++) {
            Multa m = multas.get(i);
            linhas.add(m.getId() + ";" + m.getEmprestimoId() + ";" + m.getValor()
                    + ";" + m.getDiasAtraso() + ";" + m.isQuitada());
        }
        FileStorage.escrever(ARQUIVO_MULTA, linhas);
        System.out.println("  [EmprestimoRepository] " + multas.tamanho() + " multa(s) salvas.");
    }

    public Vetor<Multa> carregarMultas() {
        Vetor<Multa> lista = new Vetor<>();
        Vetor<String> linhas = FileStorage.ler(ARQUIVO_MULTA);
        for (int i = 0; i < linhas.tamanho(); i++) {
            try {
                String[] p = linhas.get(i).split(";");
                if (p.length < 5) continue;
                int id = Integer.parseInt(p[0].trim());
                int empId = Integer.parseInt(p[1].trim());
                double valor = Double.parseDouble(p[2].trim());
                int dias = Integer.parseInt(p[3].trim());
                boolean quitada = Boolean.parseBoolean(p[4].trim());
                Multa m = new Multa(id, empId, valor, dias);
                m.setQuitada(quitada);
                lista.add(m);
            } catch (Exception e) {
                System.out.println("  [EmprestimoRepository] Linha invalida multa: " + linhas.get(i));
            }
        }
        System.out.println("  [EmprestimoRepository] " + lista.tamanho() + " multa(s) carregadas.");
        return lista;
    }
}
