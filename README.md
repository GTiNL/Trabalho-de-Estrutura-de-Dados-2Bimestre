# Trabalho Segundo Bimestre

Projeto para gerenciamento dinamico de uma biblioteca

##  Integrantes
- Felipe Alves da Silva
- Gabriel Tinelli Rodrigues
- Perola Catrinque dos Santos
- Rodrigo Lucas Leão Bastos

##  Como executar
1. Clone o repositório

2. Abra no IntelliJ / Eclipse / VS Code

3. Execute a classe principal

##  Tecnologias usadas
- Java

##  Estrutura do projeto
src/
  main/java/

## 📚 Sobre o Programa

Este projeto é um **Sistema de Gerenciamento de Biblioteca** desenvolvido em Java como trabalho da disciplina de **Estrutura de Dados**. O sistema roda via terminal (console) e permite gerenciar livros, exemplares físicos, usuários, empréstimos, devoluções, multas, reservas e relatórios estatísticos.

O ponto central do trabalho **não é apenas fazer o sistema funcionar**, mas **implementar manualmente** as estruturas de dados clássicas estudadas em aula — sem usar as coleções prontas do Java (`ArrayList`, `LinkedList`, `Stack`, `Queue`, `TreeMap`) — e aplicar cada uma delas em um cenário real e justificado dentro do domínio de uma biblioteca.

### 🎯 Objetivos do Trabalho

- Implementar do zero as estruturas: **Vetor Dinâmico, Fila, Pilha, Árvore Binária de Busca e Matriz**.
- Implementar do zero os algoritmos de ordenação **Bubble Sort, Selection Sort e Insertion Sort**.
- Aplicar cada estrutura em um problema onde ela realmente faz sentido (e não apenas “por obrigação”).
- Persistir os dados em disco, em arquivos `.csv`, sem usar bancos de dados ou bibliotecas externas.
- Organizar o código em camadas, separando modelo, estruturas, regras de negócio, persistência e interface.

---

## 🏗️ Arquitetura do Projeto

O projeto segue uma separação de responsabilidades em pacotes, dentro de `br.edu.biblioteca`:


**Fluxo de dependências:** `ui` → `action`/`service` → `structures`/`model` → `repository` (para persistência). As camadas superiores nunca manipulam listas ou nós diretamente: tudo passa pelas estruturas de dados customizadas, garantindo que o exercício de estrutura de dados permeie o projeto inteiro.

---

## 🧱 Estruturas de Dados Implementadas Manualmente

Todas ficam no pacote `structures` e não utilizam nenhuma coleção pronta do Java internamente (apenas `Object[]` ou nós encadeados feitos à mão).

### 1. `Vetor<T>` — Vetor Dinâmico (equivalente a um ArrayList)

- Armazenamento interno em `Object[]`, com **redimensionamento automático** (dobra de tamanho) quando o array enche, via método privado `crescer()`.
- **Operações:** `add`, `get`, `set`, `remove` (com deslocamento de elementos), `indexOf`, `contains`, `tamanho`, `isEmpty`, `limpar`.
- **Complexidade:** `add` é O(1) amortizado; `get`/`set` são O(1); `remove`/`indexOf`/`contains` são O(n).
- **Onde é usada:** é a estrutura de armazenamento padrão do sistema — guarda listas de livros, exemplares, usuários, empréstimos, multas, reservas e notificações dentro dos respectivos `Service`s. Também é o tipo de retorno dos percursos da árvore (`inOrder()`, `chaves()`).

### 2. `MinhaFila<T>` — Fila (Queue) com nós encadeados

- Implementada com uma classe interna `No` (nó) e ponteiros de `frente` e `tras`, sem usar array.
- Segue a disciplina **FIFO** (First In, First Out).
- **Operações:** `enqueue` (insere no final), `dequeue` (remove da frente), `peek`, `isEmpty`, `tamanho`, `limpar`.
- **Complexidade:** todas as operações principais são O(1), pois há referência direta à frente e ao fim da fila.
- **Onde é usada:** implementa a **fila de espera de reservas** de cada livro, dentro de `ReservaService`. Quando um usuário reserva um livro sem exemplar disponível, ele entra no final da fila correspondente àquele ISBN; quando um exemplar é devolvido, o sistema chama `atenderProximaReserva`, que faz `dequeue` até encontrar a próxima reserva ainda ativa (pulando as que foram canceladas nesse meio tempo).

