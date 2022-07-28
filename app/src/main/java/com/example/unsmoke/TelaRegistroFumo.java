package com.example.unsmoke;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

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
        dataFumo = findViewById(R.id.dataFumo);

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

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String tipo = spTiposFumo.getSelectedItem().toString();
        String duracao = duracaoFumo.getText().toString();
        String data = dataFumo.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> registroFumo = new HashMap<>();
        registroFumo.put("Tipo de fumo", tipo);
        registroFumo.put("Duração", duracao);
        registroFumo.put("Data", data);

        DocumentReference dr = db.collection("Dados").document(usuarioID).collection("Registro de fumo").document(data);
        dr.set(registroFumo);

        mandarBDCigarrosFumadosInicial();

        retornaInicioEaddFumo();
    }

    public void mandarBDCigarrosFumadosInicial(){
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Informações pessoais");
        int registroCigarroInicial = 0;

        Map<String, Object> totalCigarrosFumados = new HashMap<>();
        totalCigarrosFumados.put("Total de cigarros fumados", registroCigarroInicial);
    }

    public void retornaInicioEaddFumo (){
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Informações pessoais");

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null){
                     String totalCigarFumAtual = documentSnapshot.getString("Total de cigarros fumados");
                }
            }
        });



        registraCigarro = registraCigarro + 1;
        Intent voltarParaTelaInicial = new Intent(this, TelaInicial.class);
        startActivity(voltarParaTelaInicial);

    }
}