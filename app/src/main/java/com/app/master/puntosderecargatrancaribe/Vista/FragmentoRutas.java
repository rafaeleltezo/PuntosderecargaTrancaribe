package com.app.master.puntosderecargatrancaribe.Vista;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.AdaptadorEnpointGoogle;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.AuxiliarBus;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.Endpoin;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.FirebaseReferences;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.ModeloBuscador.Bus;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.ModeloBuscador.ParaderoBuscador;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.ModeloBuscador.ParaderoDistancia;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.ModeloBuscador.RutaBusParadero;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.RespuestaRutaCorta;
import com.app.master.puntosderecargatrancaribe.Presentador.GpsUtil;
import com.app.master.puntosderecargatrancaribe.R;
import com.app.master.puntosderecargatrancaribe.Vista.Adaptadores.AdaptadorReciclerViewRuta;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.ads.AdRequest.LOGTAG;


public class FragmentoRutas extends Fragment implements View.OnClickListener/*,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks*/, LocationListener, OnMapReadyCallback {

    private static final int PETICION_PERMISO_LOCALIZACION = 2;
    private static final int PETICION_CONFIG_UBICACION = 12;
    private View vista;
    private ParaderoBuscador paraderoOrigen;
    private ParaderoBuscador paraderoDestino;
    private ArrayList<ParaderoBuscador> paraderos;
    private ArrayList<ParaderoBuscador> paraderoSegundarios;
    private ArrayList<RutaBusParadero> rutaBusParaderos;
    private AutoCompleteTextView autoCompletador;
    private Button botonIr;
    private RecyclerView recyclerView;
    private LocationRequest locRequest;
    private Location localizacion;
    private GoogleMap mMap;
    private final String token = "AIzaSyDjjRBHOHlbzcFrVl_xQAK07u0EZyr19YQ";
    private ArrayList<ParaderoDistancia> distancias;
    int contador;
    private GoogleApiClient apiClient;
    private MapView mapView;
    private FirebaseDatabase database;
    private ArrayList<ParaderoBuscador> paraderosFirebase;
    private ArrayList<String> busqueda;

