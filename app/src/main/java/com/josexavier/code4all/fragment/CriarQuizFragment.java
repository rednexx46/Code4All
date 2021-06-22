package com.josexavier.code4all.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.josexavier.code4all.R;
import com.josexavier.code4all.activity.Validacao;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Introducao;
import com.josexavier.code4all.model.Pergunta;
import com.josexavier.code4all.model.Quiz;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class CriarQuizFragment extends Fragment {

    private final int idCharInicial = 65;
    private final int NULO = 0;
    private final int UNICA = 1;
    private final int MULTIPLA = 2;
    private final int SELECAO_CAMARA = 100;
    private final int SELECAO_GALERIA = 200;

    private Bitmap imagem = null;

    private int passoCriarQuiz = 1, opSelecionada = 0, totalXP = 0;
    private EditText editTextTituloQuiz, editTextIntroTitulo, editTextIntroDescricao, editTextIntroVideo, editTextPerguntaID, editTextPerguntaTitulo, editTextPerguntaXP;
    private Spinner spinnerTemas, spinnerPergunta;
    private Button botaoAdicionarOP;
    private ImageView imageFotoCriarQuiz;
    private TextView textTitulo, textSubTitulo;
    private List<String> temas = new ArrayList<>();
    private LinearLayout linearLayout, linearLayout2, linearLayout3;
    private int idChar = 65, idPergunta = 1, xpValor = 0, tipoPergunta = 0;
    private byte[] dadosImagem;
    private String fotoQuiz;

    private Button botaoPassoAnterior, botaoPassoSeguinte, botaoNovaPergunta, botaoRemoverOP, botaoDefinirSolucao;
    private List<CharSequence> solucao;
    private List<String> opcoes;
    private boolean isVetorPreenchido = false, isCamposPasso3Preenchido = false;
    private boolean[] boolArray;
    private String solucaoFinal;
    private List<Pergunta> perguntas = new ArrayList<>();

    private Dialog dialogCarregamento;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_criar_quiz, container, false);

        dialogCarregamento = new SpotsDialog.Builder().setContext(getContext()).setMessage("Criando Quiz...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();

        linearLayout = root.findViewById(R.id.linearLayoutCriarQuiz);
        linearLayout2 = root.findViewById(R.id.linearLayoutCriarQuiz2);
        linearLayout3 = root.findViewById(R.id.linearLayoutCriarQuiz3);
        botaoPassoSeguinte = root.findViewById(R.id.buttonCriarQuizAvancar);
        botaoPassoAnterior = root.findViewById(R.id.buttonCriarQuizPassoAnterior);
        botaoPassoAnterior.setOnClickListener(view -> passoAnterior());

        textTitulo = root.findViewById(R.id.textCriarQuizTitulo);
        textSubTitulo = root.findViewById(R.id.textCriarQuizSubTitulo);

        // Passo 1
        editTextTituloQuiz = root.findViewById(R.id.editTextCriarQuizTitulo);
        spinnerTemas = root.findViewById(R.id.spinnerCriarQuizTemas);

        // Passo 2
        editTextIntroTitulo = root.findViewById(R.id.editTextCriarQuizIntroTitulo);
        editTextIntroDescricao = root.findViewById(R.id.editTextCriarQuizIntroDescricao);
        editTextIntroVideo = root.findViewById(R.id.editTextCriarQuizIntroVideo);

        // Passo 3
        editTextPerguntaID = root.findViewById(R.id.editTextCriarQuizPerguntaID);
        editTextPerguntaTitulo = root.findViewById(R.id.editTextCriarQuizPerguntaTitulo);
        botaoAdicionarOP = root.findViewById(R.id.buttonCriarQuizAdicionarOpcao);
        editTextPerguntaXP = root.findViewById(R.id.editTextCriarQuizPerguntaXP);
        spinnerPergunta = root.findViewById(R.id.editTextCriarQuizPerguntaTipo);
        botaoDefinirSolucao = root.findViewById(R.id.buttonCriarQuizDefinirSolucao);
        botaoDefinirSolucao.setOnClickListener(view -> abrirDialog());
        botaoNovaPergunta = root.findViewById(R.id.buttonCriarQuizNovaPergunta);
        botaoNovaPergunta.setOnClickListener(view -> novaPergunta(root));
        botaoRemoverOP = root.findViewById(R.id.buttonCriarQuizRemoverOpcao);
        botaoRemoverOP.setOnClickListener(view -> removerOpcao(root));
        adicionarEditText();

        //Câmara, Galeria e Foto
        ImageButton imageButtonGaleria;
        imageFotoCriarQuiz = root.findViewById(R.id.imageCriarQuiz);
        imageButtonGaleria = root.findViewById(R.id.imageButtonCriarQuizGaleria);

        imageButtonGaleria.setOnClickListener(view -> abrirGaleria());

        buscarTemas();

        Introducao introducao = new Introducao();
        Quiz quiz = new Quiz();

        botaoPassoSeguinte.setOnClickListener(v -> {
            if (passoCriarQuiz == 1) {
                String titulo = editTextTituloQuiz.getText().toString();
                int tema = spinnerTemas.getSelectedItemPosition();
                if (!titulo.isEmpty()) {
                    if (tema > 0) {

                        quiz.setTitulo(titulo);
                        quiz.setTema(temas.get(tema));

                        linearLayout.setVisibility(View.GONE);
                        linearLayout2.setVisibility(View.VISIBLE);
                        textTitulo.setText("Introdução");
                        textSubTitulo.setText("Passo 2/3");
                        botaoPassoAnterior.setVisibility(View.VISIBLE);
                        passoCriarQuiz = 2;
                    } else {
                        Toast.makeText(getContext(), "Escolha um Tema!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Introduza um Título!", Toast.LENGTH_SHORT).show();
                }
            } else if (passoCriarQuiz == 2) {
                String titulo = editTextIntroTitulo.getText().toString();
                String descricao = editTextIntroDescricao.getText().toString();
                String video = editTextIntroVideo.getText().toString();

                if (!titulo.isEmpty()) {
                    if (!descricao.isEmpty()) {
                        if (!video.isEmpty()) {

                            // guardar na classe Introducao
                            introducao.setTitulo(titulo);
                            introducao.setDescricao(descricao);
                            introducao.setVideo(video);

                            textTitulo.setText("Perguntas");
                            textSubTitulo.setText("Passo 3/3");
                            botaoPassoSeguinte.setText("Finalizar Quiz!");
                            linearLayout2.setVisibility(View.GONE);
                            linearLayout3.setVisibility(View.VISIBLE);
                            configuracaoSpinnerPergunta();
                            botaoAdicionarOP.setVisibility(View.VISIBLE);
                            botaoAdicionarOP.setOnClickListener(view -> adicionarEditText());
                            editTextPerguntaID.setText("ID da Pergunta .: " + idPergunta);
                            botaoRemoverOP.setVisibility(View.VISIBLE);
                            botaoNovaPergunta.setVisibility(View.VISIBLE);
                            botaoDefinirSolucao.setVisibility(View.VISIBLE);
                            passoCriarQuiz = 3;
                        } else {
                            Toast.makeText(getContext(), "Introduza um Link (YouTube)!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Introduza uma descrição!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Introduza um Título!", Toast.LENGTH_SHORT).show();
                }
            } else if (passoCriarQuiz == 3) {
                verificarCamposPasso3(root);
                if (isCamposPasso3Preenchido) {

                    if (imagem != null) {
                        dialogCarregamento.show();
                        guardarPergunta();


                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        Date date = new Date(System.currentTimeMillis());
                        String dataCriacao = formatter.format(date);
                        String nomeUtilizador = DefinicaoFirebase.recuperarAutenticacao().getCurrentUser().getDisplayName();
                        String idUtilizador = DefinicaoFirebase.recuperarAutenticacao().getUid();
                        quiz.setIdCriador(idUtilizador);
                        quiz.setAutoId();
                        quiz.setTotalXP(totalXP);
                        quiz.setDataCriacao(dataCriacao);
                        quiz.setCriador(nomeUtilizador);
                        quiz.setTotalPerguntas(idPergunta);
                        quiz.setPerguntas(perguntas);
                        quiz.setIntroducao(introducao);

                        guardarFoto(quiz.getId(), validar -> {
                            if (validar) {
                                quiz.setImagem(fotoQuiz);
                                quiz.guardar(validar2 -> {
                                    if (validar2) {
                                        dialogCarregamento.dismiss();
                                        Toast.makeText(getContext(), "Quiz Criado com sucesso!", Toast.LENGTH_SHORT).show();
                                        getActivity().onBackPressed();
                                    } else {
                                        dialogCarregamento.dismiss();
                                        Toast.makeText(getContext(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Escolha uma Foto para o Quiz!", Toast.LENGTH_SHORT).show();
                    }

                }
            }

        });

        return root;

    }

    private void abrirGaleria() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (i.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(i, SELECAO_GALERIA);
        }
    }

    private void abrirCamara() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (i.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(i, SELECAO_CAMARA);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        imagem = null;

        try {

            switch (requestCode) {
                case SELECAO_CAMARA:
                    imagem = (Bitmap) data.getExtras().get("data");
                    break;
                case SELECAO_GALERIA:
                    Uri localImagemSelecionada = data.getData();

                    if (android.os.Build.VERSION.SDK_INT >= 29) {
                        // Usar versão mais recente do código
                        ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), localImagemSelecionada);
                        imagem = ImageDecoder.decodeBitmap(source);
                    } else {
                        /// Usar versão mais antiga do código
                        imagem = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), localImagemSelecionada);
                    }
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Erro ao Recuperar Imagem!" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // COMECA TUDO AQUI
        if (imagem != null) {
            imageFotoCriarQuiz.setImageBitmap(imagem);

            // Recuperar Dados da Imagem para o Firebase
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imagem.compress(Bitmap.CompressFormat.PNG, 100, baos);
            dadosImagem = baos.toByteArray();
        }

    }

    private void guardarFoto(String idQuiz, Validacao validacao) {
        StorageReference imagemQuizRef = DefinicaoFirebase.recuperarArmazenamento().child("imagens/quizes/").child(idQuiz + ".png");

        UploadTask uploadTask = imagemQuizRef.putBytes(dadosImagem);

        uploadTask.addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
            fotoQuiz = uri.toString();
            validacao.isValidacaoSucesso(true);
        }).addOnFailureListener(e -> validacao.isValidacaoSucesso(false)));
    }

    private void guardarPergunta() {
        String titulo = editTextPerguntaTitulo.getText().toString();
        Pergunta perguntaGuardar = new Pergunta();
        if (spinnerPergunta.getSelectedItemPosition() == UNICA) perguntaGuardar.setTipo("unica");
        else perguntaGuardar.setTipo("multipla");
        perguntaGuardar.setId(idPergunta);
        perguntaGuardar.setXp(xpValor);
        perguntaGuardar.setTitulo(titulo);
        perguntaGuardar.setSolucao(solucaoFinal);
        perguntaGuardar.setOpcoesPergunta(opcoes);
        perguntas.add(perguntaGuardar);
        totalXP += xpValor;
    }

    private void abrirDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Definir Solução da Pergunta");
        builder.setCancelable(false);
        List<String> array = new ArrayList<>();
        int valor = 0;

        for (int i = idCharInicial; i < idChar; i++) {
            char opcao = (char) i;
            array.add(String.valueOf(opcao));
            valor++;
        }
        final CharSequence[] chars = array.toArray(new CharSequence[array.size()]);

        if (!isVetorPreenchido) {
            solucao = new ArrayList<>();
            boolArray = new boolean[valor];
            Arrays.fill(boolArray, Boolean.FALSE);
            isVetorPreenchido = true;
        }

        if (tipoPergunta == UNICA) {
            builder.setSingleChoiceItems(chars, opSelecionada, (dialog, which) -> {
                solucao.clear();
                solucao.add(0, array.get(which));
                opSelecionada = which;
                definirSolucaoPergunta();
                dialog.dismiss();
            });
        } else if (tipoPergunta == MULTIPLA) {
            builder.setMultiChoiceItems(chars, boolArray, (dialog, which, isChecked) -> {
                boolArray[which] = true;
                solucao.add(chars[which]);
            });
            builder.setNegativeButton("Limpar Seleção", (dialog, which) -> {
                Arrays.fill(boolArray, Boolean.FALSE);
                solucao.clear();
                solucaoFinal = "";
                dialog.dismiss();
            });
            builder.setPositiveButton("Confirmar Seleção", (dialog, which) -> {
                definirSolucaoPergunta();
                dialog.dismiss();
            });
        }
        if (tipoPergunta != NULO) {
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            Toast.makeText(getContext(), "É preciso definir o tipo de solução!", Toast.LENGTH_SHORT).show();
        }
    }

    private void limparCampos(View root) {
        EditText primeiraOpcao = root.findViewById(idCharInicial);
        editTextPerguntaTitulo.setText("");
        editTextPerguntaXP.setText("");
        spinnerPergunta.setSelection(0);
        opSelecionada = 0;
        for (int i = idCharInicial; i <= idChar; i++) {
            removerOpcao(root);
        }

        primeiraOpcao.setText("");

        solucao.clear();
        isVetorPreenchido = false;
    }

    private void novaPergunta(View root) {
        verificarCamposPasso3(root);
        if (isCamposPasso3Preenchido) {
            guardarPergunta();
            limparCampos(root);
            idPergunta++;
            editTextPerguntaID.setText("ID da Pergunta .: " + idPergunta);
        }
    }

    private void verificarCamposPasso3(View root) {
        isCamposPasso3Preenchido = false;
        String titulo = editTextPerguntaTitulo.getText().toString();
        int pergunta = spinnerPergunta.getSelectedItemPosition();
        String xp = editTextPerguntaXP.getText().toString();
        xpValor = 0;
        opcoes = new ArrayList<>();
        for (int i = idCharInicial; i < idChar; i++) {
            EditText editTextOpcao = root.findViewById(i);
            opcoes.add(editTextOpcao.getText().toString());
        }
        if (!titulo.isEmpty()) {
            if (pergunta != NULO) {
                if (!xp.isEmpty()) {
                    boolean isNulo = opcoes.isEmpty();
                    for (int i = 0; i < opcoes.size(); i++) {
                        if (opcoes.get(i) == null || opcoes.get(i).equals("")) isNulo = true;
                    }
                    if (!isNulo) {
                        if (solucao != null) {
                            isCamposPasso3Preenchido = true;
                            xpValor = new Integer(editTextPerguntaXP.getText().toString());
                        } else {
                            Toast.makeText(getContext(), "É preciso definir a solução da Pergunta", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Preencha todas as opções de Solução!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Introduza o XP da Pergunta!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "É preciso definir o tipo de solução!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Introduza um Título para a Pergunta!", Toast.LENGTH_SHORT).show();
        }

    }

    private void definirSolucaoPergunta() {
        if (solucao.size() == 1) {
            solucaoFinal = solucao.get(0).toString();
        } else if (solucao.size() > 1) {
            for (int i = 0; i < solucao.size(); i++) {
                solucaoFinal += solucao.get(i).toString();
            }
        } else {
            Toast.makeText(getContext(), "É preciso definir a solução da Pergunta!", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getContext(), "Solução Atual : " + solucaoFinal, Toast.LENGTH_SHORT).show();
    }

    private void passoAnterior() {
        if (passoCriarQuiz == 2) {
            textTitulo.setText("Criar Quiz");
            textSubTitulo.setText("Passo 1/3");
            passoCriarQuiz = 1;
            linearLayout3.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
            botaoPassoAnterior.setVisibility(View.GONE);
            linearLayout2.setVisibility(View.GONE);
            botaoDefinirSolucao.setVisibility(View.GONE);
        } else if (passoCriarQuiz == 3) {
            textTitulo.setText("Introdução");
            textSubTitulo.setText("Passo 2/3");
            passoCriarQuiz = 2;
            linearLayout3.setVisibility(View.GONE);
            linearLayout2.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            botaoPassoAnterior.setVisibility(View.VISIBLE);
            botaoAdicionarOP.setVisibility(View.GONE);
            botaoRemoverOP.setVisibility(View.GONE);
            botaoNovaPergunta.setVisibility(View.GONE);
            botaoDefinirSolucao.setVisibility(View.GONE);
            botaoPassoSeguinte.setText("Passo Seguinte");
        }
    }

    private void configuracaoSpinnerPergunta() {
        List<String> opcoesPergunta = new ArrayList<>();
        opcoesPergunta.add("Selecionar Opção");
        opcoesPergunta.add("Única");
        opcoesPergunta.add("Múltipla");

        ArrayAdapter<List<String>> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, opcoesPergunta);
        spinnerPergunta.setAdapter(adapter);
        if (tipoPergunta != NULO) {
            spinnerPergunta.setSelection(tipoPergunta);
        }
        spinnerPergunta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipoPergunta = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void removerOpcao(View root) {
        if (idChar == 65) { // PRIMEIRA OP // BUG
            Toast.makeText(getContext(), "Operação Inválida!", Toast.LENGTH_SHORT).show();
        } else { // SE NAO PODE APAGAR :)
            isVetorPreenchido = false;
            if (!solucaoFinal.isEmpty() || !solucaoFinal.equals("")) {
                solucao.clear();
                solucaoFinal = "";
            }
            idChar--;
            ViewGroup layout = (ViewGroup) root.findViewById(idChar).getParent();
            if (layout != null) {
                layout.removeView(root.findViewById(idChar));
            }
        }
    }

    private void adicionarEditText() {
        if (idChar < 71) {
            EditText editText = new EditText(getContext());
            Drawable bordaPreta = ContextCompat.getDrawable(getContext(), R.drawable.borda_preta_quiz);
            Typeface face = ResourcesCompat.getFont(getContext(), R.font.montserrat_medium);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(0, 16, 0, 16);

            char opcao = (char) idChar;

            editText.setId(idChar);
            editText.setHint("Opção " + opcao);
            editText.setBackground(bordaPreta);
            editText.setTypeface(face);
            editText.setLayoutParams(params);
            linearLayout3.addView(editText);
            isVetorPreenchido = false;
            solucaoFinal = "";
            if (solucao != null) {
                solucao.clear();
            }
            idChar++;
        } else {
            if (idChar == 71)
                Toast.makeText(getContext(), "Chegou ao Limite de Opções!", Toast.LENGTH_LONG).show();
        }
    }

    private void buscarTemas() {
        DatabaseReference referenciaTemas = DefinicaoFirebase.recuperarBaseDados().child("temas");
        referenciaTemas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                temas.add("Selecionar Tema");
                for (DataSnapshot dados : snapshot.getChildren()) {
                    temas.add(dados.getValue(String.class));
                }
                ArrayAdapter<List<String>> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, temas);
                spinnerTemas.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}