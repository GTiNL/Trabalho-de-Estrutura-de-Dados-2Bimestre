package br.edu.biblioteca.structures;

// Vetor dinamico generico (funciona como um ArrayList feito na mao)
public class Vetor<T> {

    private Object[] dados;
    private int size;

    public Vetor() {
        this.dados = new Object[10];
        this.size = 0;
    }

    // Adiciona um elemento no final
    public void add(T elemento) {
        if (size == dados.length) {
            crescer();
        }
        dados[size] = elemento;
        size++;
    }

    // Retorna o elemento na posicao informada
    @SuppressWarnings("unchecked")
    public T get(int indice) {
        if (indice < 0 || indice >= size) {
            throw new IndexOutOfBoundsException("Indice fora do intervalo: " + indice);
        }
        return (T) dados[indice];
    }

    // Substitui o elemento na posicao informada
    public void set(int indice, T elemento) {
        if (indice < 0 || indice >= size) {
            throw new IndexOutOfBoundsException("Indice fora do intervalo: " + indice);
        }
        dados[indice] = elemento;
    }

    // Remove e retorna o elemento na posicao informada
    @SuppressWarnings("unchecked")
    public T remove(int indice) {
        if (indice < 0 || indice >= size) {
            throw new IndexOutOfBoundsException("Indice fora do intervalo: " + indice);
        }
        T removido = (T) dados[indice];

        // Desloca os elementos para a esquerda
        for (int i = indice; i < size - 1; i++) {
            dados[i] = dados[i + 1];
        }
        dados[size - 1] = null;
        size--;
        return removido;
    }

    // Retorna o indice do elemento, ou -1 se nao encontrado
    public int indexOf(T elemento) {
        for (int i = 0; i < size; i++) {
            if (dados[i] != null && dados[i].equals(elemento)) {
                return i;
            }
        }
        return -1;
    }

    // Verifica se o vetor contem o elemento
    public boolean contains(T elemento) {
        return indexOf(elemento) >= 0;
    }

    // Retorna o tamanho atual
    public int tamanho() {
        return size;
    }

    // Verifica se esta vazio
    public boolean isEmpty() {
        return size == 0;
    }

    // Limpa o vetor
    public void limpar() {
        for (int i = 0; i < size; i++) {
            dados[i] = null;
        }
        size = 0;
    }

    // Dobra o tamanho do array interno quando fica cheio
    private void crescer() {
        Object[] novoDados = new Object[dados.length * 2];
        for (int i = 0; i < size; i++) {
            novoDados[i] = dados[i];
        }
        dados = novoDados;
    }

    public String toString() {
        String resultado = "[";
        for (int i = 0; i < size; i++) {
            if (i > 0) resultado += ", ";
            resultado += dados[i];
        }
        resultado += "]";
        return resultado;
    }
}
