package com.app.reporid;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ReporteAdapterNews extends RecyclerView.Adapter<ReporteAdapterNews.ReportesViewHolder> {
    private List<VariablesReportesNews> items;

    public static class ReportesViewHolder extends RecyclerView.ViewHolder{
        public CardView card_news;
        public ImageView card_news_imgs;
        public TextView txtPersonareporta;
        public ImageView img_personaReporte;
        public TextView descripcionReporte;
        public ReportesViewHolder(View v){
            super(v);
            card_news=(CardView)v.findViewById(R.id.card_new);
            card_news_imgs = (ImageView)v.findViewById(R.id.card_news_imgs);
            txtPersonareporta=(TextView)v.findViewById(R.id.txtPersonaReporta);
            img_personaReporte=(ImageView) v.findViewById(R.id.card_news_imgs_personareporta);
            descripcionReporte=(TextView)v.findViewById(R.id.card_news_descripcion);

        }

    }
    @Override
    public ReportesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_new,viewGroup,false);
        return new ReportesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportesViewHolder viewHolder, final int i) {

        Picasso.with(viewHolder.img_personaReporte.getContext())
                .load(items.get(i).getFotoPersonaReporta()).into(viewHolder.img_personaReporte);
        Picasso.with(viewHolder.card_news_imgs.getContext())
                .load(items.get(i).getImagen()).into(viewHolder.card_news_imgs);

        //viewHolder.iD.setText("Id: "+items.get(i).getId());
        viewHolder.txtPersonareporta.setText(items.get(i).getPersonaReporta());
        viewHolder.descripcionReporte.setText(items.get(i).getDescripcionReporte());

       viewHolder.card_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* Bundle bundle =new Bundle();
                bundle.putString("ctImagen",items.get(i).getImagen());
                bundle.putString("ctId",items.get(i).getId());
                bundle.putString("ctNombre",items.get(i).getName());
                bundle.putString("ctEmail",items.get(i).getEmail());
                bundle.putString("ctAddress",items.get(i).getAddress());
                bundle.putString("ctGender",items.get(i).getGender());
                bundle.putString("ctMobile",items.get(i).getMobile());
                bundle.putString("ctHome",items.get(i).getHome());
                bundle.putString("ctOffice",items.get(i).getOffice());
                Intent iconIntent =new Intent(view.getContext(), Detalle.class);
                iconIntent.putExtras(bundle);
                view.getContext().startActivity(iconIntent);*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }
    public ReporteAdapterNews(List<VariablesReportesNews>items){
        this.items=items;
    }

    public List<VariablesReportesNews> getItems(){
        return this.items;
    }

}
