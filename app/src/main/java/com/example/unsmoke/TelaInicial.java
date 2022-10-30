package com.example.unsmoke;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class TelaInicial extends AppCompatActivity {

    TextView qntdDiaria, qntdMensal, qntdTotal, nomeUsuario;
    CircleImageView fotoUsu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_inicial);
        getWindow().setStatusBarColor(Color.rgb(12, 76, 120));
        getSupportActionBar().hide();

        iniciarComponentes();
        mostrarBottomSheet();
        popularQntdDiaria();
        popularQntdMensal();
        popularQntdTotal();
        setarImagemPerfil();
        setarNomeUsu();
    }

    private void iniciarComponentes(){
        qntdDiaria = findViewById(R.id.qntdDiaria);
        qntdMensal = findViewById(R.id.qntdMensal);
        qntdTotal = findViewById(R.id.qntdTotal);
        fotoUsu = findViewById(R.id.fotoUsuTelaInicial);
        nomeUsuario = findViewById(R.id.nomeUsuario);
    }

    public void mostrarBottomSheet(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference documentReference = db.collection("Usuarios").document("Dados").collection(FirebaseHelper.getUIDUsuario()).document("Dados de fumo diário");
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
                            .collection(FirebaseHelper.getUIDUsuario())
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

        DocumentReference dr = FirebaseHelper.getFirebaseFirestore()
                .collection("Usuarios")
                .document("Dados")
                .collection(FirebaseHelper.getUIDUsuario())
                .document("Informações boolean");

        dr.addSnapshotListener((snapshot, error) -> {
            assert snapshot != null;
            String temRegistro = snapshot.getBoolean("Tem registros de cigarro?").toString();
            if(temRegistro.equals("true")){
                Intent irTelaProgresso = new Intent(TelaInicial.this, TelaProgresso.class);
                startActivity(irTelaProgresso);
            } else {
                Intent irTelaSemRegistro = new Intent(TelaInicial.this, TelaSemRegistro.class);
                startActivity(irTelaSemRegistro);
            }
        });
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date data = new Date();
        String dataAtual = sdf.format(data);

        DocumentReference documentReference = FirebaseHelper.getFirebaseFirestore()
                .collection("Usuarios")
                .document("Dados")
                .collection(FirebaseHelper.getUIDUsuario())
                .document("Dados de fumos")
                .collection("Diários")
                .document(dataAtual);

        documentReference.addSnapshotListener((snapshot, error) -> {
            if (snapshot.exists()){
                int nCigarrosDiarios = Math.toIntExact((Long) snapshot.getData().get("Total de fumos"));
                String nCigarrosDiariosOF = Integer.toString(nCigarrosDiarios);
                qntdDiaria.setText(nCigarrosDiariosOF);
            } else {
                qntdDiaria.setText("0");
            }
        });
    }

    public void popularQntdMensal(){
        Date data = new Date();
        GregorianCalendar dataCal = new GregorianCalendar();
        dataCal.setTime(data);
        String mes = String.valueOf(dataCal.get(Calendar.MONTH));

        DocumentReference documentReference = FirebaseHelper.getFirebaseFirestore()
                .collection("Usuarios")
                .document("Dados")
                .collection(FirebaseHelper.getUIDUsuario())
                .document("Dados de fumos")
                .collection("Mensais")
                .document(mes);

        documentReference.addSnapshotListener((snapshot, error) -> {
            if (snapshot.exists()){
                int nCigarrosMensais = Math.toIntExact((Long) snapshot.getData().get("Total de fumos"));
                String nCigarrosDiariosOF = Integer.toString(nCigarrosMensais);
                qntdMensal.setText(nCigarrosDiariosOF);
            } else {
                qntdMensal.setText("0");
            }
        });
    }

    public void popularQntdTotal(){

        DocumentReference documentReference = FirebaseHelper.getFirebaseFirestore()
                .collection("Usuarios")
                .document("Dados")
                .collection(FirebaseHelper.getUIDUsuario())
                .document("Dados de fumos")
                .collection("Total")
                .document("Total de fumos");

        documentReference.addSnapshotListener((snapshot, error) -> {
            if (snapshot.exists()){
                int nCigarrosTotais = Math.toIntExact((Long) snapshot.getData().get("Total de fumos"));
                String nCigarrosDiariosOF = Integer.toString(nCigarrosTotais);
                qntdTotal.setText(nCigarrosDiariosOF);
            } else {
                qntdTotal.setText("0");
            }
        });
    }

    public void setarImagemPerfil(){
        FirebaseHelper.getFirebaseStorage().getReference()
                .child("imagens/Fotos de perfil/" + FirebaseHelper.getUIDUsuario() + "/" + FirebaseHelper.getUIDUsuario() + ".jpeg")
                .getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Picasso.get().load(uri).into(fotoUsu);
                });
    }

    private void setarNomeUsu(){
        DocumentReference documentReference = FirebaseHelper.getFirebaseFirestore()
                .collection("Usuarios")
                .document("Dados")
                .collection(FirebaseHelper.getUIDUsuario())
                .document("Informações pessoais");

        documentReference.addSnapshotListener((snapshot, error) -> {
            String nomeUsu = snapshot.getString("Nome");

            nomeUsuario.setText(nomeUsu);
        });
    }

}