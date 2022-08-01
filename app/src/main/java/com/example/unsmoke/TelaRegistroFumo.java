package com.example.unsmoke;

import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class TelaRegistroFumo extends AppCompatActivity {

    private final String[] lsTiposFumo = new String []{"Tipo de fumo", "Cigarro industrializado", "Narguilé", "Cachimbo", "Charuto", "Cigarro de palha", "Cigarrilha", "Fumo de corda", "Folha de tabaco", "Cigarro de cannabis"};
    private Spinner spTiposFumo;

    EditText duracaoFumo, dataFumo;
    String usuarioID;

    int registraCigarro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_registro_fumo);
        getWindow().setStatusBarColor(Color.BLACK);
        getSupportActionBar().hide();

        spTiposFumo = (Spinner) findViewById(R.id.spTipoFumo);
        duracaoFumo = findViewById(R.id.duracaoFumo);

        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, lsTiposFumo);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spTiposFumo.setAdapter(adapter3);

        spTiposFumo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void mandarRegistroBD(View m){

        LocalDateTime dataHora = LocalDateTime.now();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); //Formata a data
        Date data = new Date(); // Pega a data atual
        String dataAtual = sdf.format(data);


        DateTimeFormatter formatterHora = DateTimeFormatter.ofPattern("HH:mm:ss");
        String horaFormatada = formatterHora.format(dataHora);

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String tipo = spTiposFumo.getSelectedItem().toString();
        String duracao = duracaoFumo.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> registroFumo = new HashMap<>();
        registroFumo.put("Tipo de fumo", tipo);
        registroFumo.put("Duração (minutos)", duracao);

        DocumentReference dr = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Registro de fumo").collection(dataAtual).document(horaFormatada);
        dr.set(registroFumo);

        mandarBDCigarrosFumadosInicialData();
        mandarBDCigarFumInicialMes();
        mandarBDCigarFumInicialTotal();
    }

    public void mandarBDCigarrosFumadosInicialData(){
        Intent voltarParaTelaInicial = new Intent(this, TelaInicial.class);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); //Formata a data
        Date data = new Date(); // Pega a data atual
        String dataAtual = sdf.format(data);

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db
                .collection("Usuarios")
                .document("Dados")
                .collection(usuarioID)
                .document("Dados de fumos")
                .collection("Diários")
                .document(dataAtual);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (documentSnapshot.exists()){

                        retornaInicioEaddFumoData();

                    } else {

                        int registroCigarroInicial = 1;

                        Map<String, Object> totalCigarrosFumadosData = new HashMap<>();
                        totalCigarrosFumadosData.put("Total de fumos", registroCigarroInicial);

                        DocumentReference documentReference = db
                                .collection("Usuarios")
                                .document("Dados")
                                .collection(usuarioID)
                                .document("Dados de fumos")
                                .collection("Diários")
                                .document(dataAtual);


                        documentReference.set(totalCigarrosFumadosData);

                        startActivity(voltarParaTelaInicial);

                    }

                }

            }

        });
    }

    public void retornaInicioEaddFumoData(){

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); //Formata a data
        Date data = new Date(); // Pega a data atual
        String dataAtual = sdf.format(data);

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Usuarios")
                .document("Dados")
                .collection(usuarioID)
                .document("Dados de fumos")
                .collection("Diários")
                .document(dataAtual);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (documentSnapshot.exists()){

                        int totalCigarFumAtual = Math.toIntExact((Long) documentSnapshot.getData().get("Total de fumos"));
                        int registraCigarro = ++totalCigarFumAtual;

                        Map<String, Object> totalCigarrosFumadosData = new HashMap<>();
                        totalCigarrosFumadosData.put("Total de fumos", registraCigarro);

                        documentReference.set(totalCigarrosFumadosData);

                    }
                }
            }
        });
        Intent voltarParaTelaInicial = new Intent(this, TelaInicial.class);
        startActivity(voltarParaTelaInicial);
    }

    public void mandarBDCigarFumInicialMes(){
        Intent voltarParaTelaInicial = new Intent(this, TelaInicial.class);

        Date data = new Date();
        GregorianCalendar dataCal = new GregorianCalendar();
        dataCal.setTime(data);
        String mes = String.valueOf(dataCal.get(Calendar.MONTH));

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db
                .collection("Usuarios")
                .document("Dados")
                .collection(usuarioID)
                .document("Dados de fumos")
                .collection("Mensais")
                .document(mes);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (documentSnapshot.exists()){

                        retornaInicioEaddFumoMes();

                    } else {

                        int registroCigarroInicial = 1;

                        Map<String, Object> totalCigarrosFumadosMes = new HashMap<>();
                        totalCigarrosFumadosMes.put("Total de fumos", registroCigarroInicial);

                        DocumentReference documentReference = db
                                .collection("Usuarios")
                                .document("Dados")
                                .collection(usuarioID)
                                .document("Dados de fumos")
                                .collection("Mensais")
                                .document(mes);

                        documentReference.set(totalCigarrosFumadosMes);

                        startActivity(voltarParaTelaInicial);

                    }

                }

            }

        });
    }

    public void retornaInicioEaddFumoMes(){
        Date data = new Date();
        GregorianCalendar dataCal = new GregorianCalendar();
        dataCal.setTime(data);
        String mes = String.valueOf(dataCal.get(Calendar.MONTH));

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db
                .collection("Usuarios")
                .document("Dados")
                .collection(usuarioID)
                .document("Dados de fumos")
                .collection("Mensais")
                .document(mes);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (documentSnapshot.exists()){

                        int totalCigarFumAtual = Math.toIntExact((Long) documentSnapshot.getData().get("Total de fumos"));
                        int registraCigarro = ++totalCigarFumAtual;

                        Map<String, Object> totalCigarrosFumadosMes = new HashMap<>();
                        totalCigarrosFumadosMes.put("Total de fumos", registraCigarro);

                        documentReference.set(totalCigarrosFumadosMes);
                    }
                }
            }
        });
        Intent voltarParaTelaInicial = new Intent(this, TelaInicial.class);
        startActivity(voltarParaTelaInicial);
    }

    public void mandarBDCigarFumInicialTotal(){
        Intent voltarParaTelaInicial = new Intent(this, TelaInicial.class);

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db
                .collection("Usuarios")
                .document("Dados")
                .collection(usuarioID)
                .document("Dados de fumos")
                .collection("Total")
                .document("Total de fumos");

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (documentSnapshot.exists()){

                        retornaInicioEaddFumoTotal();

                    } else {

                        int registroCigarroInicial = 1;

                        Map<String, Object> totalCigarrosFumadosTotal = new HashMap<>();
                        totalCigarrosFumadosTotal.put("Total de fumos", registroCigarroInicial);

                        DocumentReference documentReference = db
                                .collection("Usuarios")
                                .document("Dados")
                                .collection(usuarioID)
                                .document("Dados de fumos")
                                .collection("Total")
                                .document("Total de fumos");
                        documentReference.set(totalCigarrosFumadosTotal);

                        startActivity(voltarParaTelaInicial);

                    }

                }

            }

        });
    }

    public void retornaInicioEaddFumoTotal(){
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db
                .collection("Usuarios")
                .document("Dados")
                .collection(usuarioID)
                .document("Dados de fumos")
                .collection("Total")
                .document("Total de fumos");

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (documentSnapshot.exists()){

                        int totalCigarFumAtual = Math.toIntExact((Long) documentSnapshot.getData().get("Total de fumos"));
                        int registraCigarro = ++totalCigarFumAtual;

                        Map<String, Object> totalCigarrosFumadosTotal = new HashMap<>();
                        totalCigarrosFumadosTotal.put("Total de fumos", registraCigarro);

                        documentReference.set(totalCigarrosFumadosTotal);
                    }
                }
            }
        });
        Intent voltarParaTelaInicial = new Intent(this, TelaInicial.class);
        startActivity(voltarParaTelaInicial);
    }

    public void voltarParaTelaInicial(View w){
        Intent voltarParaTelaInicial = new Intent(this, TelaInicial.class);
        startActivity(voltarParaTelaInicial);
    }
}