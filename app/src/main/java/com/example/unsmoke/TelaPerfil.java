package com.example.unsmoke;

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

    TextView nomeUsu;
    Button btnSair;

    private CircleImageView fotoUsu;
    private static final int REQUEST_GALERIA = 100;
    private String caminhoImagem;
    private Bitmap imagem;

    BottomSheetDialog dialog;
    Dialog dialogMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_perfil);
        getWindow().setStatusBarColor(Color.rgb(12, 76, 120));
        getSupportActionBar().hide();

        nomeUsu = findViewById(R.id.nomeUsu);
        fotoUsu = findViewById(R.id.fotoUsu);
        btnSair = findViewById(R.id.btnSair);

        fotoUsu.setOnClickListener(v -> pegarImagemGaleria());

        setarImagemPerfil();

        dialog = new BottomSheetDialog(this);
        mostrarCard();

        dialogMenu = new Dialog(this);

        btnSair.setOnClickListener(view -> dialog.show());
    }

    private void mostrarCard(){
        View view = getLayoutInflater().inflate(R.layout.modal_sair, null, false);

        Button naoSair = view.findViewById(R.id.naoSair);
        naoSair.setOnClickListener((View.OnClickListener) view12 -> {
            dialog.dismiss();
        });

        Button simSair = view.findViewById(R.id.simSair);
        simSair.setOnClickListener(view1 -> {
            FirebaseAuth.getInstance().signOut();
            finish();
            Intent i = new Intent (TelaPerfil.this, TelaLogin.class);
            startActivity(i);
        });

        dialog.setContentView(view);
    }

    public void verificaPermissaoGaleria(View view){
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                abrirGaleria();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(TelaPerfil.this, "Permissão negada.", Toast.LENGTH_SHORT).show();
            }
        };

        showDialogPermissao(permissionListener, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
    }

    private void abrirGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALERIA);
    }

    private void showDialogPermissao(PermissionListener listener, String[] permissoes){
        TedPermission.create()
                .setPermissionListener(listener)
                .setDeniedTitle("Permissões")
                .setDeniedMessage("Você negou a permissão para acessar a galeria do dispositivo, deseja permitir?")
                .setDeniedCloseButtonText("Não")
                .setGotoSettingButtonText("Sim")
                .setPermissions(permissoes)
                .check();
    }

    private void salvarImagemUsu(){
        StorageReference reference = FirebaseHelper.getFirebaseStorage().getReference()
                .child("imagens")
                .child("Fotos de perfil")
                .child(FirebaseHelper.getUIDUsuario())
                .child(FirebaseHelper.getUIDUsuario() + ".jpeg");

        UploadTask uploadTask = reference.putFile(Uri.parse(caminhoImagem));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_GALERIA){
                Uri localImagemSelecionada = data.getData();
                caminhoImagem = localImagemSelecionada.toString();

                if (Build.VERSION.SDK_INT > 28){
                    try {
                        imagem = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), localImagemSelecionada);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    ImageDecoder.Source source = ImageDecoder.createSource(getBaseContext().getContentResolver(), localImagemSelecionada);
                    try {
                        imagem = ImageDecoder.decodeBitmap(source);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                fotoUsu.setImageBitmap(imagem);
                salvarImagemUsu();
            }
        }
    }

    public void setarImagemPerfil(){

        FirebaseHelper.getFirebaseStorage().getReference()
                .child("imagens/Fotos de perfil/" + FirebaseHelper.getUIDUsuario() + "/" + FirebaseHelper.getUIDUsuario() + ".jpeg")
                .getDownloadUrl()
                .addOnSuccessListener((OnSuccessListener<Uri>) uri -> Picasso.get().load(uri).into(fotoUsu));
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
    protected void onStart() {
        super.onStart();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference documentReference = db.collection("Usuarios").document("Dados").collection(FirebaseHelper.getUIDUsuario()).document("Informações pessoais");
        documentReference.addSnapshotListener((documentSnapshot, error) -> {
            if (documentSnapshot != null){
                nomeUsu.setText(documentSnapshot.getString("Nome"));
            }
        });

//        setarInfoTelaPerfil();
    }

//    public void editarInformacoes(View e){
//        if(editInfo.isChecked()){
//            tipoFumo.setEnabled(true);
//            marcaFumo.setEnabled(true);
//        }else{
//            tipoFumo.setEnabled(false);
//            marcaFumo.setEnabled(false);
//        }
//    }

//    public void mandarInfoBD(View b){
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        String tipoDoFumo = tipoFumo.getText().toString();
//        String marcaDoFumo = marcaFumo.getText().toString();
//
//        Map<String, Object> infoFumo = new HashMap<>();
//        infoFumo.put("Tipos de fumos utilizados", tipoDoFumo);
//        infoFumo.put("Marcas de fumos utilizados", marcaDoFumo);
//
//        DocumentReference documentReference = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Informações pessoais").collection("Informações de fumo da tela de perfil").document("Informações");
//        documentReference.set(infoFumo);
//    }
//
//    public void setarInfoTelaPerfil(){
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        DocumentReference documentReference = db.collection("Usuarios").document("Dados").collection(usuarioID).document("Informações pessoais").collection("Informações de fumo da tela de perfil").document("Informações");
//        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
//                if (documentSnapshot != null){
//                    tipoFumo.setText(documentSnapshot.getString("Tipos de fumos utilizados"));
//                    marcaFumo.setText(documentSnapshot.getString("Marcas de fumos utilizados"));
//                }
//            }
//        });
//    }
}