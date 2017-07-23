package com.app.master.puntosderecargatrancaribe.Presentador;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.app.master.puntosderecargatrancaribe.MapsActivity;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.AdaptadorEnpointGoogle;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.Coordenadas;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.Endpoin;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.FirebaseReferences;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.Paradero;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.RespuestaCoordenadas;
import com.app.master.puntosderecargatrancaribe.Vista.iMapsActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rafael p on 20/7/2017.
 */

public class PresentadorMainActivity implements iPresentadorMainActivity{

    private FirebaseDatabase database;
    private Context context;
    private ArrayList<Coordenadas> coordenadasMapa;
    private iMapsActivity activity;
    private final String token ="AIzaSyDjjRBHOHlbzcFrVl_xQAK07u0EZyr19YQ";

    public PresentadorMainActivity(Context context, iMapsActivity activity){
        this.context=context;
        this.activity=activity;
        agregarPuntoRecarga();
    }

    public void agregarPuntoRecarga(){
        if(activity.verificarInternet()) {
            database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(FirebaseReferences.referencia_recarga);
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot dato : dataSnapshot.getChildren()) {
                        Paradero paradero = dato.getValue(Paradero.class);
                        activity.AgregarPuntosRecarga(paradero.getLatitud(), paradero.getLongitud(), paradero.getNombre());

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(context, "Error en el servidor, intente mas tarde", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(context, "Verifique su conexion a internet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void agregarLimitesMapa() {
        activity.establecerLimitesMapa();
    }

    @Override
    public void obtenerRutaMapa() {
        if (activity.verificarInternet()) {
            final ProgressDialog progreso=new ProgressDialog(context);
            progreso.setTitle("Iniciando ruta");
            progreso.setMessage("Dibujando aproximacion de ruta ");
            progreso.setCancelable(false);
            progreso.show();
            AdaptadorEnpointGoogle conexion = new AdaptadorEnpointGoogle();
            Gson gson = conexion.construyeJsonDeserializador();
            Endpoin endpoint = conexion.establecerConexionGoogleMaps(gson);
            Call<RespuestaCoordenadas> respuesta = endpoint.getUbicacion(String.valueOf(activity.getLocation().getLatitude()) + "," + String.valueOf(activity.getLocation().getLongitude()),
                    String.valueOf(activity.getLocationMarcador().latitude) + "," + String.valueOf(activity.getLocationMarcador().longitude),
                    token, "walking");
            respuesta.enqueue(new Callback<RespuestaCoordenadas>() {
                @Override
                public void onResponse(Call<RespuestaCoordenadas> call, Response<RespuestaCoordenadas> response) {
                    coordenadasMapa = response.body().getCoordenadas();
                    activity.dibujarpolyline(coordenadasMapa);
                    progreso.dismiss();

                }

                @Override
                public void onFailure(Call<RespuestaCoordenadas> call, Throwable t) {
                    Toast.makeText(context, "Error al conectar con el servidor " + t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

        }else {
            Toast.makeText(context, "Verifique su conexion a internet", Toast.LENGTH_SHORT).show();
        }
    }
}
