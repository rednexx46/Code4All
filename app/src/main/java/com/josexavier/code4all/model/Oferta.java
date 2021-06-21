package com.josexavier.code4all.model;

import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Oferta {

    public String id, nomeEmpresa, nomeUtilizador, emailUtilizador, estado, idUtilizador, idEmpresa, fotoEmpresa, fotoUtilizador, titulo, descricao, mensagem, dataEnvio, dataVista;

    public void guardar() {
        DatabaseReference referenciaOfertas = DefinicaoFirebase.recuperarBaseDados().child("ofertas");
        setId(referenciaOfertas.push().getKey());
        referenciaOfertas.child(getId()).setValue(this); // Interface Validacao
    }

    public String getEmailUtilizador() {
        return emailUtilizador;
    }

    public void setEmailUtilizador(String emailUtilizador) {
        this.emailUtilizador = emailUtilizador;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getNomeUtilizador() {
        return nomeUtilizador;
    }

    public void setNomeUtilizador(String nomeUtilizador) {
        this.nomeUtilizador = nomeUtilizador;
    }

    public String getIdUtilizador() {
        return idUtilizador;
    }

    public void setIdUtilizador(String idUtilizador) {
        this.idUtilizador = idUtilizador;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getFotoEmpresa() {
        return fotoEmpresa;
    }

    public void setFotoEmpresa(String fotoEmpresa) {
        this.fotoEmpresa = fotoEmpresa;
    }

    public String getFotoUtilizador() {
        return fotoUtilizador;
    }

    public void setFotoUtilizador(String fotoUtilizador) {
        this.fotoUtilizador = fotoUtilizador;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(String dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public String getDataVista() {
        return dataVista;
    }

    public void setDataVista(String dataVista) {
        this.dataVista = dataVista;
    }
}
