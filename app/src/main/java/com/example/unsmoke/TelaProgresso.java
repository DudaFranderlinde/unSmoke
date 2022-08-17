package com.example.unsmoke;

import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TelaProgresso extends AppCompatActivity {

    String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    TextView diasNoApp, qtdCigarros, txtVidaReduzida, dindin;

    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_progresso);
        getWindow().setStatusBarColor(Color.BLACK);
        getSupportActionBar().hide();

        diasNoApp = findViewById(R.id.diasNoApp);
        qtdCigarros = findViewById(R.id.qtdCigarros);
        txtVidaReduzida = findViewById(R.id.txtVidaReduzida);
        dindin = findViewById(R.id.dinheiro);

        setarDias();
        qtdCigarrosTotal();
        vidaReduzida();
        calculaDinheiro();
        bemVindoProgresso();


    }


    public void bemVindoProgresso(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference documentReference = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Informações pessoais");
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null){

                    String start = documentSnapshot.getString("BooleanModalProgresso");
                    String dataCadastro = documentSnapshot.getString("Data de cadastro");
                    String nome = documentSnapshot.getString("Nome");
                    String telefone = documentSnapshot.getString("Telefone");
                    String email = documentSnapshot.getString("Email");
                    String senha = documentSnapshot.getString("Senha");



                    if (start.equals("no")){
                        showDialog();
                        String prevStarted = "yes";

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


                }
            }
        });

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
                    int tempoApp = (dataAtual.getDayOfYear() - dataDeCadastro.getDayOfYear())+1;
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

                    DocumentReference dR = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Dados de fumo diário").collection("Total de cigarros fumados").document("Total de cigarros fumados");
                    dR.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                            if (documentSnapshot != null){
                                int totalCigarrosFumados = Math.toIntExact((Long) documentSnapshot.getData().get("Total de fumos"));
                                System.out.println(totalCigarrosFumados);
                                double precoCigarroUni =( (float) valorMacoCigarro / 20);
                                double valorGasto = precoCigarroUni * totalCigarrosFumados;

                               dindin.setText("R$:"+NumberFormat.getInstance(new Locale("pt", "BR")).format(valorGasto));
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
                        break;
                    }

                    while (horas > 23) {
                        dia++;
                        break;
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

    private void showDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_progresso);

        //LinearLayout Text = dialog.findViewById(R.id.aviso);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void voltarParaTelaInicial(View v){
        Intent voltarParaTelaInicial = new Intent (this, TelaInicial.class);
        startActivity(voltarParaTelaInicial);
    }
}