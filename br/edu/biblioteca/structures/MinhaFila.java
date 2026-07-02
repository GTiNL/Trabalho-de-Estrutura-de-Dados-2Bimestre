package br.edu.biblioteca.structures;

// Fila generica (Queue) usando nos encadeados - usada para reservas
public class MinhaFila<T> {

    // No interno da fila
    private class No {
        T dado;
        No proximo;

        No(T dado) {
            this.dado = dado;
            this.proximo = null;
        }
    }

    private No frente;
    private No tras;
    private int size;

    public MinhaFila() {
        this.frente = null;
        this.tras = null;
        this.size = 0;
    }

    // Insere no final da fila
    public void enqueue(T elemento) {
        No novoNo = new No(elemento);
        if (isEmpty()) {
            frente = novoNo;
        } else {
            tras.proximo = novoNo;
        }
        tras = novoNo;
        size++;
    }

    // Remove e retorna o elemento da frente
    public T dequeue() {
        if (isEmpty()) {
            throw new RuntimeException("Fila vazia!");
        }
        T dado = frente.dado;
        frente = frente.proximo;
        if (frente == null) {
            tras = null;
        }
        size--;
        return dado;
    }

    // Retorna o elemento da frente sem remover
    public T peek() {
        if (isEmpty()) {
            throw new RuntimeException("Fila vazia!");
        }
        return frente.dado;
    }

    // Verifica se esta vazia
    public boolean isEmpty() {
        return size == 0;
    }

    // Retorna o tamanho
    public int tamanho() {
        return size;
    }

    // Limpa a fila
    public void limpar() {
        frente = null;
        tras = null;
        size = 0;
    }

    public String toString() {
        String resultado = "Fila (frente -> tras): [";
        No atual = frente;
        boolean primeiro = true;
        while (atual != null) {
            if (!primeiro) resultado += ", ";
            resultado += atual.dado;
            atual = atual.proximo;
            primeiro = false;
        }
        resultado += "]";
        return resultado;
    }
}
