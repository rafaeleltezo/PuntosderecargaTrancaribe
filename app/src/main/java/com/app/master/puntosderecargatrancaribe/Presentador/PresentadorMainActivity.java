package com.app.master.puntosderecargatrancaribe.Presentador;

import android.content.Context;
import android.widget.Toast;

import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.Coordenadas;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.FirebaseReferences;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.Paradero;
import com.app.master.puntosderecargatrancaribe.Vista.iMapsActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Rafael p on 20/7/2017.
 */

public class PresentadorMainActivity implements iPresentadorMainActivity{

    private FirebaseDatabase database;
    private Context context;
    private ArrayList<Coordenadas> coordenadasMapa;
    private iMapsActivity activity;

    public PresentadorMainActivity(Context context, iMapsActivity activity){
        this.context=context;
        this.activity=activity;
    }

    public void agregarPuntoRecarga(){
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FirebaseReferences.referencia_recarga);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dato:dataSnapshot.getChildren()){
                    Paradero paradero=dato.getValue(Paradero.class);
                    MarkerOptions marker=new MarkerOptions();
                    marker.position(new LatLng(paradero.getLatitud(),paradero.getLatitud()));
                    activity.AgregarPuntosRecarga(marker);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Error en el servidor, intente mas tarde", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void agregarLimitesMapa() {
        activity.establecerLimitesMapa();
    }

}
