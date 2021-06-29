package com.josexavier.code4all.model;

import com.google.firebase.database.DatabaseReference;
import com.josexavier.code4all.interfaces.Validacao;
import com.josexavier.code4all.helper.DefinicaoFirebase;

import java.util.HashMap;
import java.util.List;

public class Quiz {

    private String id, titulo, criador, idCriador, tema, imagem, experiencia, dataCriacao, dataInscricao, pontuacao;
    private int totalPerguntas, progresso, perguntaAtual, totalMembros, totalXP;
    private double classificacao;
    private Introducao introducao;
    private List<Pergunta> perguntas;
    private DatabaseReference quiz = DefinicaoFirebase.recuperarBaseDados();
    private HashMap<Object, Object> quizMap = new HashMap<>();

    public void guardar(Validacao validacao) {
        quiz = quiz.child("quizes").child(getId());

        // Básico
        quizMap.put("id", this.id);
        quizMap.put("classificacao", 5);
        quizMap.put("criador", this.criador);
        quizMap.put("dataCriacao", this.dataCriacao);
        quizMap.put("imagem", this.imagem);
        quizMap.put("totalPerguntas", this.totalPerguntas);
        quizMap.put("titulo", this.titulo);
        quizMap.put("tema", this.tema);
        quizMap.put("totalMembros", 0);
        quizMap.put("totalXP", totalXP);
        quizMap.put("idCriador", idCriador);

        // Introdução
        quizMap.put("introducao", this.introducao);

        // Perguntas
        quizMap.put("perguntas", this.perguntas);

        // Guardar
        quiz.setValue(quizMap).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                validacao.isValidacaoSucesso(true);
            else
                validacao.isValidacaoSucesso(false);
        });
    }

    public void guardar(DatabaseReference referencia, Validacao validacao) {
        referencia.setValue(this).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                validacao.isValidacaoSucesso(true);
            else
                validacao.isValidacaoSucesso(false);
        });
    }

    public String getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(String pontuacao) {
        this.pontuacao = pontuacao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAutoId() {
        this.id = quiz.push().getKey();
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIdCriador() {
        return idCriador;
    }

    public void setIdCriador(String idCriador) {
        this.idCriador = idCriador;
    }

    public int getTotalXP() {
        return totalXP;
    }

    public void setTotalXP(int totalXP) {
        this.totalXP = totalXP;
    }

    public int getTotalMembros() {
        return totalMembros;
    }

    public void setTotalMembros(int totalMembros) {
        this.totalMembros = totalMembros;
    }

    public List<Pergunta> getPerguntas() {
        return perguntas;
    }

    public void setPerguntas(List<Pergunta> perguntas) {
        this.perguntas = perguntas;
    }

    public Introducao getIntroducao() {
        return introducao;
    }

    public void setIntroducao(Introducao introducao) {
        this.introducao = introducao;
    }

    public String getCriador() {
        return criador;
    }

    public void setCriador(String criador) {
        this.criador = criador;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(String experiencia) {
        this.experiencia = experiencia;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public int getPerguntaAtual() {
        return perguntaAtual;
    }

    public void setPerguntaAtual(int perguntaAtual) {
        this.perguntaAtual = perguntaAtual;
    }

    public int getTotalPerguntas() {
        return totalPerguntas;
    }

    public void setTotalPerguntas(int totalPerguntas) {
        this.totalPerguntas = totalPerguntas;
    }

    public double getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(double classificacao) {
        this.classificacao = classificacao;
    }

    public String getDataInscricao() {
        return dataInscricao;
    }

    public void setDataInscricao(String dataInscricao) {
        this.dataInscricao = dataInscricao;
    }

    public int getProgresso() {
        return progresso;
    }

    public void setProgresso(int progresso) {
        this.progresso = progresso;
    }
}
