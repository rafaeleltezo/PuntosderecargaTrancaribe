package com.app.master.puntosderecargatrancaribe.Presentador;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.app.master.puntosderecargatrancaribe.MapsActivity;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.AdaptadorEnpointGoogle;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.Coordenadas;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.Endpoin;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.FirebaseReferences;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.Paradero;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.RespuestaCoordenadas;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.RespuestaRutaCorta;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.RutaCorta;
import com.app.master.puntosderecargatrancaribe.Vista.iMapsActivity;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rafael p on 20/7/2017.
 */

public class PresentadorMainActivity implements iPresentadorMainActivity {

    private FirebaseDatabase database;
    private Context context;
    private ArrayList<Coordenadas> coordenadasMapa;
    private ArrayList<Paradero> coordenadasParaderos;
    private ArrayList<RutaCorta> rutacorta;
    private iMapsActivity activity;
    private TareaAsincronaMejorRuta tarea;
    private final String token = "AIzaSyDjjRBHOHlbzcFrVl_xQAK07u0EZyr19YQ";

    public PresentadorMainActivity(Context context, iMapsActivity activity) {
        this.context = context;
        this.activity = activity;
        agregarPuntoRecarga();

    }

    public void agregarPuntoRecarga() {
        if (activity.verificarInternet()) {
            coordenadasParaderos = new ArrayList();
            database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(FirebaseReferences.referencia_recarga);
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot dato : dataSnapshot.getChildren()) {
                        Paradero paradero = dato.getValue(Paradero.class);
                        activity.AgregarPuntosRecarga(paradero.getLatitud(), paradero.getLongitud(), paradero.getNombre(), paradero.getDescripcion());
                        coordenadasParaderos.add(paradero);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(context, "Error en el servidor, intente mas tarde", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
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
            final ProgressDialog progreso = new ProgressDialog(context);
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

        } else {
            Toast.makeText(context, "Verifique su conexion a internet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void obtenerRutaMapa(Paradero paradero) {
        if (activity.verificarInternet()) {
            final ProgressDialog progreso = new ProgressDialog(context);
            progreso.setTitle("Iniciando ruta");
            progreso.setMessage("Dibujando aproximacion de ruta ");
            progreso.setCancelable(false);
            progreso.show();
            AdaptadorEnpointGoogle conexion = new AdaptadorEnpointGoogle();
            Gson gson = conexion.construyeJsonDeserializador();
            Endpoin endpoint = conexion.establecerConexionGoogleMaps(gson);
            Call<RespuestaCoordenadas> respuesta = endpoint.getUbicacion(String.valueOf(activity.getLocation().getLatitude()) + "," + String.valueOf(activity.getLocation().getLongitude()),
                    String.valueOf(paradero.getLatitud()) + "," + String.valueOf(paradero.getLongitud()),
                    token, "walking");
            respuesta.enqueue(new Callback<RespuestaCoordenadas>() {
                @Override
                public void onResponse(Call<RespuestaCoordenadas> call, Response<RespuestaCoordenadas> response) {
                    coordenadasMapa = response.body().getCoordenadas();
                    activity.dibujarpolyline(coordenadasMapa);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(coordenadasMapa.get(1).getLatitud(),coordenadasMapa.get(1).getLongitud()))
                            .zoom(17)
                            .bearing(0)
                            .tilt(0)
                            .build();
                    activity.moverCamaraMapa(cameraPosition);
                    progreso.dismiss();

                }

                @Override
                public void onFailure(Call<RespuestaCoordenadas> call, Throwable t) {
                    Toast.makeText(context, "Error al conectar con el servidor " + t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

        } else {
            Toast.makeText(context, "Verifique su conexion a internet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void obtenerutaCercana() {
       /* if (activity.verificarInternet()) {
            AdaptadorEnpointGoogle conexion= new AdaptadorEnpointGoogle();
            Gson gson=conexion.costruyeJsonDeserializadorDistanciaCorta();
            Endpoin endpoin=conexion.establecerConexionGoogleMaps(gson);
            rutacorta=new ArrayList();
            Call<RespuestaRutaCorta> respuesta=endpoin.getubicacionCorta(String.valueOf(activity.getLocation().getLatitude()) + "," + String.valueOf(activity.getLocation().getLongitude()),
                    String.valueOf(paradero.getLatitud())+","+String.valueOf(paradero.getLongitud()),token, "walking");
            respuesta.enqueue(new Callback<RespuestaRutaCorta>() {
                @Override
                public void onResponse(Call<RespuestaRutaCorta> call, Response<RespuestaRutaCorta> response) {
                    RespuestaRutaCorta respuesta=response.body();
                    RutaCorta ruta=new RutaCorta();
                    ruta.setDistacia(respuesta.getDistancia());
                    ruta.setParadero(paradero);
                    rutacorta.add(ruta);
                }

                @Override
                public void onFailure(Call<RespuestaRutaCorta> call, Throwable t) {
                    Toast.makeText(context, "Error al conectar al servidor, intente mas tarde", Toast.LENGTH_SHORT).show();
                }
            });

        }*/
        if (activity.verificarInternet()) {
            DecimalFormat df = new DecimalFormat("#.00");
            String latitulMiPosicion = df.format(activity.getLocation().getLatitude());
            String longitudlMiPosicion = df.format(activity.getLocation().getLongitude());
            ArrayList<Paradero> coordenadasPuntoRecarga = new ArrayList();
            for (Paradero paraderos : coordenadasParaderos) {
                df = new DecimalFormat("#.00");
                String latitudRecarga = df.format(paraderos.getLatitud());
                String longitudRecarga = df.format(paraderos.getLongitud());
                if (latitulMiPosicion.equals(latitudRecarga) && longitudlMiPosicion.equals(longitudRecarga)) {
                    coordenadasPuntoRecarga.add(paraderos);
                }

            }
            tarea = new TareaAsincronaMejorRuta(coordenadasPuntoRecarga);
            tarea.execute();

        } else {
            Toast.makeText(context, "No esta conectado a internet", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void dibujarRutaCortaMapa() {
        obtenerutaCercana();
        /*
        final ProgressDialog progreso=new ProgressDialog(context);
        progreso.setTitle("Iniciando ruta");
        progreso.setMessage("Dibujando aproximacion de ruta mas cercana ");
        progreso.setCancelable(false);
        progreso.show();
        for (Paradero paradero:coordenadasParaderos) {
            obtenerutaCercana(paradero);
            Toast.makeText(context, paradero.getNombre(), Toast.LENGTH_SHORT).show();
        }
       double distancia=rutacorta.get(0).getDistacia();
        for (RutaCorta ruta:rutacorta) {
            if(ruta.getDistacia()<distancia){
                distancia=ruta.getDistacia();
                Toast.makeText(context, String.valueOf(distancia), Toast.LENGTH_SHORT).show();
            }

        }

        if (activity.verificarInternet()) {
            DecimalFormat df = new DecimalFormat("#.00");
            String latitulMiPosicion = df.format(activity.getLocation().getLatitude());
            String longitudlMiPosicion = df.format(activity.getLocation().getLongitude());
            ArrayList<Paradero> coordenadasPuntoRecarga = new ArrayList();
            for (Paradero paraderos : coordenadasParaderos) {
                df = new DecimalFormat("#.00");
                String latitudRecarga = df.format(paraderos.getLatitud());
                String longitudRecarga = df.format(paraderos.getLongitud());
                if (latitulMiPosicion.equals(latitudRecarga) && longitudlMiPosicion.equals(longitudlMiPosicion)) {
                    coordenadasPuntoRecarga.add(paraderos);
                }

            }
            TareaAsincronaMejorRuta tarea = new TareaAsincronaMejorRuta(coordenadasPuntoRecarga);
            tarea.execute();

        } else {
            Toast.makeText(context, "No esta conectado a internet", Toast.LENGTH_LONG).show();
        }*/


    }

    private class TareaAsincronaMejorRuta extends AsyncTask<Void, Void, Void> {
        private ArrayList<Paradero> coordenadas;
        private  ArrayList<RutaCorta> rutas;
        final ProgressDialog progreso = new ProgressDialog(context);
        private RutaCorta rutaCortaRecarga;;

        TareaAsincronaMejorRuta(ArrayList<Paradero> coordenadasParaderos) {
            this.coordenadas = coordenadasParaderos;
        }

        public ArrayList<RutaCorta> getRutas() {
            return rutas;
        }


        @Override
        protected void onPreExecute() {
            progreso.setTitle("Iniciando ruta");
            progreso.setMessage("Buscanco ruta cercana ");
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            for (final Paradero paradero : coordenadas) {

                final AdaptadorEnpointGoogle conexion = new AdaptadorEnpointGoogle();
                Gson gson = conexion.costruyeJsonDeserializadorDistanciaCorta();
                Endpoin endpoin = conexion.establecerConexionGoogleMaps(gson);
                rutas = new ArrayList();
                Call<RespuestaRutaCorta> respuesta = endpoin.getubicacionCorta(String.valueOf(activity.getLocation().getLatitude()) + "," + String.valueOf(activity.getLocation().getLongitude()),
                        String.valueOf(paradero.getLatitud()) + "," + String.valueOf(paradero.getLongitud()), token, "walking");
                respuesta.enqueue(new Callback<RespuestaRutaCorta>() {
                    @Override
                    public void onResponse(Call<RespuestaRutaCorta> call, Response<RespuestaRutaCorta> response) {
                        RespuestaRutaCorta respuesta = response.body();
                        RutaCorta ruta = new RutaCorta();
                        ruta.setDistacia(respuesta.getDistancia());
                        ruta.setParadero(paradero);
                        rutas.add(ruta);
                        double distacia=rutas.get(0).getDistacia();
                        if(ruta.getDistacia()<distacia){
                            distacia=ruta.getDistacia();
                            rutaCortaRecarga=ruta;
                            Toast.makeText(context, rutaCortaRecarga.getParadero().getNombre(), Toast.LENGTH_SHORT).show();
                            obtenerRutaMapa(rutaCortaRecarga.getParadero());
                        }
                    }

                    @Override
                    public void onFailure(Call<RespuestaRutaCorta> call, Throwable t) {
                        Toast.makeText(context, "Error al conectar al servidor, intente mas tarde", Toast.LENGTH_SHORT).show();
                    }
                });


            }

            return null;
        }



        @Override
        protected void onPostExecute(Void aVoid) {

            progreso.dismiss();
        }
    }
}
