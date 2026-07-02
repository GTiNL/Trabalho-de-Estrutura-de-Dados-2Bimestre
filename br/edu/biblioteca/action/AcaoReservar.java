package br.edu.biblioteca.action;

import br.edu.biblioteca.model.Reserva;
import br.edu.biblioteca.service.ReservaService;

public class AcaoReservar implements Acao {

    private final ReservaService reservaService;
    private final int usuarioId;
    private final String isbn;
    private Reserva reservaCriada;

    public AcaoReservar(ReservaService reservaService, int usuarioId, String isbn) {
        this.reservaService = reservaService;
        this.usuarioId = usuarioId;
        this.isbn = isbn;
    }

    @Override
    public boolean executar() {
        reservaCriada = reservaService.reservarLivro(usuarioId, isbn);
        return reservaCriada != null;
    }

    @Override
    public boolean desfazer() {
        if (reservaCriada == null) return false;
        boolean ok = reservaService.cancelarReserva(reservaCriada.getId());
        if (ok) System.out.println("  Reserva #" + reservaCriada.getId() + " desfeita.");
        return ok;
    }

    @Override
    public String descricao() {
        return "Reservar livro ISBN: " + isbn + " para usuario #" + usuarioId;
    }
}
