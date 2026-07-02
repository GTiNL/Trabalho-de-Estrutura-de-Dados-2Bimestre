package br.edu.biblioteca.structures;

// Matriz bidimensional de inteiros para estatisticas (ex: meses x categorias)
public class MatrizInt {

    private int[][] dados;
    private int linhas;
    private int colunas;

    public MatrizInt(int linhas, int colunas) {
        this.linhas = linhas;
        this.colunas = colunas;
        this.dados = new int[linhas][colunas];
    }

    // Retorna o valor na posicao
    public int get(int linha, int coluna) {
        return dados[linha][coluna];
    }

    // Define o valor na posicao
    public void set(int linha, int coluna, int valor) {
        dados[linha][coluna] = valor;
    }

    // Incrementa em 1
    public void incrementar(int linha, int coluna) {
        dados[linha][coluna]++;
    }

    // Soma todos os valores de uma linha
    public int somaLinha(int linha) {
        int soma = 0;
        for (int j = 0; j < colunas; j++) {
            soma += dados[linha][j];
        }
        return soma;
    }

    // Soma todos os valores de uma coluna
    public int somaColuna(int coluna) {
        int soma = 0;
        for (int i = 0; i < linhas; i++) {
            soma += dados[i][coluna];
        }
        return soma;
    }

    // Soma total de todos os valores
    public int somaTotal() {
        int soma = 0;
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                soma += dados[i][j];
            }
        }
        return soma;
    }

    // Zera a matriz
    public void limpar() {
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                dados[i][j] = 0;
            }
        }
    }

    public int getLinhas() { return linhas; }
    public int getColunas() { return colunas; }

    public String toString() {
        String resultado = "";
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                resultado += dados[i][j] + "\t";
            }
            resultado += "\n";
        }
        return resultado;
    }
}
