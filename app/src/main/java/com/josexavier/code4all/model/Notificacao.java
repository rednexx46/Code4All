package com.josexavier.code4all.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.josexavier.code4all.activity.Validacao;
import com.josexavier.code4all.helper.DefinicaoFirebase;

public class Notificacao {

    public String id, imagem, titulo, descricao, data;

    public void guardar(Validacao validacao) {
        DatabaseReference postRef = DefinicaoFirebase.recuperarBaseDados().child("notificacoes").child(getId());
        postRef.setValue(this).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                validacao.isValidacaoSucesso(true);
            } else {
                validacao.isValidacaoSucesso(false);
            }
        });
    }

    public void guardarImagem(byte[] imagem, Validacao validacao) {
        StorageReference postRef = DefinicaoFirebase.recuperarArmazenamento().child("imagens").child("notificacoes").child(getId() + ".png");
        UploadTask uploadTask = postRef.putBytes(imagem);
        uploadTask.addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
            setImagem(uri.toString());
            validacao.isValidacaoSucesso(true);
        }).addOnFailureListener(e -> validacao.isValidacaoSucesso(false)));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
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
}
