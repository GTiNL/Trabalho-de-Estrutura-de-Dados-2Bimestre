package br.edu.biblioteca.service;

import br.edu.biblioteca.model.*;
import br.edu.biblioteca.structures.MatrizInt;
import br.edu.biblioteca.structures.Vetor;

import java.time.LocalDate;

// Servico de relatorios e estatisticas (usa MatrizInt)
public class RelatorioService {

    private CatalogoService catalogoService;
    private UsuarioService usuarioService;
    private EmprestimoService emprestimoService;

    public RelatorioService(CatalogoService catalogoService,
                            UsuarioService usuarioService,
                            EmprestimoService emprestimoService) {
        this.catalogoService = catalogoService;
        this.usuarioService = usuarioService;
        this.emprestimoService = emprestimoService;
    }

    // Mostra os livros mais emprestados (usa Selection Sort decrescente)
    public void topMaisEmprestados(int top) {
        Vetor<Emprestimo> emprestimos = emprestimoService.listarEmprestimos();
        Vetor<Livro> livros = catalogoService.listarLivros();

        if (livros.isEmpty() || emprestimos.isEmpty()) {
            System.out.println("  Nenhum dado disponivel.");
            return;
        }

        // Conta emprestimos por ISBN
        Vetor<String> isbns = new Vetor<>();
        Vetor<Integer> contagens = new Vetor<>();

        for (int i = 0; i < emprestimos.tamanho(); i++) {
            Exemplar ex = catalogoService.buscarExemplarPorId(emprestimos.get(i).getExemplarId());
            if (ex == null) continue;
            String isbn = ex.getIsbnLivro();

            // Procura se ja esta na lista
            int indice = -1;
            for (int j = 0; j < isbns.tamanho(); j++) {
                if (isbns.get(j).equals(isbn)) {
                    indice = j;
                    break;
                }
            }

            if (indice >= 0) {
                contagens.set(indice, contagens.get(indice) + 1);
            } else {
                isbns.add(isbn);
                contagens.add(1);
            }
        }

        // Selection Sort decrescente
        for (int i = 0; i < contagens.tamanho() - 1; i++) {
            int maxIdx = i;
            for (int j = i + 1; j < contagens.tamanho(); j++) {
                if (contagens.get(j) > contagens.get(maxIdx)) {
                    maxIdx = j;
                }
            }
            if (maxIdx != i) {
                int tempC = contagens.get(i);
                contagens.set(i, contagens.get(maxIdx));
                contagens.set(maxIdx, tempC);

                String tempI = isbns.get(i);
                isbns.set(i, isbns.get(maxIdx));
                isbns.set(maxIdx, tempI);
            }
        }

        // Exibe
        int limite = top;
        if (limite > isbns.tamanho()) {
            limite = isbns.tamanho();
        }

        System.out.println("\n  === TOP " + limite + " LIVROS MAIS EMPRESTADOS ===");
        System.out.println("  Pos.  Titulo                                    Emprestimos");
        System.out.println("  ------------------------------------------------------------");
        for (int i = 0; i < limite; i++) {
            Livro livro = catalogoService.buscarPorIsbn(isbns.get(i));
            String titulo = "Desconhecido";
            if (livro != null) {
                titulo = livro.getTitulo();
            }
            System.out.println("  " + (i + 1) + ".    " + titulo + "  -  " + contagens.get(i) + " emprestimo(s)");
        }
    }

    // Lista emprestimos em atraso
    public void emAtraso() {
        Vetor<Emprestimo> atrasados = emprestimoService.listarEmprestimosEmAtraso();

        System.out.println("\n  === EMPRESTIMOS EM ATRASO ===");
        if (atrasados.isEmpty()) {
            System.out.println("  Nenhum emprestimo em atraso.");
            return;
        }

        System.out.println("  Total em atraso: " + atrasados.tamanho());
        System.out.println("  ------------------------------------------------------------");
        for (int i = 0; i < atrasados.tamanho(); i++) {
            Emprestimo e = atrasados.get(i);
            Usuario u = usuarioService.buscarPorId(e.getUsuarioId());

            // Calcula dias de atraso com toEpochDay
            long diasAtraso = LocalDate.now().toEpochDay() - e.getDataPrevista().toEpochDay();

            String nomeUsuario = "ID " + e.getUsuarioId();
            if (u != null) {
                nomeUsuario = u.getNome();
            }

            System.out.println("  Emprestimo #" + e.getId()
                    + " | Usuario: " + nomeUsuario
                    + " | Exemplar #" + e.getExemplarId()
                    + " | Previsto: " + e.getDataPrevista()
                    + " | Atraso: " + diasAtraso + " dia(s)");
        }
    }

