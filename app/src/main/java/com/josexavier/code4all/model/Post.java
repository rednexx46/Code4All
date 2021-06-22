package com.josexavier.code4all.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.josexavier.code4all.activity.Validacao;
import com.josexavier.code4all.helper.DefinicaoFirebase;

public class Post {

    private String id, idCriador, criador, dataCriacao, titulo, descricao, tag, imagem, estado;
    private int comentarios, gostos;

    public void guardar(Validacao validacao) {
        DatabaseReference postRef = DefinicaoFirebase.recuperarBaseDados().child("posts").child(getId());
        postRef.setValue(this).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                validacao.isValidacaoSucesso(true);
            } else {
                validacao.isValidacaoSucesso(false);
            }
        });
    }

    public void guardarImagem(byte[] imagem, Validacao validacao) {
        StorageReference postRef = DefinicaoFirebase.recuperarArmazenamento().child("imagens").child("posts").child(getId() + ".png");
        UploadTask uploadTask = postRef.putBytes(imagem);
        uploadTask.addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
            setImagem(uri.toString());
            validacao.isValidacaoSucesso(true);
        }).addOnFailureListener(e -> validacao.isValidacaoSucesso(false)));
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCriador() {
        return criador;
    }

    public void setCriador(String criador) {
        this.criador = criador;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdCriador() {
        return idCriador;
    }

    public void setIdCriador(String idCriador) {
        this.idCriador = idCriador;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public int getComentarios() {
        return comentarios;
    }

    public void setComentarios(int comentarios) {
        this.comentarios = comentarios;
    }

    public int getGostos() {
        return gostos;
    }

    public void setGostos(int gostos) {
        this.gostos = gostos;
    }
}
