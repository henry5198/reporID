package com.app.reporid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import WebService.Asynchtask;
import WebService.WebService;

public class Login_Activity extends AppCompatActivity implements TextView.OnClickListener, Asynchtask {

    private TextView register;
    private TextInputEditText correo_txt;
    private TextInputEditText contrasena_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        register = (TextView) findViewById(R.id.txt_register);
        register.setPaintFlags(register.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        register.setText(getResources().getString(R.string.text_signup));
        register.setOnClickListener(this);

        correo_txt = (TextInputEditText)findViewById(R.id.text_correo_log);
        contrasena_txt = (TextInputEditText)findViewById(R.id.text_contrasena_log);

        getPermission(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public void getPermission(String permission){
        if (Build.VERSION.SDK_INT >= 23) {
            if (!(checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED))
                ActivityCompat.requestPermissions(this, new String[]{permission}, 1);
        }
    }

    @Override
    public void processFinish(String result) throws JSONException {
        if (result.equals("") || result.isEmpty()){
            Toast.makeText(this, "Error de Conexión", Toast.LENGTH_LONG);
            return;
        }
        JSONArray jsonArray = new JSONArray(result);
        JSONObject jsonObject = jsonArray.getJSONObject(0);

        String registro = jsonObject.getString("identificacion");
        if (registro.equals("null")){
            //No Encontrado
            Toast.makeText(this, "Credenciales Incorrectas", Toast.LENGTH_LONG);
        }else{
            //Respuesta de Registro de Usuario
            Person_Reporid perfil_usuario = new Person_Reporid(jsonObject);

            //Guardado de sesión
            SharedPreferences sharedPreferences = getSharedPreferences("ReporID", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("credenciales",jsonObject.toString());
            editor.commit();
            //Inicio a la aplicación
            Intent intent = new Intent(Login_Activity.this, Principal_Activity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Login_Activity.this, Register_Activity.class);
        startActivity(intent);

    }

    public void principal(View v) {
        String cn = correo_txt.getText().toString();
        String cnn = contrasena_txt.getText().toString();
        //Error
        if(cn.equals("") && cnn.equals("")){
            Toast.makeText(this, "Credenciales Incorrectas", Toast.LENGTH_LONG);
        }
        else{
            JSONObject jsn_datos = new JSONObject();
            JSONArray array_datos = new JSONArray();

            try {
                jsn_datos.put("correo", correo_txt.getText().toString());
                jsn_datos.put("contrasena", Encrypt(correo_txt.getText().toString(), contrasena_txt.getText().toString()));
                array_datos.put(jsn_datos);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Map<String, String> datos = new HashMap<String, String>();
            WebService ws= new WebService("https://reporid-city.herokuapp.com/method.php",datos,Login_Activity.this, Login_Activity.this);
            datos.put("data", String.valueOf(array_datos));
            datos.put("method", "sign_in");
            ws.setDatos(datos);
            ws.execute("");
        }
        /*Intent intent = new Intent(Login_Activity.this, Principal_Activity.class);
        startActivity(intent);*/
    }

    public void recuperar(View v) {
        //Intent intent = new Intent(Login_Activity.this, RecuperarActivity.class);
        //startActivity(intent);

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