    private InterstitialAd interstitialAd;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment_rutas, container, false);

        interstitialAd = new InterstitialAd(getContext(), "133411673871431_176478619564736");
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Toast.makeText(getContext(), "Error del baner", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
        contador = 0;
        paraderos = datosParadero();
        paraderoSegundarios = datosParaderoSegundario();
        busqueda = new ArrayList();
        /*for (ParaderoBuscador p : paraderos) {
            for (String palabras:descomponerPalabra(p.getPalabrasClaves())) {
                busqueda.add(palabras);
            }
            //busqueda.add(descomponerpalabra(p.getPalabrasClaves()));
        }*/
        for (ParaderoBuscador p : paraderoSegundarios) {
            for (String palabras : descomponerPalabra(p.getPalabrasClaves())) {
                busqueda.add(palabras);
            }
            // busqueda.add(descomponerpalabra(p.getPalabrasClaves()));
        }
        recyclerView = (RecyclerView) vista.findViewById(R.id.recycler);
        autoCompletador = (AutoCompleteTextView) vista.findViewById(R.id.buscarDestino);
        botonIr = (Button) vista.findViewById(R.id.botonIr);
        botonIr.setOnClickListener(this);
        ArrayAdapter<String> sa = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_item, busqueda);
        autoCompletador.setThreshold(1);
        autoCompletador.setAdapter(sa);

        //buscadorParaderoDestino();


        //setHasOptionsMenu(true);

        //buscadorParaderoDestino("sao");

        return vista;
    }

    public ArrayList<ParaderoBuscador> paraderos() {
        paraderosFirebase = new ArrayList();
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Paraderos");
/*
        ArrayList<ParaderoBuscador>ps=new ArrayList();
        ArrayList<Bus> patioPortal = new ArrayList();
        patioPortal.add(new Bus("t102", "Crespo"));
        patioPortal.add(new Bus("t101", "Portal"));
        patioPortal.add(new Bus("t103", "Bocagrande"));
        patioPortal.add(new Bus("t100", "Expresa"));
        ps.add(new ParaderoBuscador(patioPortal, "Estacion Patio portal", "Estacion patio portal principal", "patio portal,principal", 10.398055780319902, -75.47220951699977, 1, "principal"));

        ArrayList<Bus> busesBombaGallo = new ArrayList();
        busesBombaGallo.add(new Bus("t102", "Crespo"));
        busesBombaGallo.add(new Bus("t101", "Portal"));
        busesBombaGallo.add(new Bus("t103", "Bocagrande"));
        busesBombaGallo.add(new Bus("t100", "Expresa"));
        ps.add(new ParaderoBuscador(busesBombaGallo, "Bomba del gallo", "Paradero frente Bomba del gallo", "Bomba del gallo", 10.398055780319902, -75.47220951699977, 2, "principal"));

        ArrayList<Bus> busesCastellana = new ArrayList();
        //buses de la castellana
        busesCastellana.add(new Bus("t102", "Crespo"));
        busesCastellana.add(new Bus("t101", "Portal"));
        busesCastellana.add(new Bus("t103", "Bocagrande"));
        busesCastellana.add(new Bus("t106", "Variante"));
        busesCastellana.add(new Bus("t100", "Expresa"));
        ps.add(new ParaderoBuscador(busesCastellana, "Castellana", "Paradero de la castellana", "castellana,Exito cartagena,",10.394465202913,-75.4866528, 3, "principal"));
        //buses de cuatro vientos
        ArrayList<Bus> losejecutivos = new ArrayList();
        losejecutivos.add(new Bus("t103", "Bocagrande"));
        losejecutivos.add(new Bus("t106", "Variante"));
        losejecutivos.add(new Bus("t101", "Portal"));
        ps.add(new ParaderoBuscador(losejecutivos, "Ejecutivos", "paradero los ejecutivos", "los ejecutivos", 10.399442402914431, -75.4936444, 4, "principal"));
        //paradero inventado

        ArrayList<Bus> busesVillaOlimpica = new ArrayList();
        busesVillaOlimpica.add(new Bus("t101", "Portal"));
        ps.add(new ParaderoBuscador(busesVillaOlimpica, "Villa olimpica", "paradero Villa olimpica", "Estadio jaime moron, villa olimpica,", 10.40363990291561,-75.49717050000004, 5, "principal"));

        ArrayList<Bus> busesCuatroViento = new ArrayList();
        busesCuatroViento.add(new Bus("t101", "Portal"));
        busesCuatroViento.add(new Bus("t102", "Crespo"));
        busesCuatroViento.add(new Bus("t103", "Bocagrande"));
        ps.add(new ParaderoBuscador(busesCuatroViento, "Cuatro vientos", "paradero Cuatro vientos", "cuatro vientos,frente sena cuatro viento,",10.40642890291641, -75.50229150000001, 6, "principal"));

        //buses de crespo
        ArrayList<Bus> busesMariaAuxiliadora = new ArrayList();
        busesMariaAuxiliadora.add(new Bus("t106", "Variante"));
        busesMariaAuxiliadora.add(new Bus("t101", "Portal"));
        ps.add(new ParaderoBuscador(busesMariaAuxiliadora, "Maria auxiliadora", "paradero Maria auxiliadora", "maria auxiliadora,cai maria auxiliadora", 10.408993402917146,-75.51582759999997, 7, "principal"));


        ArrayList<Bus> busesBasurto = new ArrayList();
        busesBasurto.add(new Bus("t106", "Variante"));
        busesBasurto.add(new Bus("t102", "Crespo"));
        busesBasurto.add(new Bus("t108", "Bocagrande"));
        ps.add(new ParaderoBuscador(busesBasurto, "Basurto", "paradero Basurto", "mercado Basurto",10.413787102918551,-75.52402340000003, 8, "principal"));

        myRef.setValue(ps);
  */
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dato : dataSnapshot.getChildren()) {
                    ParaderoBuscador paraderoBuscador = dato.getValue(ParaderoBuscador.class);
                    paraderosFirebase.add(paraderoBuscador);
                    for (String palabras : descomponerPalabra(paraderoBuscador.getPalabrasClaves())) {
                        busqueda.add(palabras);
                    }
                    // busqueda.add(descomponerpalabra(paraderoBuscador.getPalabrasClaves()));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error en el servidor, intente mas tarde", Toast.LENGTH_SHORT).show();
            }
        });

        //Toast.makeText(getContext(), String.valueOf(paraderosFirebase.size()), Toast.LENGTH_SHORT).show();
        return paraderosFirebase;
    }

    public ArrayList<String> descomponerpalabra(String palabra) {
        String[] palabras = palabra.split(",");
        ArrayList<String> palabrasclave = new ArrayList();
        for (String palabrasc : palabras) {
            String sinEspacio = palabrasc.trim();
            palabrasclave.add(sinEspacio);
            //Toast.makeText(getContext(), sinEspacio, Toast.LENGTH_SHORT).show();
            //return sinEspacio;
        }
        return palabrasclave;
    }


    public void determinarRuta() {
        if (paraderoOrigen.getTipo().equals("segundario") && paraderoDestino.getTipo().equals("segundario")) {
            int origenPosicion = (int) paraderoOrigen.getPosicion();
            int destinoPosicion = (int) paraderoDestino.getPosicion();
            //rutaBusParaderos = new ArrayList();
            //rutaVenidaeIda(paraderoOrigen, paraderoDestino);
            if (origenPosicion == destinoPosicion) {
                rutaBusParaderos = new ArrayList();
                rutaVenidaeIda(paraderoOrigen, paraderoDestino);


            } else {
                rutaBusParaderos = new ArrayList();
                int destinoParadero = (int) paraderoOrigen.getPosicion() + 1;
                ParaderoBuscador destinosegundario = obtenerPosicionParaderoPrincipal(destinoParadero);
                //Toast.makeText(getContext(), destinosegundario.getNombre(), Toast.LENGTH_SHORT).show();
                rutaVenidaeIda(paraderoOrigen, destinosegundario);
                //Toast.makeText(getContext(),"el origen es: "+destinosegundario.getNombre()+" el destino es: "+paraderoDestino.getNombre(), Toast.LENGTH_SHORT).show();
                rutaVenidaeIda(destinosegundario, paraderoDestino);

                int destino = (int) paraderoDestino.getPosicion() + 1;
                ParaderoBuscador destinosegundarios = obtenerPosicionParaderoPrincipal(destino);
                rutaVenidaeIda(paraderoOrigen, destinosegundarios);
                rutaVenidaeIda(destinosegundarios, paraderoDestino);


            }

        } else if (paraderoOrigen.getTipo().equals("segundario") && paraderoDestino.getTipo().equals("principal")) {
            rutaBusParaderos = new ArrayList();
            int destino = (int) paraderoOrigen.getPosicion() + 1;
            ParaderoBuscador destinosegundario = obtenerPosicionParaderoPrincipal(destino);
            //Toast.makeText(getContext(), destinosegundario.getNombre(), Toast.LENGTH_SHORT).show();
            rutaVenidaeIda(paraderoOrigen, destinosegundario);
            //Toast.makeText(getContext(),"el origen es: "+destinosegundario.getNombre()+" el destino es: "+paraderoDestino.getNombre(), Toast.LENGTH_SHORT).show();
            rutaVenidaeIda(destinosegundario, paraderoDestino);


            //rutaVenidaeIda(paraderoOrigen,paraderoDestino);

        } else if (paraderoDestino.getTipo().equals("principal") && paraderoOrigen.getTipo().equals("principal")) {
            rutaBusParaderos = new ArrayList();
            rutaVenidaeIda(paraderoOrigen, paraderoDestino);
            //Toast.makeText(getContext(), "principales", Toast.LENGTH_SHORT).show();

        } else if (paraderoDestino.getTipo().equals("segundario") && paraderoOrigen.getTipo().equals("principal")) {
            rutaBusParaderos = new ArrayList();
            int destino = (int) paraderoDestino.getPosicion() + 1;
            ParaderoBuscador destinosegundario = obtenerPosicionParaderoPrincipal(destino);
            rutaVenidaeIda(paraderoOrigen, destinosegundario);
            rutaVenidaeIda(destinosegundario, paraderoDestino);
        }

        /*
        switch (paraderoOrigen.getTipo()){
            case "segundario":


                //Toast.makeText(getContext(), obtenerPosicionParaderoPrincipal(destino).getNombre(), Toast.LENGTH_SHORT).show();
                if(paraderoDestino.getTipo().equals("segundario")){

                }
                break;
            case "principal":
                //Toast.makeText(getContext(), "primario", Toast.LENGTH_SHORT).show();
                rutaVenidaeIda(paraderoOrigen,paraderoDestino);
                break;
        }*/
        //imprimire();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.buscador, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView(((Principal) getContext()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (buscadorParaderoDestino(query)) {
                    determinarRuta();
                    return true;
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Toast.makeText(getContext(), newText, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {

                                          }
                                      }
        );


    }

    public ArrayList<RutaBusParadero> obtenerrutas() {
        rutaBusParaderos.add(new RutaBusParadero(paraderoDestino, paraderoDestino.getNombre()));
        return rutaBusParaderos;
    }

    public ParaderoBuscador obtenerPosicionParaderoPrincipal(int posicion) {
        for (ParaderoBuscador p : paraderos) {
            if (p.getPosicion() == posicion) {
                return p;
            }
        }
        return null;
    }

    public void rutaVenidaeIda(ParaderoBuscador origen, ParaderoBuscador destino) {
        try {
            if (destino.getPosicion() > origen.getPosicion()) {
                //Toast.makeText(getContext(), "entre", Toast.LENGTH_SHORT).show();
                rutaCercana(origen, destino);

            } else if (destino.getPosicion() < origen.getPosicion()) {
                double origenes = origen.getPosicion();
                //origen.setPosicion(-1*origenes);
                convertirPosicionParaderosNegativo();
                //Toast.makeText(getContext(),"el origen es: "+ String.valueOf(origen.getPosicion())+ " el destino es: "+String.valueOf(destino.getPosicion()), Toast.LENGTH_SHORT).show();
                rutaCercana(origen, destino);

            }
        } catch (Exception e) {

        }
    }

    public void convertirPosicionParaderosNegativo() {

        for (int i = 0; i < paraderos.size(); i++) {
            double posicion = paraderos.get(i).getPosicion();
            paraderos.get(i).setPosicion(-1 * posicion);
        }
        for (int i = 0; i < paraderoSegundarios.size(); i++) {
            double posicion = paraderoSegundarios.get(i).getPosicion();
            paraderoSegundarios.get(i).setPosicion(-1 * posicion);
        }

    }

    public ArrayList<ParaderoBuscador> datosParaderoSegundario() {
        paraderoSegundarios = new ArrayList();
        /*
        for (ParaderoBuscador p:paraderosFirebase) {
            if(p.getTipo().equals("segundario")){
                paraderoSegundarios.add(p);
            }
        }
        */

        ArrayList<Bus> busesAlimentador1Castellana = new ArrayList();
        //buses de la castellana
        busesAlimentador1Castellana.add(new Bus("t106", "Variante"));
        //busesAlimentador1Castellana.add(new Bus("t1018","Bocagrande"));
        // busesAlimentador1Castellana.add(new Bus("xt101","Todas las paradas"));
        paraderoSegundarios.add(new ParaderoBuscador(busesAlimentador1Castellana, "bomba el amparo", "Paradero frente bomba el amparo", "bomba el amparo,cai bomba el amparo,", 10.3807949925699, -75.46381123809, 1.1, "segundario"));

        ArrayList<Bus> busesAlimentador2Castellana = new ArrayList();
        //buses de la castellana
        busesAlimentador2Castellana.add(new Bus("t106", "Variante"));
        // busesAlimentador2Castellana.add(new Bus("t1031","Bocagrande"));
        //busesCastellana.add(new Bus("xt101","Todas las paradas"));
        paraderoSegundarios.add(new ParaderoBuscador(busesAlimentador2Castellana, "sao", "Paradero frente sao", "sao,prueba", 10.380177949925, -75.46380371129, 1.2, "segundario"));

        ArrayList<Bus> busesAlimentador3Castellana = new ArrayList();
        //buses de la castellana
        busesAlimentador3Castellana.add(new Bus("t101", "Variante"));
        //busesCastellana.add(new Bus("xt101","Todas las paradas"));
        paraderoSegundarios.add(new ParaderoBuscador(busesAlimentador3Castellana, "sanjose", "Paradero frente sanjose", "san jose,barrio", 10.580177949925699, -75.56380371123809, 3.3, "segundario"));

        return paraderoSegundarios;
    }

    //datos entrantes
    public ArrayList<ParaderoBuscador> datosParadero() {
        paraderos = new ArrayList();
        for (ParaderoBuscador p : paraderosFirebase) {
            if (p.getTipo().equals("principal")) {
                //Toast.makeText(getContext(), String.valueOf(p.getBus().size()), Toast.LENGTH_SHORT).show();
                paraderos.add(p);
            }
            //Toast.makeText(getContext(), String.valueOf(paraderos.get(0)), Toast.LENGTH_SHORT).show();

        }
    /*
        ArrayList<Bus> busesBombaGallo = new ArrayList();
        //buses de la castellana
        busesBombaGallo.add(new Bus("t102", "Crespo"));
        busesBombaGallo.add(new Bus("t101", "Portal"));
        busesBombaGallo.add(new Bus("t103", "Bocagrande"));
        busesBombaGallo.add(new Bus("t100", "Expresa"));
        paraderos.add(new ParaderoBuscador(busesBombaGallo, "Bomba del gallo", "Paradero frente Bomba del gallo", "Bomba del gallo", 10.398055780319902, -75.47220951699977, 1, "principal"));

        ArrayList<Bus> busesCastellana = new ArrayList();
        //buses de la castellana
        busesCastellana.add(new Bus("t102", "Crespo"));
        busesCastellana.add(new Bus("t101", "Portal"));
        busesCastellana.add(new Bus("t103", "Bocagrande"));
        busesCastellana.add(new Bus("t106", "Variante"));
        busesCastellana.add(new Bus("t100", "Expresa"));
        paraderos.add(new ParaderoBuscador(busesCastellana, "Castellana", "Paradero de la castellana", "castellana,Exito cartagena,",10.394465202913,-75.4866528, 2, "principal"));
        //buses de cuatro vientos
        ArrayList<Bus> losejecutivos = new ArrayList();
        losejecutivos.add(new Bus("t103", "Bocagrande"));
        losejecutivos.add(new Bus("t106", "Variante"));
        losejecutivos.add(new Bus("t101", "Portal"));
        paraderos.add(new ParaderoBuscador(losejecutivos, "Ejecutivos", "paradero los ejecutivos", "los ejecutivos", 10.399442402914431, -75.4936444, 3, "principal"));
        //paradero inventado

        ArrayList<Bus> busesVillaOlimpica = new ArrayList();
        busesVillaOlimpica.add(new Bus("t101", "Portal"));
        paraderos.add(new ParaderoBuscador(busesVillaOlimpica, "Villa olimpica", "paradero Villa olimpica", "Estadio jaime moron, villa olimpica,", 10.40363990291561,-75.49717050000004, 4, "principal"));

        ArrayList<Bus> busesCuatroViento = new ArrayList();
        busesCuatroViento.add(new Bus("t101", "Portal"));
        busesCuatroViento.add(new Bus("t102", "Crespo"));
        busesCuatroViento.add(new Bus("t103", "Bocagrande"));
        paraderos.add(new ParaderoBuscador(busesCuatroViento, "Cuatro vientos", "paradero Cuatro vientos", "cuatro vientos,frente sena cuatro viento,",10.40642890291641, -75.50229150000001, 5, "principal"));

        //buses de crespo
        ArrayList<Bus> busesMariaAuxiliadora = new ArrayList();
        busesMariaAuxiliadora.add(new Bus("t106", "Variante"));
        busesMariaAuxiliadora.add(new Bus("t101", "Portal"));
        paraderos.add(new ParaderoBuscador(busesMariaAuxiliadora, "Maria auxiliadora", "paradero Maria auxiliadora", "maria auxiliadora,cai maria auxiliadora", 10.408993402917146,-75.51582759999997, 6, "principal"));


        ArrayList<Bus> busesBasurto = new ArrayList();
        busesBasurto.add(new Bus("t106", "Variante"));
        busesBasurto.add(new Bus("t102", "Crespo"));
        busesBasurto.add(new Bus("t108", "Bocagrande"));
        paraderos.add(new ParaderoBuscador(busesBasurto, "Basurto", "paradero Basurto", "mercado Basurto",10.413787102918551,-75.52402340000003, 7, "principal"));
*/
        return paraderos;
    }


    //Buscar el paradero de destino a donde se dirige

    public Boolean buscadorParaderoDestino(String destino) {

        //buscador de paradero destino con el nombre solicitado por el usuario

        for (ParaderoBuscador paradero : paraderos) {
            for (String palabras : descomponerPalabra(paradero.getPalabrasClaves())) {
                if (palabras.equals(destino)) {
                    //Toast.makeText(getContext(), "Encontrado "+"Nombre del paradero: "+paradero.getNombre(), Toast.LENGTH_SHORT).show();
                    this.paraderoDestino = paradero;
                    //Toast.makeText(getContext(), paraderoDestino.getPalabrasClaves(), Toast.LENGTH_SHORT).show();
                    return true;

                }
            }
        }
        for (ParaderoBuscador paradero : paraderoSegundarios) {
            for (String palabras : descomponerPalabra(paradero.getPalabrasClaves())) {
                if (palabras.equals(destino)) {
                    //Toast.makeText(getContext(), "Encontrado "+"Nombre del paradero: "+paradero.getNombre(), Toast.LENGTH_SHORT).show();
                    this.paraderoDestino = paradero;
                    //Toast.makeText(getContext(), paraderoDestino.getPalabrasClaves(), Toast.LENGTH_SHORT).show();
                    return true;

                }
            }
        }
        return false;
    }

    public ArrayList<Bus> obtenerBusesParaAbordar(ParaderoBuscador origen, ParaderoBuscador destino) {
        ArrayList<Bus> busesParada = new ArrayList();

        for (Bus busorigen : origen.getBus()) {
            //Toast.makeText(getContext(), "Evlauando "+busorigen.getNombre()+ " con", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < destino.getBus().size(); i++) {
                if (busorigen.getNombre().equals(destino.getBus().get(i).getNombre())) {
                    //Toast.makeText(getContext(), paraderoDestino.getBus().get(i).getNombre(), Toast.LENGTH_SHORT).show();
                    busesParada.add(destino.getBus().get(i));
                }
                //  Toast.makeText(getContext(), paraderoDestino.getBus().get(i).getNombre(), Toast.LENGTH_SHORT).show();
            }
        }
        return busesParada;
    }

    public Boolean rutaCercana(ParaderoBuscador origen, ParaderoBuscador destino) {

        ArrayList<Bus> buses = new ArrayList();

        if (obtenerBusesParaAbordar(origen, destino).size() == 1) {
            buses.add(obtenerBusesParaAbordar(origen, destino).get(0));
            rutaBusParaderos.add(new RutaBusParadero(origen, obtenerBusesParaAbordar(origen, destino).get(0).getNombre()));

            //imprimir(buses);

            //Toast.makeText(getContext(), "Aborda el " + obtenerBusesParaAbordar(origen,destino).get(0).getNombre(), Toast.LENGTH_SHORT).show();
            return true;
        } else if (obtenerBusesParaAbordar(origen, destino).size() > 1) {
            ArrayList<Bus> bus = filtroBusMenosParadas(obtenerBusesParaAbordar(origen, destino));
            //filtroBusMenosParadas(bus);
            //Toast.makeText(getContext(),"son "+String.valueOf(bus.size()) , Toast.LENGTH_SHORT).show();
            if (bus.size() == 0) {
                buses.add(obtenerBusesParaAbordar(origen, destino).get(0));

                rutaBusParaderos.add(new RutaBusParadero(origen, obtenerBusesParaAbordar(origen, destino).get(0).getNombre()));
                //imprimir(buses);
            } else if (bus.size() == 1) {
                buses.add(bus.get(0));
                rutaBusParaderos.add(new RutaBusParadero(origen, obtenerBusesParaAbordar(origen, destino).get(0).getNombre()));
                //imprimir(buses);
                //Toast.makeText(getContext(), "Aborda: " + bus.get(0).getNombre(), Toast.LENGTH_SHORT).show();
                return true;
            } else if (bus.size() > 1) {
                buses.add(bus.get(0));

                rutaBusParaderos.add(new RutaBusParadero(origen, obtenerBusesParaAbordar(origen, destino).get(0).getNombre()));
                //buses.add(bus.get(1));
                //imprimir(buses);
                //Toast.makeText(getContext(), "Mejor ruta es: "+ bus.get(0).getNombre(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(getContext(), "Ruta segundaria es: "+ bus.get(1).getNombre(), Toast.LENGTH_SHORT).show();
                return true;
            }

        }
        if (obtenerBusesParaAbordar(origen, destino).size() == 0) {

            //Toast.makeText(getContext(), "Rutas alternativas", Toast.LENGTH_SHORT).show();
            //Toast.makeText(getContext(), paraderoDestino.getNombre(), Toast.LENGTH_SHORT).show();
            // rutaTransbordo();
        }

        return false;
    }

    private void imprimir(ArrayList<Bus> buses) {
        for (Bus b : buses) {
            Toast.makeText(getContext(), "Aborda el:  " + b.getNombre(), Toast.LENGTH_SHORT).show();
        }
    }

    private void rutaTransbordo() {
        int cantidadParaderos = paraderos.size();

        // if(paraderoOrigen.getPosicion()>0 && paraderoDestino.getPosicion()>0) {
        contador++;
        int paraderoPosterior = (int) paraderoDestino.getPosicion() + contador;
        int paraderoAnterior = (int) paraderoDestino.getPosicion() - contador;

        String cadena = String.valueOf(paraderoDestino.getPosicion());


        try {
            //Toast.makeText(getContext(), String.valueOf(paraderoPosterior), Toast.LENGTH_SHORT).show();
            if (paraderoOrigen.getPosicion() > 0 && paraderoDestino.getPosicion() > 0) {
                if (rutaCercana(paraderoOrigen, paraderos.get(paraderoAnterior - 1))) {
                    rutaCercana(paraderos.get(paraderoAnterior - 1), paraderoDestino);
                    //Toast.makeText(getContext(), paraderos.get( paraderoAnterior - 1).getNombre(), Toast.LENGTH_SHORT).show();
                }
            } else if (paraderoOrigen.getPosicion() < 0 && paraderoDestino.getPosicion() < 0) {
                if (rutaCercana(paraderoOrigen, paraderos.get(-1 * (paraderoAnterior - 1)))) {
                    rutaCercana(paraderos.get(-1 * (paraderoAnterior - 1)), paraderoDestino);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            if (paraderoOrigen.getPosicion() > 0 && paraderoDestino.getPosicion() > 0) {
                if (rutaCercana(paraderoOrigen, paraderos.get(paraderoPosterior + 1))) {
                    rutaCercana(paraderos.get(paraderoPosterior + 1), paraderoDestino);
                    //Toast.makeText(getContext(), paraderos.get( paraderoPosterior + 1).getNombre(), Toast.LENGTH_SHORT).show();
                }
            } else if (paraderoOrigen.getPosicion() < 0 && paraderoDestino.getPosicion() < 0) {
                if (rutaCercana(paraderoOrigen, paraderos.get(-1 * (paraderoPosterior + 1)))) {
                    rutaCercana(paraderos.get(-1 * (paraderoPosterior + 1)), paraderoDestino);
                }
            }
        }


        // }else{
        //   Toast.makeText(getContext(), "paradero menor", Toast.LENGTH_SHORT).show();
        //}
        //ArrayList<Bus>buses=obtenerBusesParaAbordar();
        //Toast.makeText(getContext(), buses.get(0).getNombre(), Toast.LENGTH_SHORT).show();
        /*
        Toast.makeText(getContext(), paraderos.get(paraderoAnterior-1).getNombre(), Toast.LENGTH_SHORT).show();
            ArrayList<Bus>bus=filtroBusMenosParadas(paraderoOrigen.getBus(),paraderos.get(paraderoAnterior-1));
            if(bus.size()>1){
                Toast.makeText(getContext(), bus.get(1).getNombre(), Toast.LENGTH_SHORT).show();
        }*/
    }

    private ArrayList<Bus> filtroBusMenosParadas(ArrayList<Bus> buses) {


        ArrayList<ParaderoBuscador> para = new ArrayList();

        //filtra los paraderos entre el origen y el destino devuelve un array con paraderos intermedios
        //for (Bus bus :buses) {
        //Toast.makeText(getContext(), "el bus: "+bus.getNombre()+ " para en ", Toast.LENGTH_SHORT).show();
        // for (int i=paraderoOrigen.getPosicion();i<paraderoDestino.getPosicion();i++) {
        //Toast.makeText(getContext(), paraderos.get(i).getNombre(), Toast.LENGTH_SHORT).show();
        //para.add(paraderos.get(i));

        // }
        // }

        if (paraderoOrigen.getPosicion() > 0 && paraderoDestino.getPosicion() > 0) {
            for (double i = paraderoOrigen.getPosicion(); i < paraderoDestino.getPosicion(); i++) {
                //Toast.makeText(getContext(), paraderos.get((int)i).getNombre(), Toast.LENGTH_SHORT).show();
                para.add(paraderos.get((int) i));
            }

        } else if (paraderoOrigen.getPosicion() < 0 && paraderoDestino.getPosicion() < 0) {
            for (double i = paraderoOrigen.getPosicion(); i < paraderoDestino.getPosicion(); i++) {
                para.add(paraderos.get((-1 * ((int) i)) - 1));
            }
        }


        //obteniendo el origen y destino de paraderos se compara los intermedios por nombre buses que necesita


        ArrayList<AuxiliarBus> auxiliar = new ArrayList();
        for (Bus b : buses) {
            String nombre = b.getNombre();
            int numero = 1;

            for (ParaderoBuscador p : para) {


                for (int i = 0; i < p.getBus().size(); i++) {
                    if (nombre.equals(p.getBus().get(i).getNombre())) {
                        auxiliar.add(new AuxiliarBus(numero++, p.getBus().get(i)));
                    }

                }
            }
        }

        Collections.sort(auxiliar, new Comparator<AuxiliarBus>() {
            @Override
            public int compare(AuxiliarBus o1, AuxiliarBus o2) {
                return new Integer(o1.getContador()).compareTo(new Integer(o2.getContador()));
            }
        });
        ArrayList<AuxiliarBus> a = auxiliar;
        ArrayList<AuxiliarBus> b = auxiliar;


        for (int i = 0; i < auxiliar.size(); i++) {


            for (int j = 0; j < a.size(); j++) {

                if (auxiliar.get(i).getBus().getNombre().equals(a.get(j).getBus().getNombre()) && (auxiliar.get(i).getContador() < a.get(j).getContador())) {
                    b.remove(i);

                }
            }

        }


        ArrayList<Bus> bus = new ArrayList<>();
        for (AuxiliarBus ax : b) {
            bus.add(ax.getBus());
            //Toast.makeText(getContext(), ax.getBus().getNombre(), Toast.LENGTH_SHORT).show();
        }

        return bus;

    }

    private ArrayList<Bus> filtroBusMenosParadas(ArrayList<Bus> buses, ParaderoBuscador paraderoDestino) {


        ArrayList<ParaderoBuscador> para = new ArrayList();

        //filtra los paraderos entre el origen y el destino devuelve un array con paraderos intermedios
        //for (Bus bus :buses) {
        //Toast.makeText(getContext(), "el bus: "+bus.getNombre()+ " para en ", Toast.LENGTH_SHORT).show();
        for (double i = paraderoOrigen.getPosicion(); i < paraderoDestino.getPosicion(); i++) {
            //Toast.makeText(getContext(), paraderos.get(i).getNombre(), Toast.LENGTH_SHORT).show();
            para.add(paraderos.get((int) i));
            // }
        }


        //obteniendo el origen y destino de paraderos se compara los intermedios por nombre buses que necesita

        // Object[][] cadena=new Object[3][para.size()];

        ArrayList<AuxiliarBus> auxiliar = new ArrayList();
        for (Bus b : buses) {
            String nombre = b.getNombre();
            int numero = 1;
            //Toast.makeText(getContext(), "evaluando bus: "+nombre, Toast.LENGTH_SHORT).show();
            for (ParaderoBuscador p : para) {
                //Toast.makeText(getContext(), "evaluando paraderos: "+p.getNombre(), Toast.LENGTH_SHORT).show();

                for (int i = 0; i < p.getBus().size(); i++) {
                    if (nombre.equals(p.getBus().get(i).getNombre())) {
                        auxiliar.add(new AuxiliarBus(numero++, p.getBus().get(i)));
                    }

                }
            }
        }

        Collections.sort(auxiliar, new Comparator<AuxiliarBus>() {
            @Override
            public int compare(AuxiliarBus o1, AuxiliarBus o2) {
                return new Integer(o1.getContador()).compareTo(new Integer(o2.getContador()));
            }
        });
        ArrayList<AuxiliarBus> a = auxiliar;
        ArrayList<AuxiliarBus> b = auxiliar;


        for (int i = 0; i < auxiliar.size(); i++) {
            //Toast.makeText(getContext(),auxiliar.get(i).getBus().getNombre(), Toast.LENGTH_SHORT).show();

            for (int j = 0; j < a.size(); j++) {

                if (auxiliar.get(i).getBus().getNombre().equals(a.get(j).getBus().getNombre()) && (auxiliar.get(i).getContador() < a.get(j).getContador())) {
                    b.remove(i);
                    //Toast.makeText(getContext(), "entre", Toast.LENGTH_SHORT).show();
                }
            }

        }


        ArrayList<Bus> bus = new ArrayList<>();
        for (AuxiliarBus ax : b) {
            bus.add(ax.getBus());
        }
        return bus;
    }


    private ArrayList<String> descomponerPalabra(String palabra) {
        String[] palabras = palabra.split(",");
        ArrayList<String> palabrasClaves = new ArrayList();
        for (String palabrasc : palabras) {
            String sinEspacio = palabrasc.trim();
            palabrasClaves.add(sinEspacio.toLowerCase());
        }
        return palabrasClaves;
    }

    @Override
    public void onClick(View v) {
        if (verificarInternet()) {

            if (buscadorParaderoDestino(autoCompletador.getText().toString())) {
                interstitialAd.loadAd();
                determinarRuta();
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(new AdaptadorReciclerViewRuta(getContext(), obtenerrutas()));
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(botonIr.getWindowToken(), 0);
                mMap.clear();
                ArrayList<ParaderoBuscador> paraderoB = new ArrayList();
                paraderoB.add(paraderoOrigen);
                for (RutaBusParadero s : obtenerrutas()) {
                    paraderoB.add(s.getParaderoBuscador());
                }
                paraderoB.add(paraderoDestino);
                mostrarRutaEnMapa(paraderoB);
            } else {
                Toast.makeText(getContext(), "No hay paraderos cercanos", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getContext(), "No hay conexion a internet", Toast.LENGTH_SHORT).show();
        }
    }

    public void mostrarRutaEnMapa(ArrayList<ParaderoBuscador> paradas) {

        for (ParaderoBuscador p : paradas) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(p.getLatitud(), p.getLongitud())).title(" " + p.getNombre()).snippet(p.getDescripcion()).icon(BitmapDescriptorFactory.fromResource(R.drawable.parada_de_autobus)));

        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paraderos();
        GpsUtil gps = new GpsUtil(getContext(), getActivity());
        apiClient = gps.Inicializaapi(getActivity());
        enableLocationUpdates();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastLocation =
                LocationServices.FusedLocationApi.getLastLocation(apiClient);

        updateUI(lastLocation);




        /*try {
        if (apiClient == null || !apiClient.isConnected()) {

                apiClient = new GoogleApiClient.Builder(getContext())
                        .enableAutoManage(getActivity(), this)
                        .addConnectionCallbacks(this)
                        .addApi(LocationServices.API)
                        .build();


            }
            enableLocationUpdates();
            }

            catch (Exception e) {
            e.printStackTrace();

        }*/
    }
    @Override
    public void onStart() {
        super.onStart();
        if (apiClient != null)
            apiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (apiClient != null && apiClient.isConnected()) {
            apiClient.stopAutoManage(getActivity());
            apiClient.disconnect();
        }
    }



    //Localizacion


    public Location getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(Location localizacion) {
        this.localizacion = localizacion;
    }

    private void updateUI(Location loc) {
        if (loc != null) {
           // Toast.makeText(getContext(), "Latitud"+loc.getLatitude()+"Longitud"+loc.getLongitude(), Toast.LENGTH_SHORT).show();
            setLocalizacion(loc);
            if(verificarInternet()){
                paraderoCercano();
            }
            else {
                Snackbar.make(getView(),"Conectese a internet para determinar paradero cercano",Snackbar.LENGTH_LONG).setAction("Abrir ajustes", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                }).show();
            }




        } else {
            Toast.makeText(getContext(), "Verifique su conexion a internet", Toast.LENGTH_SHORT).show();
        }
    }
    public Boolean verificarInternet() {
        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
/*
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getContext(), "Error al conectarse a los servicios de google", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {

            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient);

            updateUI(lastLocation);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
*/

    private void enableLocationUpdates() {

        locRequest = new LocationRequest();
        locRequest.setInterval(3000);
        locRequest.setFastestInterval(2000);
        locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest locSettingsRequest =
                new LocationSettingsRequest.Builder()
                        .addLocationRequest(locRequest)
                        .build();

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        apiClient, locSettingsRequest);

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        //Toast.makeText(MapsActivity.this, "Configuracion correcta", Toast.LENGTH_SHORT).show();
                        startLocationUpdates();
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            Toast.makeText(getContext(), "Active GPS para ubicar paraderos cercanos", Toast.LENGTH_SHORT).show();
                            status.startResolutionForResult(getActivity(), PETICION_CONFIG_UBICACION);
                        } catch (IntentSender.SendIntentException e) {
                            Toast.makeText(getContext(), "Error al intentar solucionar configuración de ubicación", Toast.LENGTH_SHORT).show();

                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Toast.makeText(getContext(), "No se puede cumplir la configuración de ubicación necesaria", Toast.LENGTH_SHORT).show();


                        break;
                }
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PETICION_CONFIG_UBICACION:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getContext(), "El usuario no ha realizado los cambios de configuración necesarios", Toast.LENGTH_SHORT).show();
                        break;
                }

                break;
        }
    }
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Ojo: estamos suponiendo que ya tenemos concedido el permiso.
            //Sería recomendable implementar la posible petición en caso de no tenerlo.
            //Toast.makeText(this, "Inicio de recepción de ubicaciones", Toast.LENGTH_SHORT).show();

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    apiClient, locRequest, this);
            mMap.setMyLocationEnabled(true);

        }
    }

    @Override
    public void onLocationChanged(Location location) {

        //Toast.makeText(this, "Recibiendo localizacion", Toast.LENGTH_SHORT).show();

        //Mostramos la nueva ubicación recibida
        updateUI(location);
    }

    @Override
    public void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }

        super.onDestroy();
        if (apiClient != null && apiClient.isConnected()) {
            apiClient.stopAutoManage(getActivity());
            apiClient.disconnect();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try{
            MapsInitializer.initialize(getContext());
        }catch (Exception e){

        }
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraPosition cameraPosition;
        cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(10.4027901, -75.5146382))
                .zoom(11)
                .bearing(0)
                .tilt(0)
                .build();
        CameraUpdate camara = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.animateCamera(camara);

        mMap.setMinZoomPreference(10f);
        mMap.setMaxZoomPreference(18f);
        agregarLimitesMapa();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {
            mMap.setMyLocationEnabled(true);
        }
        catch (Exception e){

        }
    }

    public void agregarLimitesMapa() {
        LatLngBounds Cartagena = new LatLngBounds(
                //10.4027901, -75.5156382
                new LatLng(10.3027, -75.6156), new LatLng(10.6627, -75.4556));

// Constrain the camera target to the Adelaide bounds.
        mMap.setLatLngBoundsForCameraTarget(Cartagena);

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView=(MapView) vista.findViewById(R.id.mapaRuta);
        if(mapView!=null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    public void paraderoCercano(){
        DecimalFormat df = new DecimalFormat("#.00");

        String latitudMiPosicion=df.format(getLocalizacion().getLatitude());
        String longitudMiPosicion=df.format(getLocalizacion().getLongitude());

        ArrayList<ParaderoBuscador>paraderosOrigen=new ArrayList();
        for (ParaderoBuscador p:datosParadero()) {
            String latitudParadero=df.format(p.getLatitud());
            String longitudParadero=df.format(p.getLongitud());
            if(latitudMiPosicion.equals(latitudParadero)&&longitudMiPosicion.equals(longitudParadero)){
               // Toast.makeText(getContext(), p.getNombre(), Toast.LENGTH_SHORT).show();
                paraderosOrigen.add(p);
            }
        }
        for (ParaderoBuscador p:datosParaderoSegundario()) {
            String latitudParadero=df.format(p.getLatitud());
            String longitudParadero=df.format(p.getLongitud());
            if(latitudMiPosicion.equals(latitudParadero)&&longitudMiPosicion.equals(longitudParadero)){
                //Toast.makeText(getContext(), p.getNombre(), Toast.LENGTH_SHORT).show();
                paraderosOrigen.add(p);
            }
        }
        paraderoOrigen=paraderosOrigen.get(0);
        tarea t=new tarea(paraderosOrigen);
        t.execute();
        if(distancias!=null&&distancias.size()>0){
            double contador=distancias.get(0).getDistancia();
            ParaderoDistancia para;
            for (ParaderoDistancia p:distancias) {

                if(p.getDistancia()<=contador){
                    paraderoOrigen=p.getParaderoBuscador();
                    //Toast.makeText(getContext(), p.getParaderoBuscador().getNombre(), Toast.LENGTH_SHORT).show();
                }

            }
        }



    }
    private class tarea extends AsyncTask<Void,Void,Void> {
        private ArrayList<ParaderoBuscador>para;

        public tarea(ArrayList<ParaderoBuscador>paradero){
            para=paradero;
        }

        @Override
        protected Void doInBackground(Void... params) {

            distancias=new ArrayList();
            for (final ParaderoBuscador p:para) {
                //Log.d("el",p.getNombre());
                final AdaptadorEnpointGoogle conexion = new AdaptadorEnpointGoogle();
                Gson gson = conexion.costruyeJsonDeserializadorDistanciaCorta();
                Endpoin endpoin = conexion.establecerConexionGoogleMaps(gson);
                final Call<RespuestaRutaCorta>respuesta=endpoin.getubicacionCorta((String.valueOf(getLocalizacion().getLatitude()) + "," + String.valueOf(getLocalizacion().getLongitude())),
                        String.valueOf(p.getLatitud()) + "," + String.valueOf(p.getLongitud()), token, "walking");
                respuesta.enqueue(new Callback<RespuestaRutaCorta>() {
                    @Override
                    public void onResponse(Call<RespuestaRutaCorta> call, Response<RespuestaRutaCorta> response) {
                        RespuestaRutaCorta respues=response.body();
                        distancias.add(new ParaderoDistancia(respues.getDistancia(),p));
                       // Toast.makeText(getContext(),"el paradero "+p.getNombre()+" "+String.valueOf(respues.getDistancia()), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(Call<RespuestaRutaCorta> call, Throwable t) {
                        Toast.makeText(getContext(),"Error al conectarse al servidor, intente mas tarde", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


}