    // Usuarios com mais atrasos (usa Insertion Sort decrescente)
    public void usuariosComMaisAtrasos(int top) {
        Vetor<Emprestimo> emprestimos = emprestimoService.listarEmprestimos();

        // Conta atrasos por usuario
        Vetor<Integer> userIds = new Vetor<>();
        Vetor<Integer> contagens = new Vetor<>();

        for (int i = 0; i < emprestimos.tamanho(); i++) {
            Emprestimo e = emprestimos.get(i);
            boolean atrasado = false;

            if (e.getStatus() == StatusEmprestimo.DEVOLVIDO && e.getDataDevolucao() != null) {
                if (e.getDataDevolucao().isAfter(e.getDataPrevista())) {
                    atrasado = true;
                }
            } else if ((e.getStatus() == StatusEmprestimo.ATIVO || e.getStatus() == StatusEmprestimo.RENOVADO)
                    && LocalDate.now().isAfter(e.getDataPrevista())) {
                atrasado = true;
            }

            if (atrasado) {
                int indice = -1;
                for (int j = 0; j < userIds.tamanho(); j++) {
                    if (userIds.get(j) == e.getUsuarioId()) {
                        indice = j;
                        break;
                    }
                }
                if (indice >= 0) {
                    contagens.set(indice, contagens.get(indice) + 1);
                } else {
                    userIds.add(e.getUsuarioId());
                    contagens.add(1);
                }
            }
        }

        if (userIds.isEmpty()) {
            System.out.println("\n  === USUARIOS COM MAIS ATRASOS ===");
            System.out.println("  Nenhum usuario com atraso registrado.");
            return;
        }

        // Insertion Sort decrescente
        for (int i = 1; i < contagens.tamanho(); i++) {
            int chaveC = contagens.get(i);
            int chaveU = userIds.get(i);
            int j = i - 1;
            while (j >= 0 && contagens.get(j) < chaveC) {
                contagens.set(j + 1, contagens.get(j));
                userIds.set(j + 1, userIds.get(j));
                j--;
            }
            contagens.set(j + 1, chaveC);
            userIds.set(j + 1, chaveU);
        }

        int limite = top;
        if (limite > userIds.tamanho()) {
            limite = userIds.tamanho();
        }

        System.out.println("\n  === TOP " + limite + " USUARIOS COM MAIS ATRASOS ===");
        System.out.println("  Pos.  Nome                          Tipo        Atrasos");
        System.out.println("  ------------------------------------------------------------");
        for (int i = 0; i < limite; i++) {
            Usuario u = usuarioService.buscarPorId(userIds.get(i));
            String nome = "ID " + userIds.get(i);
            String tipo = "?";
            if (u != null) {
                nome = u.getNome();
                tipo = u.getTipo().toString();
            }
            System.out.println("  " + (i + 1) + ".    " + nome + "  -  " + tipo + "  -  " + contagens.get(i) + " atraso(s)");
        }
    }

    // Estatisticas mensais usando MatrizInt (12 meses x categorias)
    public void estatisticasMensais() {
        Vetor<Livro> livros = catalogoService.listarLivros();
        Vetor<Emprestimo> emprestimos = emprestimoService.listarEmprestimos();

        // Coleta categorias unicas
        Vetor<String> categoriasNomes = new Vetor<>();
        for (int i = 0; i < livros.tamanho(); i++) {
            Livro livro = livros.get(i);
            for (int j = 0; j < livro.getCategorias().tamanho(); j++) {
                String catNome = livro.getCategorias().get(j).getNome();
                if (!categoriasNomes.contains(catNome)) {
                    categoriasNomes.add(catNome);
                }
            }
        }

        if (categoriasNomes.isEmpty()) {
            categoriasNomes.add("Geral");
        }

        // Cria matriz 12 meses x N categorias
        MatrizInt matriz = new MatrizInt(12, categoriasNomes.tamanho());

        // Preenche a matriz
        for (int i = 0; i < emprestimos.tamanho(); i++) {
            Emprestimo e = emprestimos.get(i);
            int mes = e.getDataEmprestimo().getMonthValue() - 1; // 0 a 11

            Exemplar ex = catalogoService.buscarExemplarPorId(e.getExemplarId());
            if (ex == null) continue;
            Livro livro = catalogoService.buscarPorIsbn(ex.getIsbnLivro());
            if (livro == null) continue;

            if (livro.getCategorias().isEmpty()) {
                int colGeral = categoriasNomes.indexOf("Geral");
                if (colGeral >= 0) {
                    matriz.incrementar(mes, colGeral);
                }
            } else {
                for (int j = 0; j < livro.getCategorias().tamanho(); j++) {
                    String catNome = livro.getCategorias().get(j).getNome();
                    int col = categoriasNomes.indexOf(catNome);
                    if (col >= 0) {
                        matriz.incrementar(mes, col);
                    }
                }
            }
        }

        // Nomes dos meses
        String[] meses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
                          "Jul", "Ago", "Set", "Out", "Nov", "Dez"};

        System.out.println("\n  === ESTATISTICAS MENSAIS DE EMPRESTIMOS ===");

        // Cabecalho
        String cabecalho = "  Mes   ";
        for (int j = 0; j < categoriasNomes.tamanho(); j++) {
            cabecalho += categoriasNomes.get(j) + "\t";
        }
        cabecalho += "TOTAL";
        System.out.println(cabecalho);
        System.out.println("  ------------------------------------------------------------");

        // Dados por mes
        for (int i = 0; i < 12; i++) {
            String linha = "  " + meses[i] + "\t";
            for (int j = 0; j < categoriasNomes.tamanho(); j++) {
                linha += matriz.get(i, j) + "\t";
            }
            linha += matriz.somaLinha(i);
            System.out.println(linha);
        }

        // Totais
        System.out.println("  ------------------------------------------------------------");
        String totais = "  TOTAL\t";
        for (int j = 0; j < categoriasNomes.tamanho(); j++) {
            totais += matriz.somaColuna(j) + "\t";
        }
        totais += matriz.somaTotal();
        System.out.println(totais);
    }
}
