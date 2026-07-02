package br.edu.biblioteca.repository;

import br.edu.biblioteca.structures.Vetor;

import java.io.*;

public class FileStorage {

    public static final String PASTA_DADOS;

    static {
        PASTA_DADOS = System.getProperty("user.dir") + File.separator + "dados" + File.separator;
    }

    public static void escrever(String arquivo, Vetor<String> linhas) {
        File f = new File(PASTA_DADOS + arquivo);
        try {
            File dir = f.getParentFile();
            if (dir != null && !dir.exists()) dir.mkdirs();

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
                for (int i = 0; i < linhas.tamanho(); i++) {
                    bw.write(linhas.get(i));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("  [FileStorage] Erro ao escrever em " + f.getAbsolutePath() + ": " + e.getMessage());
        }
    }

    public static Vetor<String> ler(String arquivo) {
        Vetor<String> linhas = new Vetor<>();
        File f = new File(PASTA_DADOS + arquivo);
        if (!f.exists()) return linhas;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (!linha.trim().isEmpty()) linhas.add(linha.trim());
            }
        } catch (IOException e) {
            System.out.println("  [FileStorage] Erro ao ler " + f.getAbsolutePath() + ": " + e.getMessage());
        }
        return linhas;
    }
}
