package com.example.unsmoke;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class TelaPerfil extends AppCompatActivity {

    TextView nomeUsu, tiposCigarroTextView;
    Button btnSair;
    BottomSheetDialog dialog;
    Dialog dialogMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_perfil);
        getWindow().setStatusBarColor(Color.rgb(12, 76, 120));
        getSupportActionBar().hide();

        nomeUsu = findViewById(R.id.nomeUsu);
        btnSair = findViewById(R.id.btnSair);
        tiposCigarroTextView = findViewById(R.id.tiposCigarro);

        dialog = new BottomSheetDialog(this);
        dialogMenu = new Dialog(this);
        btnSair.setOnClickListener(view -> dialog.show());
        mostrarCard();

    }

    @Override
    protected void onStart() {
        super.onStart();

        DocumentReference documentReference = FirebaseHelper.getFirebaseFirestore()
                .collection("Usuarios")
                .document("Dados")
                .collection(FirebaseHelper.getUIDUsuario())
                .document("Informações pessoais");

        documentReference.addSnapshotListener((documentSnapshot, error) -> {
            if (documentSnapshot != null){
                nomeUsu.setText(documentSnapshot.getString("Nome"));
            }
        });

        DatabaseReference reference1 = FirebaseHelper.getFirebaseDatabase().getReference()
                .child("Usuarios")
                .child(FirebaseHelper.getUIDUsuario())
                .child("Tipos de fumo");

        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tiposCigarroTextView.setText(snapshot.getValue().toString()
                        .replace("[", "• ")
                        .replace("]", "")
                        .replace(",", "\n•"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void mostrarCard() {
        View view = getLayoutInflater().inflate(R.layout.modal_sair, null, false);

        Button naoSair = view.findViewById(R.id.naoSair);
        naoSair.setOnClickListener(view12 -> {
            dialog.dismiss();
        });

        Button simSair = view.findViewById(R.id.simSair);
        simSair.setOnClickListener(view1 -> {
            FirebaseAuth.getInstance().signOut();
            finish();
            Intent i = new Intent(TelaPerfil.this, TelaLogin.class);
            startActivity(i);
        });

        dialog.setContentView(view);
    }

    public void voltarParaTelaInicial(View v){
        Intent voltarParaTelaInicial = new Intent(this, TelaInicial.class);
        startActivity(voltarParaTelaInicial);
    }
}