package com.app.reporid;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//import com.squareup.picasso.Picasso;

public class ReporteAdapter extends RecyclerView.Adapter<ReporteAdapter.ReportesViewHolder> {
    private List<VariablesReportes> items;

    public static class ReportesViewHolder extends RecyclerView.ViewHolder{
        public CardView contactosCardView;
        public ImageView imagenNew;
        public TextView tipoDeReporte;
        public TextView fechaReporte;
        public TextView descripcionReporte;
        public ReportesViewHolder(View v){
            super(v);
            contactosCardView=(CardView)v.findViewById(R.id.card_ultimasIncidencias);
            imagenNew = (ImageView)v.findViewById(R.id.ImagenNew);
            tipoDeReporte=(TextView)v.findViewById(R.id.txtTipoDeReporte);
            fechaReporte=(TextView)v.findViewById(R.id.txtFechareporte);
            descripcionReporte=(TextView)v.findViewById(R.id.txtDescripcion);

        }

    }
    @Override
    public ReportesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recent_format,viewGroup,false);
        return new ReportesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportesViewHolder viewHolder, final int i) {

        viewHolder.tipoDeReporte.setText("Tipo: "+items.get(i).getTipoReporte());
        viewHolder.fechaReporte.setText("Fecha: "+items.get(i).getFechaReporte());
        viewHolder.descripcionReporte.setText("Descrip: "+items.get(i).getDescripcionReporte());

    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }
    public ReporteAdapter(List<VariablesReportes>items){
        this.items=items;
    }

    public List<VariablesReportes> getItems(){
        return this.items;
    }

}
