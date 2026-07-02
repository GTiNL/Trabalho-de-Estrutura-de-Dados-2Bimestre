package br.edu.biblioteca.action;

import br.edu.biblioteca.model.Reserva;
import br.edu.biblioteca.model.StatusReserva;
import br.edu.biblioteca.service.ReservaService;

public class AcaoCancelarReserva implements Acao {

    private final ReservaService reservaService;
    private final int reservaId;
    private Reserva reservaCancelada;

    public AcaoCancelarReserva(ReservaService reservaService, int reservaId) {
        this.reservaService = reservaService;
        this.reservaId = reservaId;
    }

    @Override
    public boolean executar() {
        reservaCancelada = reservaService.buscarReservaPorId(reservaId);
        if (reservaCancelada == null || reservaCancelada.getStatus() != StatusReserva.ATIVA) {
            System.out.println("  [ERRO] Reserva ativa nao encontrada com ID: " + reservaId);
            return false;
        }
        return reservaService.cancelarReserva(reservaId);
    }

    @Override
    public boolean desfazer() {
        if (reservaCancelada == null) return false;
        reservaService.reativarReserva(reservaCancelada);
        System.out.println("  Cancelamento desfeito: reserva #" + reservaId + " reativada.");
        return true;
    }

    @Override
    public String descricao() {
        return "Cancelar reserva #" + reservaId;
    }
}
