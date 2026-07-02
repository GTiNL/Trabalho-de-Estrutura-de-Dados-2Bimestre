package br.edu.biblioteca.structures;

// Pilha generica (Stack) usando nos encadeados - usada para Undo/Redo
public class MinhaPilha<T> {

    // No interno da pilha
    private class No {
        T dado;
        No proximo;

        No(T dado, No proximo) {
            this.dado = dado;
            this.proximo = proximo;
        }
    }

    private No topo;
    private int size;

    public MinhaPilha() {
        this.topo = null;
        this.size = 0;
    }

    // Empilha um elemento no topo
    public void push(T elemento) {
        topo = new No(elemento, topo);
        size++;
    }

    // Desempilha e retorna o elemento do topo
    public T pop() {
        if (isEmpty()) {
            throw new RuntimeException("Pilha vazia!");
        }
        T dado = topo.dado;
        topo = topo.proximo;
        size--;
        return dado;
    }

    // Retorna o topo sem remover
    public T peek() {
        if (isEmpty()) {
            throw new RuntimeException("Pilha vazia!");
        }
        return topo.dado;
    }

    // Verifica se esta vazia
    public boolean isEmpty() {
        return size == 0;
    }

    // Retorna o tamanho
    public int tamanho() {
        return size;
    }

    // Limpa a pilha
    public void limpar() {
        topo = null;
        size = 0;
    }

    public String toString() {
        String resultado = "Pilha (topo -> base): [";
        No atual = topo;
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
