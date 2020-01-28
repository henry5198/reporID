package com.app.reporid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import WebService.Asynchtask;
import WebService.WebService;

public class Reporte_Activity extends AppCompatActivity implements Asynchtask {

    private String[] tiposIncidencias={"Robo a persona","Robo a vehiculo","Robo a comercio","Homicidio","Venta de Drogas","Accidente Vehicular","Pelea"};
    private Toolbar toolbar;
    private TextInputEditText txtFecha, txtDescripcion;
    private int dia, mes, anio;
    private Button btnEnviar, btnCamara;
    private Double latitud, longitud;
    private boolean camara=false, galeria=false;
    private Bitmap bmp;
    private Uri uri;
    private ImageView imgCamara;
    private MaterialBetterSpinner materialDesignSpinner;
    private StorageReference storageReference;
    private String fecha, tipoDelito, Url;
    private Switch swAnonimo;
    ProgressDialog dial;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reporte_activity);
        this.setTitle("Reporte de Incidencias Delictivas");

        toolbar = (Toolbar) findViewById(R.id.back_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        txtFecha=(TextInputEditText)findViewById(R.id.txtFecha);
        txtDescripcion=(TextInputEditText)findViewById(R.id.txtDescripcion);
        swAnonimo=(Switch)findViewById(R.id.swAnonimo);
        btnCamara = (Button)findViewById(R.id.btnCamara);
        imgCamara = (ImageView)findViewById(R.id.imgCamara);
        btnEnviar=(Button)findViewById(R.id.btnEnviar);

        dial=new ProgressDialog(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left);
        //Recibe coordenadas
        latitud=getIntent().getDoubleExtra("latitud",0.0);
        longitud=getIntent().getDoubleExtra("longitud",0.0);

        //Firebase
        storageReference= FirebaseStorage.getInstance().getReference("0850029802");
        checkCameraPermission();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, tiposIncidencias);
        materialDesignSpinner = (MaterialBetterSpinner)
                findViewById(R.id.android_material_design_spinner);
        materialDesignSpinner.setAdapter(arrayAdapter);

        /*materialDesignSpinner = (MaterialBetterSpinner)findViewById(R.id.android_material_design_spinner);
        //Lista de Incidencias
        Map<String, String> datos = new HashMap<String, String>();
        WebService ws= new WebService("https://reporid-city.herokuapp.com/method.php",datos, Reporte_Activity.this, Reporte_Activity.this);
        datos.put("method", "list_incidence");
        ws.setDatos(datos);
        ws.execute("");*/
    }

    @Override
    public void processFinish(String result) throws JSONException {
/*
            JSONArray jsonArray = new JSONArray(result);
            tiposIncidencias = new String[jsonArray.length()];
            for (int i=0;i<jsonArray.length();i++){
                JSONObject c = jsonArray.getJSONObject(i);

                tiposIncidencias[i]=c.getString("tipo_incidencia");
            }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, tiposIncidencias);
            arrayAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, tiposIncidencias);

            materialDesignSpinner.setAdapter(arrayAdapter);*/


    }

    private void checkCameraPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para la camarai!.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 225);
        } else {
            Log.i("Mensaje", "Tienes permiso para usar la camarai.");
        }
    }

    public void cam(View s){
        createSingleListDialog();
    }

    public void camara(){
        camara=true;
        galeria=false;
        Intent intent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,10);
    }

    public void galeria(){
        galeria=true;
        camara=false;
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==10&& resultCode== Activity.RESULT_OK){
            Bundle ext = data.getExtras();
            bmp = (Bitmap)ext.get("data");
            imgCamara.setImageBitmap(bmp);
        }
        if (requestCode==1&& resultCode== Activity.RESULT_OK){
            uri=data.getData();
            imgCamara.setImageURI(uri);
        }
    }

    public void subirImagen(int codigo, Uri uri){
        dial.setTitle("Subiendo");
        dial.setMessage("imagen");
        dial.setCancelable(false);
        dial.show();
        tipoDelito=materialDesignSpinner.getText().toString().replace(" ","-");
        Date date = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        fecha=hourdateFormat.format(date).toString().replace("/","").replace(":","").replace(" ","-");
        if(codigo==1){
            final StorageReference filepath= storageReference.child(tipoDelito).child(fecha);
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Toast.makeText(getApplicationContext(),"Subido correctamente",Toast.LENGTH_SHORT).show();

                    final StorageReference storageRef = filepath;
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String urs=uri.toString();
                            obtenerUrl(Reporte_Activity.this, urs);
                        }
                    });
                    dial.dismiss();
                    dialogo();
                }
            });
        }else{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] datas = baos.toByteArray();
            final StorageReference filepath=storageReference.child(tipoDelito).child(fecha);
            filepath.putBytes(datas).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Toast.makeText(getApplicationContext(),"Subido correctamente",Toast.LENGTH_SHORT).show();
                    final StorageReference storageRef = filepath;
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String urs=uri.toString();
                            obtenerUrl(Reporte_Activity.this, urs);
                        }
                    });
                    dialogo();
                }
            });
        }
    }

    public void obtenerUrl(Context context, String url){
        Url=url;
        Url="";
    }

    public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        dia=c.get(Calendar.DAY_OF_MONTH);
        mes=c.get(Calendar.MONTH);
        anio=c.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                txtFecha.setText(dayOfMonth+"/"+(month+1)+"/"+year);
            }
        },dia,mes,anio);
        datePickerDialog.show();
    }

    public void registrar(View n){
        if(materialDesignSpinner.getText().toString().equals("")||materialDesignSpinner.equals(null)
        ||(galeria==false&&camara==false)){
            Toast.makeText(getApplicationContext(),"Faltan algunos campos",Toast.LENGTH_SHORT).show();
        }else{
            if(camara&&!galeria){
                subirImagen(10,uri);
            }else {
                subirImagen(1,uri);
            }
        }
    }

    public void dialogo(){
        new AlertDialog.Builder(this)
                .setTitle("¡Enviado!")
                .setIcon(R.drawable.check)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                /*.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Mensaje","Se canceló la acción");
                    }
                })*/
                .show();
    }

    public void createSingleListDialog() {
        final CharSequence[] items = new CharSequence[3];

        items[0] = "Foto";
        items[1] = "Gelería";
        items[2] = "Cancelar";

        new AlertDialog.Builder(this)
                .setTitle("Seleccione")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){
                            case 0:
                                camara();
                                break;
                            case 1:
                                galeria();
                                break;
                            case 2:
                                Log.d("Mensaje","Se canceló la acción");
                                break;
                        }
                    }
                }).show();
    }
}
