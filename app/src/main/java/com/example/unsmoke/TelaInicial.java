package com.example.unsmoke;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class TelaInicial extends AppCompatActivity {

    CheckBox checkMeta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_inicial);
        getWindow().setStatusBarColor(Color.BLACK);
        getSupportActionBar().hide();

        checkMeta = findViewById(R.id.checkMeta);

        ExampleBottomSheetDialog bottomSheetDialog = new ExampleBottomSheetDialog();
        bottomSheetDialog.show(getSupportFragmentManager(), "exampleBottomSheet");
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


}