### 3. `MinhaPilha<T>` — Pilha (Stack) com nós encadeados

- Implementada com uma classe interna `No` e um único ponteiro de `topo`.
- Segue a disciplina **LIFO** (Last In, First Out).
- **Operações:** `push`, `pop`, `peek`, `isEmpty`, `tamanho`, `limpar`.
- **Complexidade:** todas as operações são O(1).
- **Onde é usada:** é a base do mecanismo de **Undo/Redo** do sistema (`UndoRedoService`), que mantém **duas pilhas**: uma de ações já executadas (`pilhaUndo`) e outra de ações desfeitas (`pilhaRedo`). Ver seção dedicada abaixo.

### 4. `ArvoreBST<K extends Comparable<K>, V>` — Árvore Binária de Busca genérica

- Implementada como uma árvore de nós (`No`) com chave, valor, filho esquerdo e filho direito — funciona como um dicionário/mapa ordenado (semelhante a um `TreeMap`), mas construída do zero.
- **Operações:** `put` (insere/atualiza), `get` (busca), `containsKey`, `remove` (com tratamento dos três casos clássicos: nó folha, um filho, ou dois filhos usando o sucessor in-order), `inOrder()` (retorna todos os valores ordenados pela chave) e `chaves()`.
- **Complexidade:** O(log n) em média para inserção, busca e remoção; O(n) no pior caso (árvore degenerada, sem balanceamento).
- **Onde é usada — em dois papéis diferentes:**
  1. **Índice de busca no catálogo** (`CatalogoService`): duas árvores independentes, uma indexando livros por **ISBN** (`indicePorIsbn`) e outra por **título** (`indicePorTitulo`, em minúsculas), permitindo busca rápida sem varrer o vetor inteiro de livros.
  2. **Mapa de filas de reserva** (`ReservaService`): uma árvore `ArvoreBST<String, MinhaFila<Reserva>>` mapeia cada ISBN à sua respectiva fila de espera. Isso combina duas estruturas na mesma funcionalidade: a árvore localiza rapidamente a fila do livro certo, e a fila decide quem será atendido primeiro.

### 5. `MatrizInt` — Matriz Bidimensional de Inteiros

- Encapsula um `int[linhas][colunas]`, com operações de soma por linha, por coluna e soma total, além de `incrementar` (soma 1 numa célula) e `limpar` (zera tudo).
- **Onde é usada:** no `RelatorioService`, para gerar as **estatísticas mensais de empréstimos por categoria**. A matriz tem 12 linhas (uma por mês do ano) e N colunas (uma por categoria de livro cadastrada). A cada empréstimo, a célula `[mês, categoria]` correspondente é incrementada; ao final, `somaLinha` dá o total de empréstimos do mês, `somaColuna` o total da categoria no ano, e `somaTotal` o total geral.

---

## 🔁 Undo / Redo com o Padrão de Projeto Command

O sistema permite desfazer e refazer as últimas ações realizadas, combinando duas ideias:

1. **Padrão de projeto Command:** cada operação relevante do sistema é encapsulada em uma classe do pacote `action`, que implementa a interface:

```java
   public interface Acao {
       boolean executar();
       boolean desfazer();
       String descricao();
   }
```

Cada implementação sabe executar a operação **e também reverter seu próprio efeito**:

| Classe | Ação | Como desfaz |
   |---|---|---|
| `AcaoCadastrarLivro` | Cadastra um livro no catálogo | Remove o livro cadastrado |
| `AcaoRemoverLivro` | Remove um livro pelo ISBN | Recadastra o livro removido (guardado em memória) |
| `AcaoEmpresta` | Realiza um empréstimo | Registra a devolução do exemplar |
| `AcaoDevolver` | Registra uma devolução | Restaura o status e a data do empréstimo, e marca o exemplar como emprestado novamente |
| `AcaoReservar` | Cria uma reserva na fila | Cancela a reserva criada |
| `AcaoCancelarReserva` | Cancela uma reserva ativa | Reativa a reserva e a recoloca na fila de espera |

