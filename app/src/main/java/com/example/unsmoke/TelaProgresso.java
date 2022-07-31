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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TelaProgresso extends AppCompatActivity {

    String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); //Formata
    Date data = new Date(); // Pega a dataAtual

    TextView diasNoApp, qtdCigarros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_progresso);
        getWindow().setStatusBarColor(Color.BLACK);
        getSupportActionBar().hide();

        diasNoApp = findViewById(R.id.diasNoApp);
        qtdCigarros = findViewById(R.id.qtdCigarros);

        qtdCigarros.setText(" ");
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
                        diasNoApp.setText(data);
                    }catch (ParseException e){
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    public void voltarParaTelaInicial(View v){
        Intent voltarParaTelaInicial = new Intent (this, TelaInicial.class);
        startActivity(voltarParaTelaInicial);
    }

    public void configCigarroFumados(){
        // Falta: conectar variavel com BD e conseguir pegar o valor dele pra usar na comparação
         int cigarros = Integer.parseInt(qtdCigarros.getText().toString());

         if(cigarros <= 0 ){
             qtdCigarros.setText("0");
         }else{

         }

    }

    public void pegarVarBD(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference dr = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Dados de fumo diário");
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){

                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (documentSnapshot.exists()){

                        int valorMacoCigarro = Math.toIntExact((Long) documentSnapshot.getData().get("Preço pago por maço de cigarro"));
                        int cigarrosFumadosPorDia = Math.toIntExact((Long) documentSnapshot.getData().get("Cigarros por dia"));
                        String dataCadastro = documentSnapshot.getString("Data de cadastro inicial");

                        calculaDinheiro(valorMacoCigarro, cigarrosFumadosPorDia, dataCadastro);
                    }
                }
            }
        });

    }

    public void calculaDinheiro(int valorMacoCigarro, int cigarrosFumadosPorDia, String dataCadastro){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Dados de cigarros fumados");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    DocumentSnapshot ds = task.getResult();

                    if(ds.exists()){

                        int totalCigarrosFumados = Math.toIntExact((Long) ds.getData().get("Total de cigarros fumados"));

                        String tipoCigarro = null;//tive que inicar senão fica com erro
                        //Falta: conectar com o BD pra saber quais são os tipos, valor e qtd média de fumo diário para calcular uma média de dinheiro gasto

                        double valorMédio;
                        if(tipoCigarro.equals("Cigarro")){
                            valorMédio = 5.60;
                            double custoDia = valorMédio  /* * fumoDiario*/; // V
                            //double custoTotais = valorMédio * qtdCigarros;
                        }

                    }
                }
            }
        });


    }
}