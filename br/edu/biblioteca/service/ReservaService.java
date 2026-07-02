package br.edu.biblioteca.service;

import br.edu.biblioteca.model.*;
import br.edu.biblioteca.structures.ArvoreBST;
import br.edu.biblioteca.structures.MinhaFila;
import br.edu.biblioteca.structures.Vetor;

import java.time.LocalDate;

// Servico de reservas de livros (usa MinhaFila para fila de espera)
public class ReservaService {

    private Vetor<Reserva> reservas;
    private ArvoreBST<String, MinhaFila<Reserva>> filasDeReserva; // ISBN -> Fila
    private CatalogoService catalogoService;
    private UsuarioService usuarioService;
    private int proximoId;

    public ReservaService(CatalogoService catalogoService, UsuarioService usuarioService) {
        this.reservas = new Vetor<>();
        this.filasDeReserva = new ArvoreBST<>();
        this.catalogoService = catalogoService;
        this.usuarioService = usuarioService;
        this.proximoId = 1;
    }

    // Reserva um livro para um usuario (entra na fila)
    public Reserva reservarLivro(int usuarioId, String isbn) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        if (usuario == null) {
            System.out.println("  [ERRO] Usuario nao encontrado.");
            return null;
        }

        Livro livro = catalogoService.buscarPorIsbn(isbn);
        if (livro == null) {
            System.out.println("  [ERRO] Livro nao encontrado com ISBN: " + isbn);
            return null;
        }

        // Verifica se ja tem reserva ativa para esse livro
        for (int i = 0; i < reservas.tamanho(); i++) {
            Reserva r = reservas.get(i);
            if (r.getUsuarioId() == usuarioId && r.getIsbnLivro().equals(isbn)
                    && r.getStatus() == StatusReserva.ATIVA) {
                System.out.println("  [ERRO] Usuario ja possui reserva ativa para este livro.");
                return null;
            }
        }

        Reserva reserva = new Reserva(proximoId, usuarioId, isbn, LocalDate.now());
        proximoId++;
        reservas.add(reserva);

        // Adiciona na fila de reservas do livro
        MinhaFila<Reserva> fila = filasDeReserva.get(isbn);
        if (fila == null) {
            fila = new MinhaFila<>();
            filasDeReserva.put(isbn, fila);
        }
        fila.enqueue(reserva);

        System.out.println("  Reserva #" + reserva.getId() + " criada. Posicao na fila: " + fila.tamanho());
        return reserva;
    }

    // Cancela uma reserva
    public boolean cancelarReserva(int reservaId) {
        for (int i = 0; i < reservas.tamanho(); i++) {
            Reserva r = reservas.get(i);
            if (r.getId() == reservaId && r.getStatus() == StatusReserva.ATIVA) {
                r.setStatus(StatusReserva.CANCELADA);
                System.out.println("  Reserva #" + reservaId + " cancelada.");
                return true;
            }
        }
        System.out.println("  [ERRO] Reserva ativa nao encontrada com ID: " + reservaId);
        return false;
    }

    // Atende a proxima reserva da fila para um ISBN
    public Reserva atenderProximaReserva(String isbn) {
        MinhaFila<Reserva> fila = filasDeReserva.get(isbn);
        if (fila == null || fila.isEmpty()) {
            System.out.println("  Nao ha reservas pendentes para ISBN: " + isbn);
            return null;
        }

        // Pula reservas que foram canceladas
        Reserva proxima = null;
        while (!fila.isEmpty()) {
            Reserva candidata = fila.dequeue();
            if (candidata.getStatus() == StatusReserva.ATIVA) {
                proxima = candidata;
                break;
            }
        }

        if (proxima == null) {
            System.out.println("  Nao ha reservas ativas na fila para ISBN: " + isbn);
            return null;
        }

        // Tenta achar um exemplar disponivel
        Exemplar exemplar = catalogoService.buscarExemplarDisponivel(isbn);
        if (exemplar == null) {
            // Coloca de volta na fila
            fila.enqueue(proxima);
            System.out.println("  Nenhum exemplar disponivel. Reserva permanece na fila.");
            return null;
        }

        // Atende a reserva
        exemplar.setStatus(StatusExemplar.RESERVADO);
        proxima.setStatus(StatusReserva.ATENDIDA);

        System.out.println("  Reserva #" + proxima.getId() + " atendida. Exemplar #"
                + exemplar.getId() + " reservado para usuario ID " + proxima.getUsuarioId());
        return proxima;
    }

    // Lista todas as reservas
    public Vetor<Reserva> listarReservas() {
        return reservas;
    }

    // Lista reservas ativas
    public Vetor<Reserva> listarReservasAtivas() {
        Vetor<Reserva> ativas = new Vetor<>();
        for (int i = 0; i < reservas.tamanho(); i++) {
            if (reservas.get(i).getStatus() == StatusReserva.ATIVA) {
                ativas.add(reservas.get(i));
            }
        }
        return ativas;
    }

    // Busca uma reserva pelo ID
    public Reserva buscarReservaPorId(int id) {
        for (int i = 0; i < reservas.tamanho(); i++) {
            if (reservas.get(i).getId() == id) return reservas.get(i);
        }
        return null;
    }

    // Reativa uma reserva cancelada (usado pelo undo de AcaoCancelarReserva)
    public void reativarReserva(Reserva reserva) {
        reserva.setStatus(StatusReserva.ATIVA);
        MinhaFila<Reserva> fila = filasDeReserva.get(reserva.getIsbnLivro());
        if (fila == null) {
            fila = new MinhaFila<>();
            filasDeReserva.put(reserva.getIsbnLivro(), fila);
        }
        fila.enqueue(reserva);
    }

    // Carrega uma reserva persistida sem gerar novo ID (usado pelo repositorio)
    public void carregarReserva(Reserva reserva) {
        reservas.add(reserva);
        if (reserva.getId() >= proximoId) proximoId = reserva.getId() + 1;
        if (reserva.getStatus() == StatusReserva.ATIVA) {
            MinhaFila<Reserva> fila = filasDeReserva.get(reserva.getIsbnLivro());
            if (fila == null) {
                fila = new MinhaFila<>();
                filasDeReserva.put(reserva.getIsbnLivro(), fila);
            }
            fila.enqueue(reserva);
        }
    }

    // Lista reservas de um usuario
    public Vetor<Reserva> listarReservasPorUsuario(int usuarioId) {
        Vetor<Reserva> resultado = new Vetor<>();
        for (int i = 0; i < reservas.tamanho(); i++) {
            if (reservas.get(i).getUsuarioId() == usuarioId) {
                resultado.add(reservas.get(i));
            }
        }
        return resultado;
    }
}
