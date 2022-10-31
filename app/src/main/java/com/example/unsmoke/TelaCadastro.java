package com.example.unsmoke;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class TelaCadastro extends AppCompatActivity {

    EditText nomeCadastro, sobrenomeCadastro, telefoneCadastro, emailCadastro, senhaCadastro, repeteSenha;
    Button continuar;
    CheckBox checkUso;

    DateTimeFormatter formata = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    LocalDate data = LocalDate.now();
    String dataCadastro = data.format(formata);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_cadastro);
        getWindow().setStatusBarColor(Color.rgb(12, 76, 120));
        getSupportActionBar().hide();

        iniciarComponentes();
    }

    public void iniciarComponentes(){
        nomeCadastro = findViewById(R.id.nomeCadastro);
        sobrenomeCadastro = findViewById(R.id.sobrenomeCadastro);
        telefoneCadastro = findViewById(R.id.telefoneCadastro);
        emailCadastro = findViewById(R.id.emailCadastro);
        senhaCadastro = findViewById(R.id.senhaCadastro);
        repeteSenha = findViewById(R.id.repeteSenha);
        continuar = findViewById(R.id.btnContinuar);
        checkUso = findViewById(R.id.checkUso);
    }

    public void verificarPreenchimento(View v){
        String nome = nomeCadastro.getText().toString();
        String sobrenome = sobrenomeCadastro.getText().toString();
        String telefone = telefoneCadastro.getText().toString();
        String email = emailCadastro.getText().toString();
        String senha = senhaCadastro.getText().toString();
        String confirmSenha = repeteSenha.getText().toString();

        if (nome.length() == 0){
            nomeCadastro.setError("Você precisa inserir o seu nome para se cadastrar!");
        }
        else if (sobrenome.length() == 0){
            sobrenomeCadastro.setError("Você precisa inserir o seu sobrenome para continuar!");
        }
        else if (telefone.length() != 16){
            telefoneCadastro.setError("Você precisa inserir o seu telefone corretamente!");
        }
        else if (email.length() < 5){
            emailCadastro.setError("Você precisa inserir um email válido!");
        }
        else if (senha.length() < 8){
            senhaCadastro.setError("A sua deve ter pelo menos 8 caracteres!");
        }
        else if (confirmSenha.length() < 8){
            repeteSenha.setError("Você deve confirmar a sua senha corretamente!");
        }
        else if(checkUso.isChecked()){
            if (senha.equals(confirmSenha)){
                CadastrarUsuario(email, senha, v);
            } else {
                repeteSenha.setError("As senhas não batem!");
            }
        }
        else {
            checkUso.setError("Você deve concordar com os termos para continuar!");
        }
    }

    public void CadastrarUsuario(String email, String senha, View v){

        Intent irTelaContaCriada = new Intent(this, TelaPadraoUso.class);

        FirebaseHelper.getFirebaseAuth().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(task -> {
            if (task.isSuccessful()){

                SalvarDadosUsuario();

                startActivity(irTelaContaCriada);
            }else{
                String erro;
                try {
                    throw task.getException();
                }catch (FirebaseAuthWeakPasswordException e){
                    erro = "Digite uma senha com no mínimo 6 caracteres!";
                }catch (FirebaseAuthUserCollisionException e){
                    erro = "Esta conta de email já está cadastrada!";
                }catch (FirebaseAuthInvalidCredentialsException e){
                    erro = "Email inválido";
                }catch (Exception e){
                    erro = "Erro ao cadastrar o usuário";
                }

                Snackbar snackbar = Snackbar.make(v,erro,Snackbar.LENGTH_LONG);
                snackbar.setBackgroundTint(Color.WHITE);
                snackbar.setTextColor(Color.BLACK);
                snackbar.show();
            }
        });
    }

    private void SalvarDadosUsuario(){

        String nome = nomeCadastro.getText().toString();
        String sobrenome = sobrenomeCadastro.getText().toString();
        String telefone = telefoneCadastro.getText().toString();
        String email = emailCadastro.getText().toString();
        String senha = senhaCadastro.getText().toString();
        String prevStarted = "no";

        Map<String, Object> usuario = new HashMap<>();
        usuario.put("Nome", nome);
        usuario.put("Sobrenome", sobrenome);
        usuario.put("Telefone", telefone);
        usuario.put("Email", email);
        usuario.put("Senha", senha);
        usuario.put("Data de cadastro", dataCadastro);
        usuario.put("BooleanModalProgresso", prevStarted);
        usuario.put("Tem registros de cigarro?", false);

        DocumentReference ns = FirebaseHelper.getFirebaseFirestore()
                .collection("Usuarios")
                .document("Dados")
                .collection(FirebaseHelper.getUIDUsuario())
                .document("Informações pessoais");

        ns.set(usuario);

        DocumentReference dr = FirebaseHelper.getFirebaseFirestore()
                .collection("Usuarios")
                .document("Dados")
                .collection(FirebaseHelper.getUIDUsuario())
                .document("Informações boolean");

        Map<String, Object> booleano = new HashMap<>();
        booleano.put("Tem registros de cigarro?", false);

        dr.set(booleano);
    }

    public void voltarTelaLogin(View b){
        Intent voltarTelaLogin = new Intent(this, TelaLogin.class);
        startActivity(voltarTelaLogin);
    }
}