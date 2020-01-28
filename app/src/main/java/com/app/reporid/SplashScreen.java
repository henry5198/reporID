package com.app.reporid;

import android.app.Activity;
import android.app.Person;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashScreen extends Activity {

    public static String server = "https://intense-brook-70197.herokuapp.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("ReporID", Context.MODE_PRIVATE);
        String resultado = sharedPreferences.getString("credenciales","null");
        Intent intent;
        try {
            if (resultado.equals("null")){
                intent = new Intent(SplashScreen.this, Login_Activity.class);
                startActivity(intent);
                finish();
            }else{
                Person_Reporid person_reporid = new Person_Reporid(new JSONObject(resultado));
                intent = new Intent(SplashScreen.this, Principal_Activity.class);
                startActivity(intent);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
