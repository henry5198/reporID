package com.app.reporid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import WebService.Asynchtask;
import WebService.WebService;


public class Estadistica extends AppCompatActivity implements Asynchtask {

    private TextView txtFechaInicio, txtFechaFin;
    private int dia, mes, anio;
    private PieChart pieChart;
    private BarChart barChart;
    private Toolbar toolbar;
    private ArrayList<Integer> colors;
    private String fechaInicio="", fechaFin="";
    AlertDialog dialogo=null;
    private String tipoGrafico="";

    private ArrayList<String> tipo_incidencia;
    private ArrayList<Integer> cantidad_incidencia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadistica);
        this.setTitle("Estadisticas");
        toolbar = (Toolbar) findViewById(R.id.back_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left);

        init();
        graficoBarras();

        tipoGrafico="pastel";
        Map<String, String> datos = new HashMap<String, String>();
        WebService ws= new WebService("https://reporid-city.herokuapp.com/method.php",datos, Estadistica.this, Estadistica.this);
        datos.put("method", "list_statistics_30days");
        ws.setDatos(datos);
        ws.execute("");

        /*tipoGrafico="barras";
        Map<String, String> datos1 = new HashMap<String, String>();
        WebService ws1= new WebService("https://reporid-city.herokuapp.com/method.php",datos1, Estadistica.this, Estadistica.this);
        datos.put("method", "list_reports_month");
        ws1.setDatos(datos1);
        ws1.execute("");*/


    }

    public void init(){
        tipo_incidencia=new ArrayList<>();
        cantidad_incidencia=new ArrayList<>();

        colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
    }

    public void graficoBarras(){
        barChart = findViewById(R.id.BarChart);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);

        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setTextSize(7.5f);
        l.setYEntrySpace(0f);
        l.setXEntrySpace(0f);
        String[] parties1 = new String[] {
                "ENE","FEB","MAR","ABR",
                "MAY","JUN","JUL","AGS","SEP","OCT","NOV","DIC"
        };

        ArrayList<LegendEntry> entri=new ArrayList<>();
        for (int i=0;i<parties1.length;i++){
            LegendEntry entry=new LegendEntry();
            entry.label=parties1[i];
            entri.add(entry);
        }
        l.setCustom(entri);
        barChart.setMaxVisibleValueCount(60);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);

        //LLENAR DATA
        ArrayList<BarEntry> values = new ArrayList<>();
        while(cantidad_incidencia.size()<12){
            cantidad_incidencia.add(0);
        }

        int[] incidenciasXMes=new int[]{11, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 0; i < parties1.length; i++) {
            int val = (incidenciasXMes[i]);
            values.add(new BarEntry(i, val));
        }

        BarDataSet set1;
        set1 = new BarDataSet(values, "Año 2020");
        set1.setColors(colors);

        BarData dataBar = new BarData(set1);
        dataBar.setValueTextSize(10f);
        dataBar.setBarWidth(0.9f);
        barChart.setData(dataBar);
    }

    public void graficoPastel(){
        pieChart=(PieChart)findViewById(R.id.PieChart);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setCenterText("INCIDENCIAS\nDELICTIVAS");
        pieChart.setDrawCenterText(true);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.animateY(1400, Easing.EaseInOutQuad);
        pieChart.getLegend().setEnabled(false);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(11f);

        //LLENAR DATA
        ArrayList<PieEntry> entries = new ArrayList<>();
        /*String[] parties = new String[] {
                "Robo a persona","Robo a vehículo","Robo a casa","Robo a comercio",
                "Actv. sospechosa","Homicidio","Vandalismo","Venta de drogas","Otros","Otros"
        };*/
        //int[] incidencias=new int[]{7, 4, 3, 8, 12, 4, 6, 7, 3};
        int num=cantidad_incidencia.get(0);
        for (int i = 0; i < cantidad_incidencia.size() ; i++) {
            entries.add(new PieEntry((float) (cantidad_incidencia.get(i)),
                    tipo_incidencia.get(i).toString()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Incidencias");
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);
        pieChart.invalidate();

        tipoGrafico="barras";
        Map<String, String> datos = new HashMap<String, String>();
        WebService ws= new WebService("https://reporid-city.herokuapp.com/method.php",datos, Estadistica.this, Estadistica.this);
        datos.put("method", "list_reports_month");
        ws.setDatos(datos);
        ws.execute("");
    }

    @Override
    public void processFinish(String result) throws JSONException {
        //if(tipoGrafico.equals("pastel")){
            tipo_incidencia.clear();
            cantidad_incidencia.clear();
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                tipo_incidencia.add(c.getString("tipo_incidencia"));
                cantidad_incidencia.add(Integer.parseInt(c.getString("estadistica")));
            }
            graficoPastel();
        /*}
        else{
            cantidad_incidencia.clear();
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                cantidad_incidencia.add(Integer.parseInt(c.getString("count")));
            }
            graficoBarras();
        }*/

    }

    public void todos(View v){
        Map<String, String> datos = new HashMap<String, String>();
        WebService ws= new WebService("https://reporid-city.herokuapp.com/method.php",datos, Estadistica.this, Estadistica.this);
        datos.put("method", "list_statistics_all");
        ws.setDatos(datos);
        ws.execute("");
    }

    public void dias30(View v){
        Map<String, String> datos = new HashMap<String, String>();
        WebService ws= new WebService("https://reporid-city.herokuapp.com/method.php",datos, Estadistica.this, Estadistica.this);
        datos.put("method", "list_statistics_30days");
        ws.setDatos(datos);
        ws.execute("");
    }

    public void filtro(View v){
        dialogo=createLoginDialogo();
        dialogo.show();
    }

    public AlertDialog createLoginDialogo() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.activity_fecha, null); builder.setView(v);

        Button signin = (Button) v.findViewById(R.id.entrar_boton);
        txtFechaInicio = (TextView)v.findViewById(R.id.txtFechaInicio);
        txtFechaFin = (TextView)v.findViewById(R.id.txtFechaFin);

        final Calendar c = Calendar.getInstance();
        dia=c.get(Calendar.DAY_OF_MONTH);
        mes=c.get(Calendar.MONTH);
        anio=c.get(Calendar.YEAR);

        txtFechaInicio.setText(dia+"/"+(mes+1)+"/"+anio);
        txtFechaFin.setText(dia+"/"+(mes+1)+"/"+anio);

        txtFechaInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog=new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        txtFechaInicio.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },dia,mes,anio);
                datePickerDialog.show();
            }
        });

        txtFechaFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                dia=c.get(Calendar.DAY_OF_MONTH);
                mes=c.get(Calendar.MONTH);
                anio=c.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog=new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        txtFechaFin.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },dia,mes,anio);
                datePickerDialog.show();
            }
        });

        signin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s=txtFechaInicio.getText().toString();
                        String d=txtFechaFin.getText().toString();
                        obtenerFechas(v.getContext(), s, d);
                    }
                }
        );
        return builder.create();
    }

    public void obtenerFechas(Context context, String fInicio, String fFin){
        fechaInicio=fInicio;
        fechaFin=fFin;
        dialogo.dismiss();
        Map<String, String> datos = new HashMap<String, String>();
        WebService ws= new WebService("https://reporid-city.herokuapp.com/method.php",datos, Estadistica.this, Estadistica.this);
        datos.put("method", "list_statistics_filter");
        datos.put("fecha_inicio",fInicio);
        datos.put("fecha_fin",fFin);
        ws.setDatos(datos);
        ws.execute("");
    }
}

