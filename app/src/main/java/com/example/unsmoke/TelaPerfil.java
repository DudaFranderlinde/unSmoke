package com.example.unsmoke;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class TelaPerfil extends AppCompatActivity {

    TextView nomeUsu;
    EditText tipoFumo, marcaFumo;
    ImageView fotoUsu, deslogar;
    Button voltarTela;
    CheckBox editInfo;

    String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Uri uri_imagem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_perfil);
        getWindow().setStatusBarColor(Color.BLACK);
        getSupportActionBar().hide();

        nomeUsu = findViewById(R.id.nomeUsu);
        fotoUsu = findViewById(R.id.fotoUsu);
        voltarTela = findViewById(R.id.voltarTela);
        editInfo = findViewById(R.id.editInfo);
        tipoFumo = findViewById(R.id.tipoFumo);
        marcaFumo = findViewById(R.id.marcaFumo);
        deslogar = findViewById(R.id.deslogar);

        fotoUsu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pegarImagemGaleria();
            }
        });
    }

    public void voltarParaTelaInicial(View v){
        Intent voltarParaTelaInicial = new Intent(this, TelaInicial.class);
        startActivity(voltarParaTelaInicial);
    }

    private void pegarImagemGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Escolha uma imagem"), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){

            if (requestCode == 0){
                if (data != null){

                    uri_imagem = data.getData();

                    Glide.with(getBaseContext()).asBitmap().load(uri_imagem).listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {

                            Toast.makeText(getBaseContext(), "Falha ao selecionar imagem", Toast.LENGTH_LONG).show();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    }).into(fotoUsu);
                }else{
                    Toast.makeText(getBaseContext(), "Falha ao selecionar imagem", Toast.LENGTH_LONG).show();
                }
            }
        }
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
                    nomeUsu.setText(documentSnapshot.getString("Nome"));
                }
            }
        });
    }

    public void editarInformacoes(View e){
        if(editInfo.isChecked()){
            tipoFumo.setEnabled(true);
            marcaFumo.setEnabled(true);
        }else{
            tipoFumo.setEnabled(false);
            marcaFumo.setEnabled(false);
        }
    }

    public void deslogar(View d){
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent (TelaPerfil.this, TelaLogin.class);
        startActivity(i);
        finish();
    }

}