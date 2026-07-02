package br.edu.biblioteca.repository;

import br.edu.biblioteca.model.Exemplar;
import br.edu.biblioteca.model.StatusExemplar;
import br.edu.biblioteca.structures.Vetor;

public class ExemplarRepository {

    private static final String ARQUIVO = "exemplares.csv";

    // Formato: id;isbnLivro;status
    public void salvar(Vetor<Exemplar> exemplares) {
        Vetor<String> linhas = new Vetor<>();
        for (int i = 0; i < exemplares.tamanho(); i++) {
            Exemplar e = exemplares.get(i);
            linhas.add(e.getId() + ";" + e.getIsbnLivro() + ";" + e.getStatus().name());
        }
        FileStorage.escrever(ARQUIVO, linhas);
        System.out.println("  [ExemplarRepository] " + exemplares.tamanho() + " exemplar(es) salvos.");
    }

    public Vetor<Exemplar> carregar() {
        Vetor<Exemplar> exemplares = new Vetor<>();
        Vetor<String> linhas = FileStorage.ler(ARQUIVO);
        for (int i = 0; i < linhas.tamanho(); i++) {
            try {
                String[] p = linhas.get(i).split(";");
                if (p.length < 3) continue;
                int id = Integer.parseInt(p[0].trim());
                String isbn = p[1].trim();
                StatusExemplar status = StatusExemplar.valueOf(p[2].trim());
                Exemplar ex = new Exemplar(id, isbn);
                ex.setStatus(status);
                exemplares.add(ex);
            } catch (Exception e) {
                System.out.println("  [ExemplarRepository] Linha invalida: " + linhas.get(i));
            }
        }
        System.out.println("  [ExemplarRepository] " + exemplares.tamanho() + " exemplar(es) carregados.");
        return exemplares;
    }
}