2. **Duas pilhas (`MinhaPilha<Acao>`) no `UndoRedoService`:**
  - Ao executar uma ação com sucesso, ela é empilhada em `pilhaUndo`, e a `pilhaRedo` é **limpa** (o histórico de "refazer" não faz mais sentido após uma nova ação).
  - `desfazer()`: retira o topo de `pilhaUndo`, chama `desfazer()` na ação, e a empilha em `pilhaRedo`.
  - `refazer()`: retira o topo de `pilhaRedo`, chama `executar()` na ação novamente, e a devolve para `pilhaUndo`.

   Esse é o motivo pelo qual uma **pilha** (e não uma fila) é a estrutura correta aqui: a última ação feita deve ser sempre a primeira a ser desfeita.

---

## 🔢 Algoritmos de Ordenação Implementados Manualmente

Nenhum uso de `Collections.sort` ou `Arrays.sort`: os três algoritmos abaixo foram escritos à mão, cada um aplicado a um relatório diferente.

| Algoritmo | Complexidade | Onde é usado | Critério |
|---|---|---|---|
| **Bubble Sort** | O(n²) | `CatalogoService.listarLivrosOrdenadosPorAno()` | Ordena os livros por ano de publicação, crescente, comparando pares adjacentes e trocando-os até estabilizar |
| **Selection Sort** | O(n²) | `RelatorioService.topMaisEmprestados()` | Seleciona repetidamente o livro com maior número de empréstimos e o coloca na posição correta, gerando o ranking decrescente |
| **Insertion Sort** | O(n²) | `RelatorioService.usuariosComMaisAtrasos()` | Insere cada usuário na posição correta de uma lista já parcialmente ordenada por quantidade de atrasos, decrescente |

Além dos algoritmos de ordenação escritos manualmente, o `CatalogoService` também oferece listagens **já ordenadas “de graça”** aproveitando a árvore de busca: `listarLivrosOrdenadosPorTitulo()` e `listarLivrosOrdenadosPorIsbn()` simplesmente fazem o percurso **in-order** da `ArvoreBST`, que retorna os elementos naturalmente ordenados pela chave.

---

## 🗂️ Modelo de Domínio (pacote `model`)

| Classe / Enum | Descrição |
|---|---|
| `Livro` | ISBN, título, editora, ano, e vetores de autores, categorias e palavras-chave |
| `Autor`, `Categoria` | Entidades simples com id e nome |
| `Exemplar` | Cópia física de um livro, com status (`DISPONIVEL`, `EMPRESTADO`, `RESERVADO`, `INATIVO`) |
| `Usuario` | Id, nome, tipo (`ALUNO`, `PROFESSOR`, `BIBLIOTECARIO`), e-mail e flag de bloqueio |
| `Emprestimo` | Vincula usuário e exemplar, com datas de empréstimo, prevista e devolução, e status (`ATIVO`, `DEVOLVIDO`, `ATRASADO`, `RENOVADO`) |
| `Multa` | Valor e dias de atraso vinculados a um empréstimo, com flag de quitação |
| `Reserva` | Vincula usuário e ISBN, com status (`ATIVA`, `CANCELADA`, `ATENDIDA`) |
| `Notificacao` | Mensagens geradas automaticamente pelo sistema (empréstimo realizado, atraso, devolução, renovação) |

Cada tipo de usuário tem um **prazo de empréstimo diferente**, definido em `EmprestimoService`:

| Tipo de usuário | Prazo de empréstimo |
|---|---|
| Aluno | 14 dias |
| Bibliotecário | 21 dias |
| Professor | 30 dias |

A multa por atraso é fixa em **R$ 1,50 por dia**, calculada automaticamente na devolução com base na diferença entre a data de devolução e a data prevista.

---

## ⚙️ Funcionalidades por Tela (Menus do Sistema)

O `MenuPrincipal` direciona para cinco módulos, além do controle de Undo/Redo:

**1. Livros e Exemplares (`TelaCatalogo`)**
- Cadastrar livro / cadastrar exemplar
- Buscar livro por ISBN ou por título (usa a `ArvoreBST`)
- Listar todos os livros
- Listar por título (percurso in-order da árvore)
- Listar por ano (Bubble Sort)
- Listar exemplares de um livro específico
- Remover livro (remove também seus exemplares)

**2. Usuários (`TelaUsuarios`)**
- Cadastrar usuário
- Buscar por ID ou por nome (busca parcial, case-insensitive)
- Listar todos
- Bloquear / desbloquear usuário (usuário bloqueado não pode realizar novos empréstimos)

