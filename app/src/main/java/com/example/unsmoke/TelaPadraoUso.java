package com.example.unsmoke;

import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TelaPadraoUso extends AppCompatActivity {

    EditText cigarroDia, precoMaco, tempoCigarro;
    String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_padrao_uso);
        getWindow().setStatusBarColor(Color.BLACK);
        getSupportActionBar().hide();

        cigarroDia = findViewById(R.id.cigarroDia);
        precoMaco = findViewById(R.id.precoMaco);
        tempoCigarro = findViewById(R.id.tempoCigarro);
    }

    public void salvarDadosUsuario(View s){
        Intent irTelaInicial = new Intent(this, TelaInicial.class);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); //Formata a data
        Date data = new Date(); // Pega a data atual
        String dataAtual = sdf.format(data);

        int cigarroPorDia = parseInt(cigarroDia.getText().toString());
        int precoMacoCigarro = parseInt(precoMaco.getText().toString());
        int tempoFumarCigarro = parseInt(tempoCigarro.getText().toString());

        if (cigarroDia.getText().length() == 0){
            cigarroDia.setError("Insira um valor válido");
        }else if(precoMaco.getText().length() == 0){
            precoMaco.setError("Insira um valor válido");
        }else if(tempoCigarro.getText().length() == 0){
            tempoCigarro.setError("Insira um valor válido");
        }else{

            try {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Map<String, Object> usuarios = new HashMap<>();
                usuarios.put("Cigarros por dia", cigarroPorDia);
                usuarios.put("Preço pago por maço de cigarro", precoMacoCigarro);
                usuarios.put("Minutos levados para fumar 1 cigarro", tempoFumarCigarro);
                usuarios.put("Data de cadastro inicial", dataAtual);

                usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DocumentReference ns = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Dados de fumo diário");
                ns.set(usuarios);

            } catch (Exception e){
                Toast.makeText(this, "Não foi possível enviar para o banco de dados", Toast.LENGTH_LONG).show();
            }

            startActivity(irTelaInicial);

        }
    }

}