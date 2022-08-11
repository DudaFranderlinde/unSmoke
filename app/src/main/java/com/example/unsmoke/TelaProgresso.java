package com.example.unsmoke;

import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
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
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TelaProgresso extends AppCompatActivity {

    String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); //Formata
    LocalDate data = LocalDate.now(); // Pega a dataAtual

    TextView diasNoApp, qtdCigarros, txtVidaReduzida;

    String[] tiposFumo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_progresso);
        getWindow().setStatusBarColor(Color.BLACK);
        getSupportActionBar().hide();

        diasNoApp = findViewById(R.id.diasNoApp);
        qtdCigarros = findViewById(R.id.qtdCigarros);
        txtVidaReduzida = findViewById(R.id.txtVidaReduzida);

        qtdCigarrosTotal();
        vidaReduzida();
        calculaDinheiro();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference documentReference = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Informações pessoais");
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null){

                    String data1 = documentSnapshot.getString("Data de cadastro");
                    String data2 = sdf.format(data);

                    try{
                        Date primeiraDt = sdf.parse(data1);
                        Date segundaDt = sdf.parse(data2);
                        long diffEmMil = Math.abs(segundaDt.getTime() - primeiraDt.getTime());
                        long diff = TimeUnit.DAYS.convert(diffEmMil, TimeUnit.MILLISECONDS);

                        int data = (int) diff;
                        System.out.println(data);
                        diasNoApp.setText(data);
                    }catch (ParseException e){
                        e.printStackTrace();
                    }

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
                    LocalDate dataDeCadastro = LocalDate.parse(dataCadastro, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                    LocalDate dataAtual = LocalDate.now();


                    DocumentReference dR = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Dados de fumo diário").collection("Total de cigarros fumados").document("Total de cigarros fumados");
                    dR.get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()){

                            DocumentSnapshot documentSnapshoT = task1.getResult();

                            if (documentSnapshoT.exists()){
                                int totalCigarrosFumados = Math.toIntExact((Long) documentSnapshot.getData().get("Total de fumos"));

                                float precoCigarroUni = valorMacoCigarro / 20;
                                float mediaGasto = precoCigarroUni * cigarrosFumadosPorDia;
                                float valorGasto = valorMacoCigarro * totalCigarrosFumados;
                                int tempoApp = dataDeCadastro.getDayOfYear() - dataAtual.getDayOfYear();
                                float dinheiroEconomizado = (tempoApp * mediaGasto) - valorGasto;

                                Locale ptBr = new Locale("pt", "BR");
                                String valorString = NumberFormat.getCurrencyInstance(ptBr).format(dinheiroEconomizado);
                                System.out.println(valorString);


                                qtdCigarros.setText(valorString);
                            }

                        }
                    });
                }
            }
        });
    }



    public void vidaReduzida( ){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Dados de cigarros fumados");
        documentReference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                DocumentSnapshot ds = task.getResult();

                if(ds.exists()){

                    int totalCigarrosFumados = Math.toIntExact((Long) ds.getData().get("Total de cigarros fumados"));
                    int tempoTirado = 11;

                    int vidaReduzida = tempoTirado * totalCigarrosFumados;
                    txtVidaReduzida.setText(vidaReduzida);
                }
            }
        });
    }

    public void qtdCigarrosTotal(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Dados de cigarros fumados");
        documentReference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                DocumentSnapshot ds = task.getResult();

                if(ds.exists()){

                    int totalCigarrosFumados = Math.toIntExact((Long) ds.getData().get("Total de cigarros fumados"));
                    qtdCigarros.setText(totalCigarrosFumados);
                }
            }
        });


    }

    public void voltarParaTelaInicial(View v){
        Intent voltarParaTelaInicial = new Intent (this, TelaInicial.class);
        startActivity(voltarParaTelaInicial);
    }
}