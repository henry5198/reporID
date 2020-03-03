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
import android.content.ClipData;
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
import android.widget.VideoView;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import WebService.Asynchtask;
import WebService.WebService;

public class Reporte_Activity extends AppCompatActivity implements Asynchtask {

    private String[] tiposIncidencias={"Robo a persona","Robo a vehículo","Robo a casa","Robo a comercio","Actividad sospechosa","Homicidio","Vandalismo","Venta de drogas","Otros"};
    private Toolbar toolbar;
    private TextInputEditText txtFecha, txtDescripcion;
    private int dia, mes, anio, hora, min, ss;
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
    private final int CODE_ONE_IMAGE=1;
    private final int CODE_MULTI_IMAGE=2;
    private final int CODE_ONE_VIDEO=3;
    private final int CODE_MULTI_VIDEO=4;

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
        init();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left);
        //Recibe coordenadas
        latitud=getIntent().getDoubleExtra("latitud",0.0);
        longitud=getIntent().getDoubleExtra("longitud",0.0);

        String identificador = SplashScreen.person_reporid.getIdentificacion();
        //Firebase
        storageReference= FirebaseStorage.getInstance().getReference(identificador);
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

    public void init(){
        final Calendar c = Calendar.getInstance();
        dia=c.get(Calendar.DAY_OF_MONTH);
        mes=c.get(Calendar.MONTH);
        anio=c.get(Calendar.YEAR);
        hora = c.get(Calendar.HOUR);
        min = c.get(Calendar.MINUTE);
        ss = c.get(Calendar.SECOND);
        txtFecha.setText(dia+"/"+(mes+1)+"/"+anio+" "+hora+":"+min+":"+ss);
    }

    @Override
    public void processFinish(String result) throws JSONException {

        String pr = result;


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

        /* //Subir varias Imagenes a Firebase
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccione varias imagenes"),CODE_MULTI_IMAGE);*/

        /* //Subir varios videos a Firebase
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setType("video/mp4");
        startActivityForResult(intent, CODE_MULTI_VIDEO);*/
    }
    ArrayList<Uri> uris_galeria=null;
    ArrayList<Uri> uris_videos=null;
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


        /* //Subir varias Imagenes a Firebase
        if(requestCode==CODE_MULTI_IMAGE&&resultCode==Activity.RESULT_OK){
            ClipData clipData=data.getClipData();
            if(clipData!=null){
                imgCamara.setImageURI(clipData.getItemAt(0).getUri());
                uris_galeria=new ArrayList<>();
                urls=new ArrayList<>();
                //MAS IMGS
                for (int i=0;i<clipData.getItemCount();i++){
                    ClipData.Item item=clipData.getItemAt(i);
                    Uri uri=item.getUri();
                    uris_galeria.add(uri);
                    //Log.e("IMGS",uri.toString());
                }
            }
        }*/

        /* //Subir varios videos a Firebase
        if(requestCode==CODE_MULTI_VIDEO&&resultCode==RESULT_OK){
            ClipData clipData=data.getClipData();
            if(clipData!=null){
                vdGaleria=(VideoView)findViewById(R.id.vdGaleria);
                vdGaleria.setVideoURI(clipData.getItemAt(0).getUri());
                vdGaleria.start();

                //MAS VIDEOS
                uris_videos=new ArrayList<>();
                urls_videos=new ArrayList<>();
                for (int i=0;i<clipData.getItemCount();i++){
                    ClipData.Item item=clipData.getItemAt(i);
                    Uri uri=item.getUri();
                    uris_videos.add(uri);
                }
            }
        }*/
    }
    private VideoView vdGaleria;
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

    ArrayList<String> urls=null;
    ArrayList<String> urls_videos=null;
    public void obtenerUrl(Context context, String url){
        Url=url;
        //Subir varias Imagenes a Firebase
        //urls.add(url);

        //Subir varios videos a Firebase
        //urls_videos.add(url);

        Registrar_Server();
    }

    public void onClick(View v) {
        DatePickerDialog datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                txtFecha.setText(dayOfMonth+"/"+(month+1)+"/"+year+" "+hora+":"+min+":"+ss );
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
        /* //Subir Varias Imágenes a Firebase
        dial.setTitle("Subiendo");
        dial.setMessage("imagen");
        dial.setCancelable(false);
        dial.show();
        tipoDelito=materialDesignSpinner.getText().toString().replace(" ","-");

        for (int i=0;i<uris_galeria.size();i++){
            Date date = new Date();
            DateFormat hourdateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            fecha=hourdateFormat.format(date).toString().replace("/","").replace(":","").replace(" ","-")+(String.valueOf(i));
            final StorageReference filepath= storageReference.child(tipoDelito).child(fecha);
            filepath.putFile(uris_galeria.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
        }*/


        /* //Subir varios videos a Firebase
        dial.setTitle("Subiendo");
        dial.setMessage("imagen");
        dial.setCancelable(false);
        dial.show();
        tipoDelito=materialDesignSpinner.getText().toString().replace(" ","-");
        for (int i=0;i<uris_videos.size();i++){
            Date date = new Date();
            DateFormat hourdateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            fecha=hourdateFormat.format(date).toString().replace("/","").replace(":","").replace(" ","-")+String.valueOf(i);

            final StorageReference filepath= storageReference.child(tipoDelito).child(fecha);
            filepath.putFile(uris_videos.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
        }*/
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

    public void Registrar_Server(){
        /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String tiempo = simpleDateFormat.format(Calendar)*/

        JSONObject jsn_datos = new JSONObject();
        JSONArray array_datos = new JSONArray();
        try {
            jsn_datos.put("descripcion", txtDescripcion.getText().toString());
            jsn_datos.put("perfil_identificacion", SplashScreen.person_reporid.getIdentificacion());
            jsn_datos.put("fecha_reporte", txtFecha.getText().toString());
            jsn_datos.put("latitud_geo", String.valueOf(latitud));
            jsn_datos.put("longitud_geo", String.valueOf(longitud));
            jsn_datos.put("directorio",Url);
            if (swAnonimo.isChecked()){
                jsn_datos.put("tipo_denuncia", 1);
            }else{
                jsn_datos.put("tipo_denuncia", 0);
            }
            jsn_datos.put("directorio", Url);
            int indice = posicion(materialDesignSpinner.getText().toString());
            jsn_datos.put("incidencia_id_incidencia",indice);

            array_datos.put(jsn_datos);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, String> datos = new HashMap<String, String>();
        WebService ws= new WebService("https://reporid-city.herokuapp.com/method.php",datos,Reporte_Activity.this, Reporte_Activity.this);
        datos.put("data", String.valueOf(array_datos));
        datos.put("method", "register_report");
        ws.setDatos(datos);
        ws.execute("");
    }

    public int posicion(String seleccionado){
        int indice=0;
        for (int i=0;i<tiposIncidencias.length;i++){
            if(seleccionado.equals(tiposIncidencias[i])){
                indice=i+1;
                break;
            }
        }
        return indice;
    }
}

