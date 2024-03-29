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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;

public class TelaLogin extends AppCompatActivity {

    EditText emailLogin, senhaLogin;
    CheckBox mostrarSenhaLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_login);
        getWindow().setStatusBarColor(Color.rgb(12, 76, 120));
        getSupportActionBar().hide();

        iniciarComponentes();

//        Intent irDireto = new Intent(this, TelaPadraoUso.class);
//        startActivity(irDireto);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseHelper.getFirebaseAuth().getCurrentUser() != null){
            Intent ir = new Intent(this, TelaInicial.class);
            startActivity(ir);
        }
    }

    public void iniciarComponentes(){
        mostrarSenhaLogin = findViewById(R.id.mostrarSenhaLogin);
        emailLogin = findViewById(R.id.emailLogin);
        senhaLogin = findViewById(R.id.senhaLogin);
    }

    public void AutenticarUsuario(View a){
        Intent irTelaInicial = new Intent(this, TelaInicial.class);

        String email = emailLogin.getText().toString();
        String senha = senhaLogin.getText().toString();

        FirebaseHelper.getFirebaseAuth().signInWithEmailAndPassword(email, senha).addOnCompleteListener(task -> {
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
        });
    }

    public void mostrarSenha(View m) {
        if (mostrarSenhaLogin.isChecked()){
            senhaLogin.setInputType(InputType.TYPE_CLASS_TEXT);
            mostrarSenhaLogin.setButtonDrawable(R.drawable.ic_outline_lock_open_24);
        }else{
            senhaLogin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mostrarSenhaLogin.setButtonDrawable(R.drawable.ic_outline_lock_24_preto);
        }
    }

    public void irTelaCadastro(View i){
        Intent go = new Intent(this, TelaCadastro.class);
        startActivity(go);
    }

    public void recuperarSenha(View r){

        String email = emailLogin.getText().toString();

        if (email.isEmpty()){
            emailLogin.setError("Você precisa inserir o seu email para recuperar a sua senha");
        }else{
            FirebaseHelper.getFirebaseAuth().sendPasswordResetEmail(email).addOnSuccessListener(unused ->
                    Toast.makeText(getBaseContext(), "Te enviamos um email com um link para redefinição da senha", Toast.LENGTH_LONG).show()
            ).addOnFailureListener(e ->
                    Toast.makeText(getBaseContext(), "Erro ao enviar o email", Toast.LENGTH_LONG).show()
            );
        }
    }

}