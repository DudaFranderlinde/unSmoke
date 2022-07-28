package com.example.unsmoke;

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
import com.google.firebase.firestore.FirebaseFirestore;

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

    public void mandarRegistroBD(){

        /*if(duracaoFumo.getText().length() < 2){
                duracaoFumo.setError("Você precisa inserir o seu nome para se cadastrar!");
            }
            else if (emailCadastro.getText().length() < 5){
                emailCadastro.setError("Você precisa inserir um email válido!");
            }
            else if (senhaCadastro.getText().length() < 8){
                senhaCadastro.setError("A sua deve ter pelo menos 8 caracteres!");
            }
            else{
                CadastrarUsuario(v);
            }
        }*/

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
    }

    public void retornaInicioEaddFumo (View v){
        registraCigarro += registraCigarro + 1;
        Intent voltarParaTelaInicial = new Intent(this, TelaInicial.class);
        startActivity(voltarParaTelaInicial);

    }
}