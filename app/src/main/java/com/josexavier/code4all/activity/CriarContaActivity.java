package com.josexavier.code4all.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.josexavier.code4all.R;
import com.josexavier.code4all.helper.Configs;
import com.josexavier.code4all.helper.DefinicaoFirebase;
import com.josexavier.code4all.model.Conta;
import com.josexavier.code4all.model.Empresa;
import com.josexavier.code4all.interfaces.Validacao;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class  CriarContaActivity extends AppCompatActivity {

    private EditText editNome, editEmail, editPassword, editConfirmarPassword, editDataNascimento, editNIF;
    private Spinner spinnerEstado, spinnerSexo;
    private Calendar calendario;
    private FirebaseAuth autenticacao;
    private ToggleButton switchUtilizadorEmpresa;
    private String dataNascimento, nif, estado, sexo;
    private Conta conta;
    private List<String> listaEstado = new ArrayList<>();
    private List<String> listaSexo = new ArrayList<>();
    private AlertDialog dialog;
    private CheckBox checkBoxEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_conta);

        configuracoesIniciais();

    }

    private void configuracoesIniciais() {
        editNome = findViewById(R.id.editRegistrarNome);
        editEmail = findViewById(R.id.editRegistrarEmail);
        editPassword = findViewById(R.id.editRegistrarPassword);
        editConfirmarPassword = findViewById(R.id.editRegistrarConfirmarPassword);
        editDataNascimento = findViewById(R.id.editRegistrarDataNascimento);
        spinnerEstado = findViewById(R.id.spinnerEstado);
        spinnerSexo = findViewById(R.id.spinnerSexo);
        editNIF = findViewById(R.id.editRegistrarNIF);
        editNIF.setVisibility(View.GONE);
        checkBoxEmail = findViewById(R.id.checkBoxEmail);
        autenticacao = DefinicaoFirebase.recuperarAutenticacao();

        Drawable casa = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_casa);
        casa.setBounds(8, 8, 8, 8);
        Drawable pessoa = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pessoa);
        pessoa.setBounds(8, 8, 8, 8);

        switchUtilizadorEmpresa = findViewById(R.id.toggleButton);
        switchUtilizadorEmpresa.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) { // Empresa
                spinnerSexo.setVisibility(View.GONE);
                spinnerEstado.setVisibility(View.GONE);
                editNome.setCompoundDrawablesWithIntrinsicBounds(casa, null, null, null);
                editNIF.setVisibility(View.VISIBLE);
                editDataNascimento.setVisibility(View.GONE);
            } else { // Utilizador
                spinnerSexo.setVisibility(View.VISIBLE);
                spinnerEstado.setVisibility(View.VISIBLE);
                editNome.setCompoundDrawablesWithIntrinsicBounds(pessoa, null, null, null);
                editNIF.setVisibility(View.GONE);
                editDataNascimento.setVisibility(View.VISIBLE);
            }
        });

        configuracaoCalendario();
        configurarSpinnerEstado();
        configurarSpinnerSexo();

    }

    private void configurarSpinnerSexo() {

        listaSexo.add("Selecionar Sexo...");
        listaSexo.add("Masculino");
        listaSexo.add("Feminino");

        ArrayAdapter<List<String>> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, listaSexo);
        spinnerSexo.setAdapter(adapter);

    }

    private void configurarSpinnerEstado() {

        listaEstado.add("Selecionar Estado...");
        listaEstado.add("Estudante");
        listaEstado.add("Desempregado");
        listaEstado.add("Empregado");

        ArrayAdapter<List<String>> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, listaEstado);
        spinnerEstado.setAdapter(adapter);

    }

    private void configuracaoCalendario() {
        calendario = Calendar.getInstance();

        editDataNascimento.setKeyListener(null);
        DatePickerDialog.OnDateSetListener date = (view, ano, mes, dia) -> {
            calendario.set(Calendar.YEAR, ano);
            calendario.set(Calendar.MONTH, mes);
            calendario.set(Calendar.DAY_OF_MONTH, dia);
            atualizarTexto();
        };

        editDataNascimento.setOnClickListener(v -> new DatePickerDialog(CriarContaActivity.this, date, calendario
                .get(Calendar.YEAR), calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void atualizarTexto() { // Atualizar Texto do EditText DataNascimento
        String formato = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(formato, Locale.US);

        editDataNascimento.setText(sdf.format(calendario.getTime()));
    }

    public void entrarCriarConta(View view) {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    public void verificarDados(View view) {
        String nome = editNome.getText().toString();
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        String confirmarPassword = editConfirmarPassword.getText().toString();
        dataNascimento = "";
        nif = "";

        if (!nome.isEmpty()) {
            if (!email.isEmpty()) {
                if (!password.isEmpty()) {
                    if (password.equals(confirmarPassword)) {
                        if (checkBoxEmail.isChecked()) {
                            if (!switchUtilizadorEmpresa.isChecked()) { // Membro
                                dataNascimento = editDataNascimento.getText().toString();
                                if (spinnerEstado.getSelectedItemPosition() > 0) {
                                    if (spinnerSexo.getSelectedItemPosition() > 0) {
                                        if (!dataNascimento.isEmpty()) {
                                            String iniciais = obterIniciais(nome);
                                            if (iniciais.length() < 3 && iniciais.length() > 0) {
                                                estado = listaEstado.get(spinnerEstado.getSelectedItemPosition());
                                                sexo = listaSexo.get(spinnerSexo.getSelectedItemPosition());
                                                registoConta(nome, email, password);
                                            } else
                                                Toast.makeText(this, "Introduze apenas o Primeiro e Último nome!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(this, "Escolha uma data de nascimento", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(this, "Escolha um Género!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(this, "Selecione um Estado!", Toast.LENGTH_SHORT).show();
                                }

                            } else { // Empresa

                                nif = editNIF.getText().toString();
                                if (!nif.isEmpty()) {
                                    if (nif.length() == 9) { // tiver 9 números correspondentes ao NIF (PT)
                                        registoConta(nome, email, password);
                                    } else {
                                        Toast.makeText(this, "NIF Incorreto!", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(this, "Introduza o NIF da Empresa!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(this, "Para criar uma conta, precisa de aceitar o partilhamento do seu Email!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "As Passwords não coicidem!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Introduza uma Password!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Introduza um email!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Introduza um Nome!", Toast.LENGTH_SHORT).show();
        }

    }

    private void registoConta(String nome, String email, String password) { // Registo da conta, sendo ele de um membro ou de uma empresa
        autenticacao.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!dataNascimento.isEmpty()) { // Utilizador
                    dialog = new SpotsDialog.Builder().setContext(this).setMessage("Criando conta...").setCancelable(false).setTheme(R.style.dialog_carregamento).build();
                    try {
                        dialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    conta = new Conta();
                    conta.setId(task.getResult().getUser().getUid());
                    conta.setNome(nome);
                    conta.setEmail(email);
                    conta.setEstado(estado);
                    conta.setDataNascimento(dataNascimento);
                    conta.setTipo(Configs.grupos[0]);
                    conta.setSexo(sexo);
                    conta.setCorPerfil(Configs.corAleatoria());
                    conta.setCorFundoPerfil(Configs.corAleatoria());
                    conta.setDataInscricao(Configs.recuperarDataHoje());
                    if (sexo.equals("Masculino"))
                        conta.setBiografia("Olá, eu sou o " + nome + "!");
                    else
                        conta.setBiografia("Olá, eu sou a " + nome + "!");
                    conta.setBiografiaData(Configs.recuperarDataHoje());
                    guardarNome(nome, sucessoNome -> {
                        if (sucessoNome)
                            guardarFoto(nome, conta.getCorPerfil(), conta.getTipo(), sucessoFoto -> {
                                if (sucessoFoto) {
                                    Toast.makeText(CriarContaActivity.this, "Conta criada com Sucesso!", Toast.LENGTH_SHORT).show();
                                    limparCampos();
                                    Intent atividadeInscricoes = new Intent(getApplicationContext(), InscricoesActivity.class);
                                    atividadeInscricoes.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    startActivity(atividadeInscricoes);
                                    dialog.dismiss();
                                    IntroActivity.activity.finish(); // Finaliza a IntroActivity
                                    finish();
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(CriarContaActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                }

                            });
                        else {
                            dialog.dismiss();
                            Toast.makeText(CriarContaActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else if (!nif.isEmpty()) {// Empresa (nif não for nulo)
                    dialog = new SpotsDialog.Builder().setContext(this).setMessage("Criando Empresa...").setTheme(R.style.dialog_carregamento).setCancelable(false).build();
                    try {
                        dialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Empresa empresa = new Empresa();
                    empresa.setId(task.getResult().getUser().getUid());
                    empresa.setNome(nome);
                    empresa.setEmail(email);
                    empresa.setNif(nif);
                    empresa.setTipo(Configs.grupos[3]);
                    empresa.setDataDescricao(Configs.recuperarDataHoje());
                    empresa.setCorFundoPerfil(Configs.corAleatoria());
                    guardarNome(nome, sucessoNome -> {
                        if (sucessoNome) {
                            Toast.makeText(getApplicationContext(), "Empresa criada com Sucesso!", Toast.LENGTH_SHORT).show();
                            limparCampos();
                            Intent atividadeInscricoes = new Intent(getApplicationContext(), EmpresaConfiguracaoActivity.class);
                            atividadeInscricoes.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            atividadeInscricoes.putExtra("empresa", empresa);
                            startActivity(atividadeInscricoes);
                            dialog.dismiss();
                            finish();
                        } else
                            Toast.makeText(CriarContaActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                    });
                }
            } else {

                String erro;
                try {
                    throw task.getException();
                } catch (FirebaseAuthWeakPasswordException e) {
                    erro = "A Password introduzida é fraca!";
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    erro = "O Email introduzido é inválido!";
                } catch (FirebaseAuthUserCollisionException e) {
                    erro = "Utilizador com esse email já criado!";
                } catch (Exception e) {
                    erro = "Erro, tente novamente mais tarde!";
                    e.printStackTrace();
                }
                Toast.makeText(CriarContaActivity.this, erro, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void guardarNome(String nome, Validacao validacao) { // Guardar Nome no próprio utilizador da firebase

        UserProfileChangeRequest profileRequest = new UserProfileChangeRequest.Builder().setDisplayName(nome).build();
        FirebaseUser utilizador = autenticacao.getCurrentUser();
        utilizador.updateProfile(profileRequest).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                validacao.isValidacaoSucesso(true);
            else
                validacao.isValidacaoSucesso(false);
        });

    }

    private void guardarFoto(String nome, int corPerfil, String tipo, Validacao validacao) { // Verificar apenas dois nomes e caracteres especiais

        String iniciais = obterIniciais(nome);

        Bitmap bitmap = Configs.desenharTexto(iniciais, 10000, corPerfil);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        StorageReference firebaseStorage = DefinicaoFirebase.recuperarArmazenamento().child("imagens").child("perfil").child(autenticacao.getCurrentUser().getUid()).child("foto.jpeg");
        UploadTask uploadTask = firebaseStorage.putBytes(data);

        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebaseStorage.getDownloadUrl().addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        final String foto = task2.getResult().toString();

                        UserProfileChangeRequest profileRequest = new UserProfileChangeRequest.Builder().setPhotoUri(task2.getResult()).build();
                        FirebaseUser utilizador = autenticacao.getCurrentUser();
                        utilizador.updateProfile(profileRequest).addOnCompleteListener(task3 -> {
                            if (task3.isSuccessful()) {
                                if (tipo.equals(Configs.grupos[0])) { // membro
                                    conta.setFoto(foto);
                                    conta.guardar(sucesso -> {
                                        if (sucesso) {
                                            validacao.isValidacaoSucesso(true);
                                        } else {
                                            validacao.isValidacaoSucesso(false);
                                            Toast.makeText(CriarContaActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(CriarContaActivity.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
            }
        });
    }

    private String obterIniciais(String nome) { // método para obter as iniciais do nome introduzido
        String iniciais = "";
        char c = nome.toUpperCase().charAt(0);
        iniciais += c;
        for (int i = 0; i < nome.length(); i++) {
            char letra = nome.toUpperCase().charAt(i);
            if (letra == ' ') {
                iniciais += nome.toUpperCase().charAt(i + 1);
            }
        }
        return iniciais;
    }

    private void limparCampos() { // limpar todos os campos

        editNome.setText("");
        editEmail.setText("");
        editPassword.setText("");
        editConfirmarPassword.setText("");
        editDataNascimento.setText("");
        editNIF.setText("");

    }

}