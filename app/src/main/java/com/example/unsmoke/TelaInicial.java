package com.example.unsmoke;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class TelaInicial extends AppCompatActivity {

    TextView qntdDiaria, qntdMensal, qntdTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_inicial);
        getWindow().setStatusBarColor(Color.BLACK);
        getSupportActionBar().hide();

        qntdDiaria = findViewById(R.id.qntdDiaria);
        qntdMensal = findViewById(R.id.qntdMensal);
        qntdTotal = findViewById(R.id.qntdTotal);

        mostrarBottomSheet();
        popularQntdDiaria();
        popularQntdMensal();
        popularQntdTotal();

    }

    public void mostrarBottomSheet(){

        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Dados de fumo diário");
        documentReference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                DocumentSnapshot ds = task.getResult();

                if(ds.exists()){

                    int cigarrosPorDiaMedia = Math.toIntExact((Long) ds.getData().get("Cigarros por dia"));

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); //Formata a data
                    Date data = new Date(); // Pega a data atual
                    String dataAtual = sdf.format(data);


                    FirebaseFirestore dB = FirebaseFirestore.getInstance();
                    DocumentReference documentR = dB.collection("Usuarios")
                            .document("Dados")
                            .collection(usuarioID)
                            .document("Dados de fumos")
                            .collection("Diários")
                            .document(dataAtual);

                    documentR.get().addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){

                            DocumentSnapshot dS = task1.getResult();

                            if(dS.exists()){

                                int nCigarrosDiarios = Math.toIntExact((Long) dS.getData().get("Total de fumos"));

                                if (nCigarrosDiarios > cigarrosPorDiaMedia){
                                    ExampleBottomSheetDialog bottomSheetDialog = new ExampleBottomSheetDialog();
                                    bottomSheetDialog.show(getSupportFragmentManager(), "exampleBottomSheet");
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    public void irTelaProgresso(View i){
        Intent irTelaProgresso = new Intent(this, TelaProgresso.class);
        startActivity(irTelaProgresso);
    }

    public void irPerfil(View ip){
        Intent irTelaPerfil = new Intent(this, TelaPerfil.class);
        startActivity(irTelaPerfil);
    }

    public void irRegistrarFumo(View ir){
        Intent irTelaRegistroFumo = new Intent(this, TelaRegistroFumo.class);
        startActivity(irTelaRegistroFumo);
    }

    public void popularQntdDiaria(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); //Formata a data
        Date data = new Date(); // Pega a data atual
        String dataAtual = sdf.format(data);

        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Usuarios")
                .document("Dados")
                .collection(usuarioID)
                .document("Dados de fumos")
                .collection("Diários")
                .document(dataAtual);

        documentReference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                DocumentSnapshot ds = task.getResult();

                if(ds.exists()){

                    int nCigarrosDiarios = Math.toIntExact((Long) ds.getData().get("Total de fumos"));
                    String nCigarrosDiariosOF = Integer.toString(nCigarrosDiarios);
                    qntdDiaria.setText(nCigarrosDiariosOF);
                }
            }
        });
    }

    public void popularQntdMensal(){
        Date data = new Date();
        GregorianCalendar dataCal = new GregorianCalendar();
        dataCal.setTime(data);
        String mes = String.valueOf(dataCal.get(Calendar.MONTH));

        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Usuarios")
                .document("Dados")
                .collection(usuarioID)
                .document("Dados de fumos")
                .collection("Mensais")
                .document(mes);

        documentReference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                DocumentSnapshot ds = task.getResult();

                if(ds.exists()){

                    int nCigarrosDiarios = Math.toIntExact((Long) ds.getData().get("Total de fumos"));
                    String nCigarrosDiariosOF = Integer.toString(nCigarrosDiarios);
                    qntdMensal.setText(nCigarrosDiariosOF);
                }
            }
        });
    }

    public void popularQntdTotal(){
        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Usuarios")
                .document("Dados")
                .collection(usuarioID)
                .document("Dados de fumos")
                .collection("Total")
                .document("Total de fumos");

        documentReference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                DocumentSnapshot ds = task.getResult();

                if(ds.exists()){

                    int nCigarrosDiarios = Math.toIntExact((Long) ds.getData().get("Total de fumos"));
                    String nCigarrosDiariosOF = Integer.toString(nCigarrosDiarios);
                    qntdTotal.setText(nCigarrosDiariosOF);
                }
            }
        });
    }

}