package com.josexavier.code4all.model;

import java.util.List;

public class Pergunta {

    public int id, xp;
    public String titulo, solucao, tipo;
    public List<String> opcoesPergunta;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSolucao() {
        return solucao;
    }

    public void setSolucao(String solucao) {
        this.solucao = solucao;
    }

    public List<String> getOpcoesPergunta() {
        return opcoesPergunta;
    }

    public void setOpcoesPergunta(List<String> opcoesPergunta) {
        this.opcoesPergunta = opcoesPergunta;
    }
}
