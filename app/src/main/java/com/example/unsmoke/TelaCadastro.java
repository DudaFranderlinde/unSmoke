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
    String usuarioID;

    DateTimeFormatter formata = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    LocalDate data = LocalDate.now();
    String dataCadastro = data.format(formata);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_cadastro);
        getWindow().setStatusBarColor(Color.BLACK);
        getSupportActionBar().hide();

        nomeCadastro = findViewById(R.id.nomeCadastro);
        sobrenomeCadastro = findViewById(R.id.sobrenomeCadastro);
        telefoneCadastro = findViewById(R.id.telefoneCadastro);
        emailCadastro = findViewById(R.id.emailCadastro);
        senhaCadastro = findViewById(R.id.senhaCadastro);
        repeteSenha = findViewById(R.id.repeteSenha);
        continuar = findViewById(R.id.btnContinuar);

        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(nomeCadastro.getText().length() == 0){
                    nomeCadastro.setError("Você precisa inserir o seu nome para se cadastrar!");
                }
                else if(sobrenomeCadastro.getText().length() == 0){
                    sobrenomeCadastro.setError("Você precisa inserir o seu sobrenome para continuar!");
                }
                else if (emailCadastro.getText().length() < 5){
                    emailCadastro.setError("Você precisa inserir um email válido!");
                }
                else if (senhaCadastro.getText().length() < 8){
                    senhaCadastro.setError("A sua deve ter pelo menos 8 caracteres!");
                }
                else{
                    CadastrarUsuario(v);
                }
            }
        });
    }

    public void CadastrarUsuario(View v){

        String email = emailCadastro.getText().toString();
        String senha = senhaCadastro.getText().toString();

        Intent irTelaContaCriada = new Intent(this, TelaPadraoUso.class);

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
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
            }
        });
    }

    private void SalvarDadosUsuario(){

        String nome = nomeCadastro.getText().toString();
        String telefone = telefoneCadastro.getText().toString();
        String email = emailCadastro.getText().toString();
        String senha = senhaCadastro.getText().toString();
        String prevStarted = "no";

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> usuarios = new HashMap<>();
        usuarios.put("Data de cadastro", dataCadastro);
        usuarios.put("Nome", nome);
        usuarios.put("Telefone", telefone);
        usuarios.put("Email", email);
        usuarios.put("Senha", senha);
        usuarios.put("BooleanModalProgresso", prevStarted);

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference ns = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Informações pessoais");
        ns.set(usuarios);
    }

//    public void mostrarSenha(View m) {
//        if (mostrarSenhaCadastro.isChecked()){
//            senhaCadastro.setInputType(InputType.TYPE_CLASS_TEXT);
//        }else{
//            senhaCadastro.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//        }
//    }
}