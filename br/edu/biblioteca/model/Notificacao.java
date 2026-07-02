package br.edu.biblioteca.model;

import java.time.LocalDate;

public class Notificacao {

    private int id;
    private int usuarioId;
    private String mensagem;
    private LocalDate data;
    private boolean lida;

    public Notificacao(int id, int usuarioId, String mensagem, LocalDate data) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.mensagem = mensagem;
        this.data = data;
        this.lida = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public boolean isLida() { return lida; }
    public void setLida(boolean lida) { this.lida = lida; }

    public String toString() {
        String statusLida = "Nao";
        if (lida) {
            statusLida = "Sim";
        }
        return "Notificacao #" + id
                + " | UsuarioID: " + usuarioId
                + " | Data: " + data
                + " | Lida: " + statusLida
                + " | Msg: " + mensagem;
    }
}
