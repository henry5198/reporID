package com.app.reporid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import WebService.Asynchtask;
import WebService.WebService;

public class Principal_Activity extends AppCompatActivity implements Asynchtask, OnMapReadyCallback, BottomNavigationView.OnNavigationItemSelectedListener{

    private List<VariablesReportes> items=new ArrayList<>();
    public GoogleMap mapa;
    public static String server = "";
    private static final String TAG="MainActivity";
    private RecyclerView recycle;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    private FloatingActionButton btnReporte;
    private FloatingActionButton btnCancela;
    private FloatingActionButton btnAcepta;
    private Boolean seleccionado=false;
    private Double latitud, longitud;
    private BottomNavigationView bottomNavigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal_activity);

        btnReporte=(FloatingActionButton)findViewById(R.id.btnReporte);
        btnCancela=(FloatingActionButton)findViewById(R.id.btnCancela);
        btnAcepta=(FloatingActionButton)findViewById(R.id.btnAcepta);
        btnAcepta.hide(); btnCancela.hide(); btnReporte.show();
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottonNavView_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        Map<String, String> datos = new HashMap<String, String>();
        WebService ws= new WebService("https://reporid-city.herokuapp.com/method.php",datos, Principal_Activity.this, Principal_Activity.this);
        //datos.put("method", "list_new_report");
        datos.put("method", "list_reports_48hr");
        ws.setDatos(datos);
        ws.execute("");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.recreate();
    }

    @Override
    public void processFinish(String result) throws JSONException {
            items.clear();
            try {
                JSONArray jsonArray = new JSONArray(result);
                //JSONObject jsonObject = jsonArray.getJSONObject(0);

                for (int i = 0; i < jsonArray.length(); i++) {
                    String insidencia="";
                    JSONObject c = jsonArray.getJSONObject(i);
                    String fecha_reporte = c.getString("fecha_reporte");
                    String descripcion = c.getString("descripcion");
                    Double latitud = c.getDouble("latitud_geo");
                    Double longitud = c.getDouble("longitud_geo");
                    JSONArray tipo_incidencia = c.getJSONArray("incidencias");
                    for(int j=0;j<tipo_incidencia.length();j++){
                        JSONObject c1 = tipo_incidencia.getJSONObject(j);
                        if(j==tipo_incidencia.length()-1)
                            insidencia= insidencia+c1.getString("tipo_incidencia");
                        else
                            insidencia= insidencia+c1.getString("tipo_incidencia")+"\n";

                    }
                    JSONArray imagen = c.getJSONArray("multimedia");
                    String img = "";
                    for (int j = 0; j < imagen.length(); j++){
                        JSONObject c1 = imagen.getJSONObject(j);
                        img = c1.getString("directorio");
                    }
                    items.add(new VariablesReportes(latitud,longitud,img,insidencia,descripcion,fecha_reporte));
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }

            mapa.clear();
            for(int i = 0; i < items.size(); i++){
                VariablesReportes vp = items.get(i);
                LatLng punto = new LatLng(vp.getLatitud(), vp.getLongitud());
                mapa.addMarker(new MarkerOptions().position(punto).title(vp.getTipoReporte()));
            }

            recycle = (RecyclerView)findViewById(R.id.recycler_viewListReports);
            recycle.setHasFixedSize(true);

            lManager=new LinearLayoutManager(this);
            recycle.setLayoutManager(lManager);

            adapter= new ReporteAdapter(items);
            recycle.setAdapter(adapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        LatLng latLng= new LatLng(-1.012712,-79.469013);
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mapa.getUiSettings().setZoomControlsEnabled(true);
        CameraUpdate camUpd1 = CameraUpdateFactory.newLatLngZoom(new LatLng(-1.012712, -79.469013), 16);
        mapa.moveCamera(camUpd1);
    }

    public void reporte(View v) {
        Toast.makeText(this,"Seleccione la Ubicación", Toast.LENGTH_LONG).show();
        btnReporte.hide();
        btnCancela.show();
        btnAcepta.show();

        seleccionado=false;
        mapa.clear();
        mapa.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Projection projection=mapa.getProjection();
                Point point=projection.toScreenLocation(latLng);
                LatLng punto = new LatLng(latLng.latitude, latLng.longitude);
                mapa.clear();
                latitud=punto.latitude;
                longitud=punto.longitude;
                mapa.addMarker(new MarkerOptions().position(punto).title("Nuevo Punto").draggable(true));

                seleccionado=true;
            }
        });

    }

    public void estadistica(View v){

        Intent intent = new Intent(Principal_Activity.this, Estadistica.class);
        startActivity(intent);
    }

    public void acepta(View v){

        if(seleccionado){
            Intent intent = new Intent(Principal_Activity.this, Reporte_Activity.class);
            intent.putExtra("latitud",latitud);
            intent.putExtra("longitud",longitud);
            seleccionado=false;

            startActivity(intent);
        }else{
            Toast.makeText(this,"No ha seleccionado la ubicación", Toast.LENGTH_LONG).show();
        }


    }
    public void cancela(View v){
        btnReporte.show();
        btnCancela.hide();
        btnAcepta.hide();
        mapa.clear();
        seleccionado=false;

        Map<String, String> datos = new HashMap<String, String>();
        WebService ws= new WebService("https://reporid-city.herokuapp.com/method.php",datos, Principal_Activity.this, Principal_Activity.this);
        //datos.put("method", "list_new_report");
        datos.put("method", "list_reports_48hr");
        ws.setDatos(datos);
        ws.execute("");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.id_filtro:
                AlertDialog.Builder alertBuilder=new AlertDialog.Builder(Principal_Activity.this);
                View view=getLayoutInflater().inflate(R.layout.activity_filtro,null);

                Button btnAceptar=(Button)view.findViewById(R.id.btnAceptar);
                MaterialButton btnUlt48horas=(MaterialButton)view.findViewById(R.id.btnUlt48Horas);
                MaterialButton btnUlt7Dias=(MaterialButton)view.findViewById(R.id.btnUlt7Dias);
                MaterialButton btnUlt15Dias=(MaterialButton)view.findViewById(R.id.btnUlt15Dias);
                MaterialButton btnUlt30Dias=(MaterialButton)view.findViewById(R.id.btnUlt30Dias);
                MaterialButton btnMasDe30Dias=(MaterialButton)view.findViewById(R.id.btnMasDe30Dias);
                alertBuilder.setView(view);
                final AlertDialog dialog=alertBuilder.create();
                dialog.show();
                btnUlt48horas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mapa.clear();
                        Map<String, String> datos = new HashMap<String, String>();
                        WebService ws= new WebService("https://reporid-city.herokuapp.com/method.php",datos, Principal_Activity.this, Principal_Activity.this);
                        datos.put("method", "list_reports_48hr");
                        ws.setDatos(datos);
                        ws.execute("");
                        dialog.dismiss();
                    }
                });
                btnUlt7Dias.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mapa.clear();
                        Map<String, String> datos = new HashMap<String, String>();
                        WebService ws= new WebService("https://reporid-city.herokuapp.com/method.php",datos, Principal_Activity.this, Principal_Activity.this);
                        datos.put("method", "list_reports_7days");
                        ws.setDatos(datos);
                        ws.execute("");
                        dialog.dismiss();
                    }
                });
                btnUlt15Dias.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mapa.clear();
                        Map<String, String> datos = new HashMap<String, String>();
                        WebService ws= new WebService("https://reporid-city.herokuapp.com/method.php",datos, Principal_Activity.this, Principal_Activity.this);
                        datos.put("method", "list_reports_15days");
                        ws.setDatos(datos);
                        ws.execute("");
                        dialog.dismiss();
                    }
                });
                btnUlt30Dias.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mapa.clear();
                        Map<String, String> datos = new HashMap<String, String>();
                        WebService ws= new WebService("https://reporid-city.herokuapp.com/method.php",datos, Principal_Activity.this, Principal_Activity.this);
                        datos.put("method", "list_reports_30days");
                        ws.setDatos(datos);
                        ws.execute("");
                        dialog.dismiss();
                    }
                });
                btnMasDe30Dias.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mapa.clear();
                        Map<String, String> datos = new HashMap<String, String>();
                        WebService ws= new WebService("https://reporid-city.herokuapp.com/method.php",datos, Principal_Activity.this, Principal_Activity.this);
                        datos.put("method", "list_reports_30plus");
                        ws.setDatos(datos);
                        ws.execute("");
                        dialog.dismiss();
                    }
                });
                btnAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.ic_arrow:
                Intent intent1 = new Intent(Principal_Activity.this, noticias_activity.class);
                startActivity(intent1);
                break;
            case R.id.id_perfil:
                Intent intent = new Intent(Principal_Activity.this, Perfil_Activity.class);
                startActivity(intent);
                break;
        }
        return true;
    }


}
