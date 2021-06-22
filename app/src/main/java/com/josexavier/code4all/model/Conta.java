package com.josexavier.code4all.model;

import com.google.firebase.database.DatabaseReference;
import com.josexavier.code4all.activity.Validacao;
import com.josexavier.code4all.helper.DefinicaoFirebase;

public class Conta {

    public String id, nome, sexo, email, tipo, nif, dataNascimento, dataInscricao, foto, biografia, biografiaData, grupo, estado;
    public int corPerfil, corFundoPerfil, totalXP;

    public void guardar(Validacao validacao) {
        DatabaseReference contaRef = DefinicaoFirebase.recuperarBaseDados().child("contas").child(getId());

        contaRef.setValue(this).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                validacao.isValidacaoSucesso(true);
            else
                validacao.isValidacaoSucesso(false);
        });
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getTotalXP() {
        return totalXP;
    }

    public void setTotalXP(int totalXP) {
        this.totalXP = totalXP;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public String getBiografiaData() {
        return biografiaData;
    }

    public void setBiografiaData(String biografiaData) {
        this.biografiaData = biografiaData;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getDataInscricao() {
        return dataInscricao;
    }

    public void setDataInscricao(String dataInscricao) {
        this.dataInscricao = dataInscricao;
    }

    public int getCorPerfil() {
        return corPerfil;
    }

    public void setCorPerfil(int corPerfil) {
        this.corPerfil = corPerfil;
    }

    public int getCorFundoPerfil() {
        return corFundoPerfil;
    }

    public void setCorFundoPerfil(int corFundoPerfil) {
        this.corFundoPerfil = corFundoPerfil;
    }
}
