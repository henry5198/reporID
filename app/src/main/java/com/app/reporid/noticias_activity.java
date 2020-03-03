package com.app.reporid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import WebService.Asynchtask;
import WebService.WebService;

public class noticias_activity extends AppCompatActivity implements Asynchtask {

    private List<VariablesReportesNews> items=new ArrayList<>();
    private RecyclerView recycle;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    public CardView card_ultimasNews;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticias);
        this.setTitle("Noticias");
        toolbar = (Toolbar) findViewById(R.id.back_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left);

        Map<String, String> datos = new HashMap<String, String>();
        WebService ws= new WebService("https://reporid-city.herokuapp.com/method.php",datos,
                noticias_activity.this, noticias_activity.this);
        datos.put("method", "list_reports_48hr");
        ws.setDatos(datos);
        ws.execute("");
    }

    @Override
    public void processFinish(String result) throws JSONException {
        Log.i("ProcessFinish",result);
        try {
            JSONArray jsonArray = new JSONArray(result);
            //JSONObject jsonObject = jsonArray.getJSONObject(0);

            for (int i = 0; i < jsonArray.length(); i++) {
                String personaDenuncia;
                String imgPersonaDenuncia;
                JSONObject c = jsonArray.getJSONObject(i);
                String fecha_reporte = c.getString("fecha_reporte");
                String descripcion = c.getString("descripcion");
                Integer tipo=Integer.parseInt(c.getString("tipo_denuncia"));
                if(tipo.equals(1)){
                    personaDenuncia = c.getString("nombres")+" "+c.getString("apellidos");
                    imgPersonaDenuncia="https://img2.freepng.es/20180426/bfe/kisspng-computer-icons-user-profile-5ae25c1f867d48.1548444315247841595509.jpg";
                }else {
                    personaDenuncia = "Anonimo";
                    imgPersonaDenuncia="https://www.emse.cl/wp-content/uploads/2019/01/anonimo.jpg";
                }
                JSONArray imagen=c.getJSONArray("multimedia");
                String FotoReporte="";
                for(int j=0;j<imagen.length();j++) {
                    JSONObject c1 = imagen.getJSONObject(j);
                    FotoReporte=c1.getString("directorio");
                }
                items.add(new VariablesReportesNews(FotoReporte,descripcion,personaDenuncia,imgPersonaDenuncia));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        recycle = (RecyclerView)findViewById(R.id.recycler_viewListReports);
        recycle.setHasFixedSize(true);

        lManager=new LinearLayoutManager(this);
        recycle.setLayoutManager(lManager);

        adapter= new ReporteAdapterNews(items);
        recycle.setAdapter(adapter);
    }
}
