package com.app.master.puntosderecargatrancaribe.Vista.Adaptadores;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.ModeloBuscador.RutaBusParadero;
import com.app.master.puntosderecargatrancaribe.R;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by Rafael p on 22/8/2017.
 */

public class AdaptadorReciclerViewRuta extends RecyclerView.Adapter<AdaptadorReciclerViewRuta.AdaptadorHolder> {

    private Context context;
    private ArrayList<RutaBusParadero> rutaBusParaderos;
    private int contador;

    public AdaptadorReciclerViewRuta(Context context,ArrayList<RutaBusParadero> rutaBusParaderos){
        this.context=context;
        this.rutaBusParaderos=rutaBusParaderos;
        contador=1;
    }

    @Override
    public AdaptadorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista= LayoutInflater.from(parent.getContext()).inflate(R.layout.cartaruta,parent,false);
        return new AdaptadorHolder(vista);
    }

    @Override
    public void onBindViewHolder(AdaptadorHolder holder, int position) {
        RutaBusParadero ruta=rutaBusParaderos.get(position);
        holder.contador.setText(String.valueOf(contador++));
        holder.nombreBus.setText("Aborda "+ruta.getNombreBus());
        holder.contador.setText("En el "+ruta.getParaderoBuscador().getDescripcion());
    }

    @Override
    public int getItemCount() {
        return rutaBusParaderos.size();
    }

    public static class AdaptadorHolder extends RecyclerView.ViewHolder{

        private TextView contador,nombreBus,nombreEstacion;

        public AdaptadorHolder(View itemView) {
            super(itemView);
            contador=       (TextView)  itemView.findViewById(R.id.contador);
            nombreBus=      (TextView)  itemView.findViewById(R.id.nombreBus);
            nombreEstacion= (TextView)  itemView.findViewById(R.id.nombreEstacion);
        }
    }
}
