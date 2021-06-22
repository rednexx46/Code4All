package com.josexavier.code4all.model;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.josexavier.code4all.activity.Validacao;
import com.josexavier.code4all.helper.DefinicaoFirebase;

import java.io.Serializable;

public class Empresa implements Serializable {

    private String id, nome, foto, email, nif, localidade, empregados, descricao, dataInscricao, dataDescricao, tipo;
    private int corFundoPerfil;

    public void guardar(Validacao sucesso) {
        DatabaseReference empresaRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(getId());
        empresaRef.setValue(this).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                sucesso.isValidacaoSucesso(true);
            else
                sucesso.isValidacaoSucesso(false);
        });
    }

    public void guardarImagem(byte[] imagem, Validacao validacao) {
        StorageReference postRef = DefinicaoFirebase.recuperarArmazenamento().child("imagens").child("perfil").child(getId()).child("foto.png");
        UploadTask uploadTask = postRef.putBytes(imagem);
        uploadTask.addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {

            setFoto(uri.toString());

            UserProfileChangeRequest profileRequest = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
            FirebaseUser utilizador = DefinicaoFirebase.recuperarAutenticacao().getCurrentUser();
            utilizador.updateProfile(profileRequest).addOnCompleteListener(task -> {
                if (task.isSuccessful())
                    validacao.isValidacaoSucesso(true);
                else
                    validacao.isValidacaoSucesso(false);
            });

        }));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getDataDescricao() {
        return dataDescricao;
    }

    public void setDataDescricao(String dataDescricao) {
        this.dataDescricao = dataDescricao;
    }

    public int getCorFundoPerfil() {
        return corFundoPerfil;
    }

    public void setCorFundoPerfil(int corFundoPerfil) {
        this.corFundoPerfil = corFundoPerfil;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public String getEmpregados() {
        return empregados;
    }

    public void setEmpregados(String empregados) {
        this.empregados = empregados;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDataInscricao() {
        return dataInscricao;
    }

    public void setDataInscricao(String dataInscricao) {
        this.dataInscricao = dataInscricao;
    }
}
