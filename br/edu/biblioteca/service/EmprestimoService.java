package br.edu.biblioteca.service;

import br.edu.biblioteca.model.*;
import br.edu.biblioteca.structures.Vetor;

import java.time.LocalDate;

// Servico de emprestimos, devolucoes, renovacoes e multas
public class EmprestimoService {

    private Vetor<Emprestimo> emprestimos;
    private Vetor<Multa> multas;
    private Vetor<Notificacao> notificacoes;
    private CatalogoService catalogoService;
    private UsuarioService usuarioService;
    private int proximoEmprestimoId;
    private int proximoMultaId;
    private int proximoNotificacaoId;

    // Configuracoes de prazo por tipo de usuario
    private static final int DIAS_ALUNO = 14;
    private static final int DIAS_PROFESSOR = 30;
    private static final int DIAS_BIBLIOTECARIO = 21;
    private static final double MULTA_POR_DIA = 1.50;

    public EmprestimoService(CatalogoService catalogoService, UsuarioService usuarioService) {
        this.emprestimos = new Vetor<>();
        this.multas = new Vetor<>();
        this.notificacoes = new Vetor<>();
        this.catalogoService = catalogoService;
        this.usuarioService = usuarioService;
        this.proximoEmprestimoId = 1;
        this.proximoMultaId = 1;
        this.proximoNotificacaoId = 1;
    }

    // Realiza um emprestimo
    public Emprestimo emprestarExemplar(int usuarioId, int exemplarId) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        if (usuario == null) {
            System.out.println("  [ERRO] Usuario nao encontrado.");
            return null;
        }
        if (usuario.isBloqueado()) {
            System.out.println("  [ERRO] Usuario bloqueado.");
            return null;
        }

        Exemplar exemplar = catalogoService.buscarExemplarPorId(exemplarId);
        if (exemplar == null) {
            System.out.println("  [ERRO] Exemplar nao encontrado.");
            return null;
        }
        if (exemplar.getStatus() != StatusExemplar.DISPONIVEL) {
            System.out.println("  [ERRO] Exemplar nao esta disponivel. Status: " + exemplar.getStatus());
            return null;
        }

        // Verifica multas pendentes
        if (temMultaPendente(usuarioId)) {
            System.out.println("  [ERRO] Usuario possui multa(s) pendente(s).");
            return null;
        }

        LocalDate hoje = LocalDate.now();
        int dias = calcularDiasEmprestimo(usuario.getTipo());
        LocalDate dataPrevista = hoje.plusDays(dias);

        Emprestimo emprestimo = new Emprestimo(proximoEmprestimoId, usuarioId, exemplarId, hoje, dataPrevista);
        proximoEmprestimoId++;
        emprestimos.add(emprestimo);

        exemplar.setStatus(StatusExemplar.EMPRESTADO);

        criarNotificacao(usuarioId, "Emprestimo realizado. Exemplar #" + exemplarId
                + ". Devolver ate " + dataPrevista);

