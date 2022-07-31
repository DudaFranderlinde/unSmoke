package com.example.unsmoke;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class TelaLogin extends AppCompatActivity {

    EditText emailLogin, senhaLogin;
    CheckBox mostrarSenhaLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_login);
        getWindow().setStatusBarColor(Color.BLACK);
        getSupportActionBar().hide();

        mostrarSenhaLogin = findViewById(R.id.mostrarSenhaLogin);
        emailLogin = findViewById(R.id.emailLogin);
        senhaLogin = findViewById(R.id.senhaLogin);

        //Intent irDireto = new Intent(this, TelaPerfil.class);
        //startActivity(irDireto);
    }

    public void AutenticarUsuario(View a){
        Intent irTelaInicial = new Intent(this, TelaInicial.class);

        String email = emailLogin.getText().toString();
        String senha = senhaLogin.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    startActivity(irTelaInicial);
                }else {
                    String erro;

                    try {
                        throw task.getException();
                    }catch (Exception e){
                        erro = "Erro ao logar o usuário";
                    }
                    Snackbar snackbar = Snackbar.make(a,erro,Snackbar.LENGTH_LONG);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }
            }
        });
    }

    public void mostrarSenha(View m) {
        if (mostrarSenhaLogin.isChecked()){
            senhaLogin.setInputType(InputType.TYPE_CLASS_TEXT);
        }else{
            senhaLogin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    public void irTelaCadastro(View i){
        Intent go = new Intent(this, TelaCadastro.class);
        startActivity(go);
    }
}