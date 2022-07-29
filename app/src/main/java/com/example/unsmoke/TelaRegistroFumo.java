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
import java.util.Date;
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

        mandarBDCigarrosFumadosInicial();

    }

    public void mandarBDCigarrosFumadosInicial(){
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Dados de cigarros fumados");

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (documentSnapshot.exists()){

                        retornaInicioEaddFumo();

                    } else {

                        int registroCigarroInicial = 1;

                        Map<String, Object> totalCigarrosFumados = new HashMap<>();
                        totalCigarrosFumados.put("Total de cigarros fumados", registroCigarroInicial);

                        DocumentReference documentReference = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Dados de cigarros fumados");
                        documentReference.set(totalCigarrosFumados);

                    }

                }

            }

        });
    }

    public void retornaInicioEaddFumo (){
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Dados de cigarros fumados");

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (documentSnapshot.exists()){

                        int totalCigarFumAtual = Math.toIntExact((Long) documentSnapshot.getData().get("Total de cigarros fumados"));
                        int registraCigarro = ++totalCigarFumAtual;

                        Map<String, Object> totalCigarrosFumados = new HashMap<>();
                        totalCigarrosFumados.put("Total de cigarros fumados", registraCigarro);

                        documentReference.set(totalCigarrosFumados);

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