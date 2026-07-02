package br.edu.biblioteca.service;

import br.edu.biblioteca.action.Acao;
import br.edu.biblioteca.structures.MinhaPilha;

public class UndoRedoService {

    private MinhaPilha<Acao> pilhaUndo;
    private MinhaPilha<Acao> pilhaRedo;

    public UndoRedoService() {
        this.pilhaUndo = new MinhaPilha<>();
        this.pilhaRedo = new MinhaPilha<>();
    }

    // Executa a acao e a registra na pilha de undo (limpa o redo)
    public boolean executarAcao(Acao acao) {
        boolean ok = acao.executar();
        if (ok) {
            pilhaUndo.push(acao);
            pilhaRedo.limpar();
            System.out.println("  [Undo/Redo] Registrado: " + acao.descricao());
        }
        return ok;
    }

    // Desfaz a ultima acao executada
    public boolean desfazer() {
        if (pilhaUndo.isEmpty()) {
            System.out.println("  [Undo/Redo] Nenhuma acao para desfazer.");
            return false;
        }
        Acao acao = pilhaUndo.pop();
        boolean ok = acao.desfazer();
        if (ok) {
            pilhaRedo.push(acao);
            System.out.println("  [Undo/Redo] Desfeito: " + acao.descricao());
        }
        return ok;
    }

    // Refaz a ultima acao desfeita
    public boolean refazer() {
        if (pilhaRedo.isEmpty()) {
            System.out.println("  [Undo/Redo] Nenhuma acao para refazer.");
            return false;
        }
        Acao acao = pilhaRedo.pop();
        boolean ok = acao.executar();
        if (ok) {
            pilhaUndo.push(acao);
            System.out.println("  [Undo/Redo] Refeito: " + acao.descricao());
        }
        return ok;
    }

    public boolean podeDesfazer() { return !pilhaUndo.isEmpty(); }
    public boolean podeRefazer()  { return !pilhaRedo.isEmpty(); }

    public String proximoUndo() {
        return pilhaUndo.isEmpty() ? null : pilhaUndo.peek().descricao();
    }

    public String proximoRedo() {
        return pilhaRedo.isEmpty() ? null : pilhaRedo.peek().descricao();
    }
}
