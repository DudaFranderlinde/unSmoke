package com.example.unsmoke;

import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TelaProgresso extends AppCompatActivity {

    String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    TextView diasNoApp, qtdCigarros, txtVidaReduzida, dindin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_progresso);
        getWindow().setStatusBarColor(Color.BLACK);
        getSupportActionBar().hide();

        diasNoApp = findViewById(R.id.diasNoApp);
        qtdCigarros = findViewById(R.id.qtdCigarros);
        txtVidaReduzida = findViewById(R.id.txtVidaReduzida);
        dindin = findViewById(R.id.dindin);

        setarDias();
        qtdCigarrosTotal();
        vidaReduzida();
        calculaDinheiro();
    }

    public void setarDias(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference documentReference = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Dados de fumo diário");
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null){

                    String dataCadastro = documentSnapshot.getString("Data de cadastro inicial");
                    LocalDate dataDeCadastro = LocalDate.parse(dataCadastro, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                    LocalDate dataAtual = LocalDate.now();
                    System.out.println(dataAtual);
                    int tempoApp = dataAtual.getDayOfYear() - dataDeCadastro.getDayOfYear();
                    diasNoApp.setText("Dia " + tempoApp);
                }
            }
        });
    }

    public void calculaDinheiro( ){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference dr = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Dados de fumo diário");
        dr.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot documentSnapshot = task.getResult();

                if (documentSnapshot.exists()){
                    int valorMacoCigarro = Math.toIntExact((Long) documentSnapshot.getData().get("Preço pago por maço de cigarro"));
                    int cigarrosFumadosPorDia = Math.toIntExact((Long) documentSnapshot.getData().get("Cigarros por dia"));
                    String dataCadastro = documentSnapshot.getString("Data de cadastro inicial");
                    LocalDate dataDeCadastro = LocalDate.parse(dataCadastro, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                    LocalDate dataAtual = LocalDate.now();

                    DocumentReference dR = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Dados de fumo diário").collection("Total de cigarros fumados").document("Total de cigarros fumados");
                    dR.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                            if (documentSnapshot != null){
                                float totalCigarrosFumados = Math.toIntExact((Long) documentSnapshot.getData().get("Total de fumos"));

                                float precoCigarroUni = valorMacoCigarro / 20;
                                float mediaGasto = precoCigarroUni * cigarrosFumadosPorDia;
                                float valorGasto = precoCigarroUni * totalCigarrosFumados;
                                int tempoApp = dataDeCadastro.getDayOfYear() - dataAtual.getDayOfYear();
                                float dinheiroEconomizado = (tempoApp * mediaGasto) - valorGasto;

                                dindin.setText("R$ " + dinheiroEconomizado);
                            }
                        }
                    });
                }
            }
        });
    }

    public void vidaReduzida( ){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Dados de fumos").collection("Total").document("Total de fumos");
        documentReference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                DocumentSnapshot ds = task.getResult();

                if(ds.exists()){

                    int totalCigarrosFumados = Math.toIntExact((Long) ds.getData().get("Total de fumos"));

                    int min = 11 * totalCigarrosFumados;
                    int dia = 0;
                    int horas = 0;
                    String msg;

                    while (min > 59) {
                        horas++;
                    }

                    while (horas > 23) {
                        dia++;
                    }

                    msg = +dia + "d " + horas + "h " + min + "min";

                    txtVidaReduzida.setText(msg);
                }
            }
        });
    }

    public void qtdCigarrosTotal(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Dados de fumos").collection("Total").document("Total de fumos");
        documentReference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                DocumentSnapshot ds = task.getResult();

                if(ds.exists()){
                    int totalCigarrosFumados = Math.toIntExact((Long) ds.getData().get("Total de fumos"));
                    String vai = Integer.toString(totalCigarrosFumados);
                    qtdCigarros.setText(vai);
                }
            }
        });
    }

    public void voltarParaTelaInicial(View v){
        Intent voltarParaTelaInicial = new Intent (this, TelaInicial.class);
        startActivity(voltarParaTelaInicial);
    }
}