        return emprestimo;
    }

    // Realiza a devolucao de um exemplar
    public Emprestimo devolverExemplar(int exemplarId) {
        Emprestimo emprestimo = buscarEmprestimoAtivoPorExemplar(exemplarId);
        if (emprestimo == null) {
            System.out.println("  [ERRO] Nao ha emprestimo ativo para este exemplar.");
            return null;
        }

        LocalDate hoje = LocalDate.now();
        emprestimo.setDataDevolucao(hoje);
        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);

        Exemplar exemplar = catalogoService.buscarExemplarPorId(exemplarId);
        if (exemplar != null) {
            exemplar.setStatus(StatusExemplar.DISPONIVEL);
        }

        // Verifica atraso e gera multa se necessario
        Multa multa = calcularMulta(emprestimo);
        if (multa != null) {
            criarNotificacao(emprestimo.getUsuarioId(),
                    "Devolucao com atraso de " + multa.getDiasAtraso()
                            + " dia(s). Multa de R$" + multa.getValor());
        } else {
            criarNotificacao(emprestimo.getUsuarioId(),
                    "Devolucao realizada com sucesso. Exemplar #" + exemplarId);
        }

        return emprestimo;
    }

    // Renova um emprestimo (estende a data prevista)
    public boolean renovar(int emprestimoId) {
        Emprestimo emprestimo = buscarEmprestimoPorId(emprestimoId);
        if (emprestimo == null) {
            System.out.println("  [ERRO] Emprestimo nao encontrado.");
            return false;
        }
        if (emprestimo.getStatus() != StatusEmprestimo.ATIVO
                && emprestimo.getStatus() != StatusEmprestimo.RENOVADO) {
            System.out.println("  [ERRO] Emprestimo nao esta ativo.");
            return false;
        }

        // Verifica se ja esta atrasado
        if (LocalDate.now().isAfter(emprestimo.getDataPrevista())) {
            System.out.println("  [ERRO] Emprestimo ja esta atrasado. Devolva primeiro.");
            return false;
        }

        Usuario usuario = usuarioService.buscarPorId(emprestimo.getUsuarioId());
        int dias = calcularDiasEmprestimo(usuario.getTipo());
        emprestimo.setDataPrevista(emprestimo.getDataPrevista().plusDays(dias));
        emprestimo.setStatus(StatusEmprestimo.RENOVADO);

        criarNotificacao(emprestimo.getUsuarioId(),
                "Emprestimo #" + emprestimoId + " renovado. Nova data: " + emprestimo.getDataPrevista());

        return true;
    }

    // Calcula multa por atraso
    public Multa calcularMulta(Emprestimo emprestimo) {
        LocalDate dataDevolucao = emprestimo.getDataDevolucao();
        if (dataDevolucao == null) {
            dataDevolucao = LocalDate.now();
        }

        // Calcula diferenca de dias usando toEpochDay (simples)
        long diasAtraso = dataDevolucao.toEpochDay() - emprestimo.getDataPrevista().toEpochDay();

        if (diasAtraso <= 0) {
            return null; // sem atraso
        }

        double valor = diasAtraso * MULTA_POR_DIA;

        Multa multa = new Multa(proximoMultaId, emprestimo.getId(), valor, (int) diasAtraso);
        proximoMultaId++;
        multas.add(multa);
        return multa;
    }

    // Quita uma multa pelo ID
    public boolean quitarMulta(int multaId) {
        for (int i = 0; i < multas.tamanho(); i++) {
            if (multas.get(i).getId() == multaId) {
                multas.get(i).setQuitada(true);
                return true;
            }
        }
        return false;
    }

    // ========== CONSULTAS ==========

    public Emprestimo buscarEmprestimoPorId(int id) {
        for (int i = 0; i < emprestimos.tamanho(); i++) {
            if (emprestimos.get(i).getId() == id) {
                return emprestimos.get(i);
            }
        }
        return null;
    }

    public Emprestimo buscarEmprestimoAtivoPorExemplar(int exemplarId) {
        for (int i = 0; i < emprestimos.tamanho(); i++) {
            Emprestimo e = emprestimos.get(i);
            if (e.getExemplarId() == exemplarId
                    && (e.getStatus() == StatusEmprestimo.ATIVO || e.getStatus() == StatusEmprestimo.RENOVADO)) {
                return e;
            }
        }
        return null;
    }

    public Vetor<Emprestimo> listarEmprestimos() {
        return emprestimos;
    }

    public Vetor<Emprestimo> listarEmprestimosAtivos() {
        Vetor<Emprestimo> ativos = new Vetor<>();
        for (int i = 0; i < emprestimos.tamanho(); i++) {
            Emprestimo e = emprestimos.get(i);
            if (e.getStatus() == StatusEmprestimo.ATIVO || e.getStatus() == StatusEmprestimo.RENOVADO) {
                ativos.add(e);
            }
        }
        return ativos;
    }

    public Vetor<Emprestimo> listarEmprestimosEmAtraso() {
        Vetor<Emprestimo> atrasados = new Vetor<>();
        LocalDate hoje = LocalDate.now();
        for (int i = 0; i < emprestimos.tamanho(); i++) {
            Emprestimo e = emprestimos.get(i);
            if ((e.getStatus() == StatusEmprestimo.ATIVO || e.getStatus() == StatusEmprestimo.RENOVADO)
                    && hoje.isAfter(e.getDataPrevista())) {
                atrasados.add(e);
            }
        }
        return atrasados;
    }

    public Vetor<Emprestimo> listarEmprestimosPorUsuario(int usuarioId) {
        Vetor<Emprestimo> resultado = new Vetor<>();
        for (int i = 0; i < emprestimos.tamanho(); i++) {
            if (emprestimos.get(i).getUsuarioId() == usuarioId) {
                resultado.add(emprestimos.get(i));
            }
        }
        return resultado;
    }

    public Vetor<Multa> listarMultas() {
        return multas;
    }

    public Vetor<Multa> listarMultasPendentes() {
        Vetor<Multa> pendentes = new Vetor<>();
        for (int i = 0; i < multas.tamanho(); i++) {
            if (!multas.get(i).isQuitada()) {
                pendentes.add(multas.get(i));
            }
        }
        return pendentes;
    }

    public boolean temMultaPendente(int usuarioId) {
        for (int i = 0; i < multas.tamanho(); i++) {
            Multa m = multas.get(i);
            if (!m.isQuitada()) {
                Emprestimo e = buscarEmprestimoPorId(m.getEmprestimoId());
                if (e != null && e.getUsuarioId() == usuarioId) {
                    return true;
                }
            }
        }
        return false;
    }

    public Vetor<Notificacao> listarNotificacoes() {
        return notificacoes;
    }

    public Vetor<Notificacao> listarNotificacoesPorUsuario(int usuarioId) {
        Vetor<Notificacao> resultado = new Vetor<>();
        for (int i = 0; i < notificacoes.tamanho(); i++) {
            if (notificacoes.get(i).getUsuarioId() == usuarioId) {
                resultado.add(notificacoes.get(i));
            }
        }
        return resultado;
    }

    // ========== AUXILIARES ==========

    private int calcularDiasEmprestimo(TipoUsuario tipo) {
        if (tipo == TipoUsuario.PROFESSOR) {
            return DIAS_PROFESSOR;
        } else if (tipo == TipoUsuario.BIBLIOTECARIO) {
            return DIAS_BIBLIOTECARIO;
        } else {
            return DIAS_ALUNO;
        }
    }

    private void criarNotificacao(int usuarioId, String mensagem) {
        Notificacao n = new Notificacao(proximoNotificacaoId, usuarioId, mensagem, LocalDate.now());
        proximoNotificacaoId++;
        notificacoes.add(n);
    }

    // Carrega emprestimo persistido diretamente, sem gerar novo ID
    public void carregarEmprestimo(Emprestimo e) {
        emprestimos.add(e);
        if (e.getId() >= proximoEmprestimoId) proximoEmprestimoId = e.getId() + 1;
    }

    // Carrega multa persistida diretamente, sem gerar novo ID
    public void carregarMulta(Multa m) {
        multas.add(m);
        if (m.getId() >= proximoMultaId) proximoMultaId = m.getId() + 1;
    }

    public Vetor<Emprestimo> getEmprestimos() { return emprestimos; }
    public Vetor<Multa> getMultas() { return multas; }

    public int totalEmprestimos() {
        return emprestimos.tamanho();
    }
}
