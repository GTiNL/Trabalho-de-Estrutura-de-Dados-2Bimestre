package br.edu.biblioteca.repository;

import br.edu.biblioteca.model.Reserva;
import br.edu.biblioteca.model.StatusReserva;
import br.edu.biblioteca.structures.Vetor;

import java.time.LocalDate;

public class ReservaRepository {

    private static final String ARQUIVO = "reservas.csv";

    // Formato: id;usuarioId;isbnLivro;dataReserva;status
    public void salvar(Vetor<Reserva> reservas) {
        Vetor<String> linhas = new Vetor<>();
        for (int i = 0; i < reservas.tamanho(); i++) {
            Reserva r = reservas.get(i);
            linhas.add(r.getId() + ";" + r.getUsuarioId() + ";" + r.getIsbnLivro()
                    + ";" + r.getDataReserva() + ";" + r.getStatus().name());
        }
        FileStorage.escrever(ARQUIVO, linhas);
        System.out.println("  [ReservaRepository] " + reservas.tamanho() + " reserva(s) salvas.");
    }

    public Vetor<Reserva> carregar() {
        Vetor<Reserva> reservas = new Vetor<>();
        Vetor<String> linhas = FileStorage.ler(ARQUIVO);
        for (int i = 0; i < linhas.tamanho(); i++) {
            try {
                String[] p = linhas.get(i).split(";", 5);
                if (p.length < 5) continue;
                int id = Integer.parseInt(p[0].trim());
                int usuId = Integer.parseInt(p[1].trim());
                String isbn = p[2].trim();
                LocalDate data = LocalDate.parse(p[3].trim());
                StatusReserva status = StatusReserva.valueOf(p[4].trim());
                Reserva r = new Reserva(id, usuId, isbn, data);
                r.setStatus(status);
                reservas.add(r);
            } catch (Exception e) {
                System.out.println("  [ReservaRepository] Linha invalida: " + linhas.get(i));
            }
        }
        System.out.println("  [ReservaRepository] " + reservas.tamanho() + " reserva(s) carregadas.");
        return reservas;
    }
}
