package br.edu.biblioteca.repository;

import br.edu.biblioteca.model.Autor;
import br.edu.biblioteca.model.Categoria;
import br.edu.biblioteca.model.Livro;
import br.edu.biblioteca.structures.Vetor;

public class LivroRepository {

    private static final String ARQUIVO = "livros.csv";

    // Formato: isbn;titulo;editora;ano;id:nomeAutor1|id:nomeAutor2;id:nomeCat1|...;pc1|pc2
    // ISBN, titulo e editora NAO sao escapados (nao usam os separadores ; | :)
    // Nomes de autores/categorias/palavras-chave sao escapados (substituem ; por , e | por /)
    public void salvar(Vetor<Livro> livros) {
        Vetor<String> linhas = new Vetor<>();
        for (int i = 0; i < livros.tamanho(); i++) {
            linhas.add(serializar(livros.get(i)));
        }
        FileStorage.escrever(ARQUIVO, linhas);
        System.out.println("  [LivroRepository] " + livros.tamanho() + " livro(s) salvos.");
    }

    public Vetor<Livro> carregar() {
        Vetor<Livro> livros = new Vetor<>();
        Vetor<String> linhas = FileStorage.ler(ARQUIVO);
        for (int i = 0; i < linhas.tamanho(); i++) {
            Livro livro = deserializar(linhas.get(i));
            if (livro != null) livros.add(livro);
        }
        System.out.println("  [LivroRepository] " + livros.tamanho() + " livro(s) carregados.");
        return livros;
    }

    private String serializar(Livro livro) {
        StringBuilder autores = new StringBuilder();
        for (int i = 0; i < livro.getAutores().tamanho(); i++) {
            Autor a = livro.getAutores().get(i);
            if (i > 0) autores.append("|");
            // Separador id:nome — dois pontos nao aparecem em ISBNs nem conflitam
            autores.append(a.getId()).append(":").append(escapar(a.getNome()));
        }

        StringBuilder cats = new StringBuilder();
        for (int i = 0; i < livro.getCategorias().tamanho(); i++) {
            Categoria c = livro.getCategorias().get(i);
            if (i > 0) cats.append("|");
            cats.append(c.getId()).append(":").append(escapar(c.getNome()));
        }

        StringBuilder pcs = new StringBuilder();
        for (int i = 0; i < livro.getPalavrasChave().tamanho(); i++) {
            if (i > 0) pcs.append("|");
            pcs.append(escapar(livro.getPalavrasChave().get(i)));
        }

        // ISBN, titulo e editora sao gravados sem escapamento para preservar hifens e acentos
        return livro.getIsbn() + ";" + livro.getTitulo() + ";" + livro.getEditora()
                + ";" + livro.getAno() + ";" + autores + ";" + cats + ";" + pcs;
    }

    private Livro deserializar(String linha) {
        try {
            // Limite 7: isbn;titulo;editora;ano;autores;cats;pcs
            String[] p = linha.split(";", 7);
            if (p.length < 4) return null;

            Livro livro = new Livro(p[0].trim(), p[1].trim(), p[2].trim(), Integer.parseInt(p[3].trim()));

            if (p.length > 4 && !p[4].isEmpty()) {
                for (String aut : p[4].split("\\|")) {
                    // split(":", 2) garante que dois pontos no nome nao quebrem a leitura
                    String[] av = aut.split(":", 2);
                    if (av.length == 2) {
                        livro.adicionarAutor(new Autor(Integer.parseInt(av[0].trim()), av[1].trim()));
                    }
                }
            }

            if (p.length > 5 && !p[5].isEmpty()) {
                for (String cat : p[5].split("\\|")) {
                    String[] cv = cat.split(":", 2);
                    if (cv.length == 2) {
                        livro.adicionarCategoria(new Categoria(Integer.parseInt(cv[0].trim()), cv[1].trim()));
                    }
                }
            }

            if (p.length > 6 && !p[6].isEmpty()) {
                for (String pc : p[6].split("\\|")) {
                    livro.adicionarPalavraChave(pc.trim());
                }
            }

            return livro;
        } catch (Exception e) {
            System.out.println("  [LivroRepository] Linha invalida: " + linha);
            return null;
        }
    }

    // Escapa apenas os separadores de campo e lista usados no protocolo CSV interno
    private String escapar(String s) {
        return s.replace(";", ",").replace("|", "/");
    }
}
