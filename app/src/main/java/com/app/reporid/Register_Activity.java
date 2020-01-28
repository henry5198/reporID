package com.app.reporid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import WebService.WebService;
import WebService.Asynchtask;

public class Register_Activity extends AppCompatActivity implements Asynchtask {

    private Toolbar toolbar;
    private TextInputEditText txt_identificacion;
    private TextInputEditText txt_nombres;
    private TextInputEditText txt_apellidos;
    private TextInputEditText txt_correo;
    private TextInputEditText txt_contrasena;
    private TextInputEditText txt_rpcontrasena;
    private TextInputEditText txt_celular;

    public static StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        this.setTitle(R.string.act_register);

        txt_identificacion = (TextInputEditText) findViewById(R.id.txt_identificacion);
        txt_nombres = (TextInputEditText) findViewById(R.id.txt_nombres);
        txt_apellidos = (TextInputEditText) findViewById(R.id.txt_apellidos);
        txt_correo = (TextInputEditText) findViewById(R.id.txt_correo);
        txt_contrasena = (TextInputEditText) findViewById(R.id.txt_contrasena);
        txt_rpcontrasena = (TextInputEditText) findViewById(R.id.txt_rp_contrasena);
        txt_celular = (TextInputEditText) findViewById(R.id.txt_celular);

        toolbar = (Toolbar) findViewById(R.id.back_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left);
    }

    public void registrar(View view){
        if (
                !(txt_nombres.getText().toString().isEmpty() && txt_apellidos.getText().toString().isEmpty() &&
                        txt_correo.getText().toString().isEmpty() && txt_identificacion.getText().toString().isEmpty() &&
                        txt_celular.getText().toString().isEmpty() ) && (txt_contrasena.getText().toString().equals(txt_rpcontrasena.getText().toString()) )
        ){
            JSONObject jsn_datos = new JSONObject();
            JSONArray array_datos = new JSONArray();
            try {
                jsn_datos.put("identificacion", txt_identificacion.getText().toString());
                jsn_datos.put("tipo_identificacion", "cedula");
                jsn_datos.put("nombres", txt_nombres.getText().toString());
                jsn_datos.put("apellidos", txt_apellidos.getText().toString());
                jsn_datos.put("correo", txt_correo.getText().toString());
                jsn_datos.put("contrasena", Encrypt(txt_correo.getText().toString(),txt_contrasena.getText().toString()));
                jsn_datos.put("directorio_perfil", txt_identificacion.getText().toString());
                jsn_datos.put("celular", txt_celular.getText().toString());
                array_datos.put(jsn_datos);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Map<String, String> datos = new HashMap<String, String>();
            WebService ws= new WebService("https://reporid-city.herokuapp.com/method.php",datos,Register_Activity.this, Register_Activity.this);
            datos.put("data", String.valueOf(array_datos));
            datos.put("method", "register");
            ws.setDatos(datos);
            ws.execute("");

        }else{
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void processFinish(String result) throws JSONException {
        try{
            JSONArray jsonArray = new JSONArray(result);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            if (result.equals("")){
                Toast.makeText(this, "Error de Conexión", Toast.LENGTH_LONG).show();
                return;
            }
            if (jsonObject.length() == 1){
                //Respuesta de Validación de Usuario
                /*if ( Integer.valueOf(String.valueOf(jsonObject.get("listar_usuario"))) > 0){
                    txt_usuario.setError("Este usuario está en uso");
                }else{txt_usuario.setError(null);}*/

            }else{

                //El nombre del directorio por ahora es el número de identificación que no se va a repetir.
                //Eso se encuentra en esta linea de código
                String directorio = txt_identificacion.getText().toString();
                storageReference= FirebaseStorage.getInstance().getReference("0850029802");
                ///crea la carpeta con lo que contiene esa variable


                //Respuesta de Registro de Usuario
                Person_Reporid perfil_usuario = new Person_Reporid(jsonObject);

                //Guardado de sesión
                SharedPreferences sharedPreferences = getSharedPreferences("ReporID", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("credenciales",jsonObject.toString());
                editor.commit();

                //Inicio a la aplicación
                Intent intent = new Intent(Register_Activity.this, Principal_Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public String Encrypt(String identificacion, String contrasena) throws Exception {
        SecretKey key =  Generate_key(identificacion, contrasena);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] bytes  = cipher.doFinal(contrasena.getBytes());
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public SecretKey Generate_key(String identificacion, String contrasena) throws Exception{
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        contrasena = getKey(identificacion, contrasena);
        byte[] bytes = contrasena.getBytes("UTF-8");
        digest.update(bytes);
        byte[] key =  digest.digest();
        SecretKeySpec secretKeySpec =  new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }
    public String getKey(String usuario, String contrasena){
        return usuario+contrasena;
    }


}