**3. Empréstimos e Devoluções (`TelaEmprestimos`)**
- Realizar empréstimo (valida usuário, bloqueio, disponibilidade do exemplar e multas pendentes)
- Devolver exemplar (calcula multa automaticamente se houver atraso)
- Renovar empréstimo (só é permitido se ainda não estiver atrasado)
- Listar empréstimos ativos / em atraso / todos / por usuário
- Consultar multas
- Consultar notificações geradas pelo sistema

**4. Reservas (`TelaReservas`)**
- Reservar livro (entra na fila de espera daquele ISBN)
- Cancelar reserva
- Atender próxima reserva de um ISBN (busca o próximo da fila que ainda está ativo)
- Listar reservas ativas / todas

**5. Relatórios (`TelaRelatorios`)**
- Top livros mais emprestados (Selection Sort)
- Empréstimos em atraso (com cálculo de dias corridos de atraso)
- Usuários com mais atrasos (Insertion Sort)
- Estatísticas mensais de empréstimos por categoria (Matriz 12 meses × N categorias)

**6. Desfazer / Refazer**
- Mostra qual seria a próxima ação a desfazer e a próxima a refazer
- Permite desfazer (Undo) e refazer (Redo) em sequência

---

## 💾 Persistência de Dados

Os dados são gravados em arquivos `.csv` simples, dentro da pasta `dados/`, sem uso de bibliotecas externas — toda a leitura/escrita é feita manualmente pela classe `FileStorage`, usando `BufferedReader`/`BufferedWriter`.

| Arquivo | Formato das colunas |
|---|---|
| `livros.csv` | `isbn;titulo;editora;ano;id:autor1\|id:autor2;id:categoria1\|...;palavraChave1\|...` |
| `exemplares.csv` | `id;isbnLivro;status` |
| `usuarios.csv` | `id;nome;tipo;email;bloqueado` |
| `emprestimos.csv` | `id;usuarioId;exemplarId;dataEmprestimo;dataPrevista;dataDevolucao;status` |
| `multas.csv` | `id;emprestimoId;valor;diasAtraso;quitada` |
| `reservas.csv` | `id;usuarioId;isbnLivro;dataReserva;status` |

Alguns pontos de atenção implementados:
- Nos campos de lista (autores, categorias, palavras-chave), o caractere `;` é substituído por `,` e o `|` por `/` antes de salvar, para não conflitar com os separadores do formato.
- Cada `Repository` tem um método `carregar()` para leitura na inicialização e um método `salvar()` para escrita.
- Cada `Service` possui um método `carregarX(...)` específico (ex: `carregarEmprestimo`, `carregarUsuario`) que insere o objeto já persistido **sem gerar um novo ID**, ajustando o contador interno (`proximoId`) para continuar a numeração corretamente na sessão seguinte.

**Fluxo de carregamento/gravação (`MenuPrincipal`):**
1. Ao iniciar, `carregarDados()` lê todos os `.csv` e povoa os serviços.
2. Se nenhum livro for encontrado (primeira execução), o sistema carrega automaticamente **5 livros, 8 exemplares e 4 usuários de exemplo** para facilitar os testes.
3. Um **shutdown hook** (`Runtime.getRuntime().addShutdownHook`) garante que os dados sejam salvos mesmo se o programa for encerrado de forma abrupta (ex: `Ctrl+C`).
4. Ao escolher a opção "Sair" no menu, os dados também são salvos explicitamente antes de encerrar.

---

## ▶️ Como Executar

```bash
# a partir da pasta "src"
javac -d out $(find . -name "*.java")
java -cp out Main
```

Ao rodar, o sistema exibirá o menu principal no terminal. Basta digitar o número da opção desejada e seguir as instruções em tela (o `EntradaConsole` cuida da leitura validada de inteiros e textos).

---

## 📌 Resumo — Estrutura × Funcionalidade

| Estrutura de dados | Funcionalidade que a utiliza |
|---|---|
| Vetor dinâmico | Armazenamento geral de livros, usuários, empréstimos, multas, reservas |
| Fila (Queue) | Fila de espera de reservas por livro |
| Pilha (Stack) | Undo/Redo de ações (Command Pattern) |
| Árvore Binária de Busca | Índice de busca por ISBN/título e mapa ISBN → fila de reservas |
| Matriz | Estatísticas mensais de empréstimos por categoria |
| Bubble / Selection / Insertion Sort | Ordenação por ano, ranking de mais emprestados e ranking de atrasos |