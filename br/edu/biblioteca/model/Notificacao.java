package br.edu.biblioteca.model;

import java.time.LocalDate;

public class Notificacao {

    private int id; // Identificador único da notificação
    private int usuarioId; // ID do usuário que receberá a notificação
    private String mensagem;   // Mensagem da notificação
    private LocalDate data; // Data em que a notificação foi criada
    private boolean lida; // Indica se a notificação já foi lida

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
