package com.app.reporid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import static com.app.reporid.SplashScreen.person_reporid;

public class Perfil_Activity extends AppCompatActivity {

    private MaterialButton btn_cerrarsesion;
    private Toolbar toolbar;
    private TextInputEditText txt_nombres;
    private TextInputEditText txt_apellidos;
    private TextInputEditText txt_correo;
    private TextInputEditText txt_usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        btn_cerrarsesion = (MaterialButton)findViewById(R.id.cerrar_sesion);

        toolbar = (Toolbar) findViewById(R.id.back_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left);

        txt_nombres = (TextInputEditText) findViewById(R.id.perfil_nombre_txt);
        txt_apellidos = (TextInputEditText) findViewById(R.id.perfil_apellido_txt);
        txt_correo = (TextInputEditText) findViewById(R.id.perfil_correo_txt);
        txt_usuario = (TextInputEditText) findViewById(R.id.perfil_usuario_txt);

        txt_usuario.setText(person_reporid.getIdentificacion());
        txt_nombres.setText(person_reporid.getNombres());
        txt_apellidos.setText(person_reporid.getApellidos());
        txt_correo.setText(person_reporid.getCorreo_electronico());
    }

    public void OnClick_CerrarSesion(View view){

        SharedPreferences sharedPreferences = getSharedPreferences("ReporID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

        Intent intent = new Intent(Perfil_Activity.this, Login_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
