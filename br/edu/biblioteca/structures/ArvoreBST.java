package br.edu.biblioteca.structures;

// Arvore Binaria de Busca - usada como indice para busca por ISBN ou titulo
public class ArvoreBST<K extends Comparable<K>, V> {

    // No interno da arvore.
    // Cada no e como uma "caixinha" que guarda um par (chave, valor) e
    // aponta para no maximo dois outros nos: o filho esquerdo e o direito.
    private class No {
        K chave;
        V valor;
        No esquerda;
        No direita;

        No(K chave, V valor) {
            this.chave = chave;
            this.valor = valor;
            this.esquerda = null;
            this.direita = null;
        }
    }

    private No raiz;
    private int size;


    public ArvoreBST() {
        this.raiz = null;
        this.size = 0;
    }

    // Insere ou atualiza um par chave-valor
    public void put(K chave, V valor) {
        raiz = inserir(raiz, chave, valor);
    }

    /*
     * Insercao recursiva.
     * A cada chamada, comparamos a chave que queremos inserir com a chave
     * do no atual:
     *  - se for menor, "descemos" para a subarvore esquerda
     *  - se for maior, "descemos" para a subarvore direita
     *  - se for igual, ja existe: apenas atualizamos o valor
     *
     * Quando chegamos em um no == null, significa que encontramos o lugar
     * correto (uma "folha vazia") para pendurar o novo no.
     *
     * Repare no padrao "no.esquerda = inserir(no.esquerda, ...)":
     * isso reconecta a subarvore modificada de volta ao pai, garantindo
     * que a arvore inteira fique corretamente ligada apos a insercao.
     */

    private No inserir(No no, K chave, V valor) {
        if (no == null) {
            size++;
            return new No(chave, valor);
        }

        int comparacao = chave.compareTo(no.chave);
        if (comparacao < 0) {
            no.esquerda = inserir(no.esquerda, chave, valor);
        } else if (comparacao > 0) {
            no.direita = inserir(no.direita, chave, valor);
        } else {
            no.valor = valor; // atualiza se ja existe
        }
        return no;
    }

    // Busca o valor pela chave, retorna null se nao achar
    public V get(K chave) {
        No resultado = buscar(raiz, chave);
        if (resultado != null) {
            return resultado.valor;
        }
        return null;
    }

    private No buscar(No no, K chave) {
        if (no == null) {
            return null;
        }

        int comparacao = chave.compareTo(no.chave);
        if (comparacao < 0) {
            return buscar(no.esquerda, chave);
        } else if (comparacao > 0) {
            return buscar(no.direita, chave);
        } else {
            return no;
        }
    }

    // Verifica se a chave existe
    public boolean containsKey(K chave) {
        return get(chave) != null;
    }

    // Remove e retorna o valor da chave
    public V remove(K chave) {
        V valor = get(chave);
        if (valor != null) {
            raiz = remover(raiz, chave);
            size--;
        }
        return valor;
    }


    /*
     * Remocao recursiva — a operacao mais delicada da BST.
     * Primeiro "descemos" ate encontrar o no com a chave desejada, igual
     * na busca. Depois de encontra-lo, existem 3 casos possiveis:
     *
     * Caso 1 - no sem filho esquerdo:
     *   simplesmente "promovemos" o filho direito para o lugar do no
     *   removido (se tambem nao houver filho direito, ele vira null,
     *   o que representa corretamente um no sem filhos sendo removido).
     *
     * Caso 2 - no sem filho direito:
     *   simetrico ao caso 1: promovemos o filho esquerdo.
     *
     * Caso 3 - no com dois filhos:
     *   nao da pra simplesmente apagar, pois isso quebraria as duas
     *   subarvores. A solucao classica: buscamos o "sucessor" — o menor
     *   valor da subarvore direita (que e o proximo valor em ordem
     *   crescente depois do no removido). Copiamos a chave/valor do
     *   sucessor para o no atual, e entao removemos o sucessor de dentro
     *   da subarvore direita (ele com certeza cai no caso 1, pois o
     *   menor elemento de uma subarvore nunca tem filho esquerdo).
     */
    private No remover(No no, K chave) {
        if (no == null) {
            return null;
        }

        int comparacao = chave.compareTo(no.chave);
        if (comparacao < 0) {
            no.esquerda = remover(no.esquerda, chave);
        } else if (comparacao > 0) {
            no.direita = remover(no.direita, chave);
        } else {
            // Encontrou o no para remover

            // Caso 1: sem filho esquerdo
            if (no.esquerda == null) {
                return no.direita;
            }
            // Caso 2: sem filho direito
            if (no.direita == null) {
                return no.esquerda;
            }
            // Caso 3: dois filhos - pega o menor da subarvore direita
            No sucessor = encontrarMinimo(no.direita);
            no.chave = sucessor.chave;
            no.valor = sucessor.valor;
            no.direita = remover(no.direita, sucessor.chave);
        }
        return no;
    }

    // Encontra o no com menor chave na subarvore
    private No encontrarMinimo(No no) {
        while (no.esquerda != null) {
            no = no.esquerda;
        }
        return no;
    }

    // Percorre em ordem (esquerda -> raiz -> direita) e retorna valores ordenados
    public Vetor<V> inOrder() {
        Vetor<V> resultado = new Vetor<>();
        percorrerEmOrdem(raiz, resultado);
        return resultado;
    }

    private void percorrerEmOrdem(No no, Vetor<V> resultado) {
        if (no == null) {
            return;
        }
        percorrerEmOrdem(no.esquerda, resultado);
        resultado.add(no.valor);
        percorrerEmOrdem(no.direita, resultado);
    }

    // Retorna todas as chaves em ordem
    public Vetor<K> chaves() {
        Vetor<K> resultado = new Vetor<>();
        percorrerChaves(raiz, resultado);
        return resultado;
    }

    private void percorrerChaves(No no, Vetor<K> resultado) {
        if (no == null) {
            return;
        }
        percorrerChaves(no.esquerda, resultado);
        resultado.add(no.chave);
        percorrerChaves(no.direita, resultado);
    }

    // Retorna o tamanho
    public int tamanho() {
        return size;
    }

    // Verifica se esta vazia
    public boolean isEmpty() {
        return size == 0;
    }

    public String toString() {
        Vetor<V> valores = inOrder();
        return "ArvoreBST " + valores.toString();
    }
}
