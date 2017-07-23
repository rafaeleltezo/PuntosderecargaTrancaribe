package com.app.master.puntosderecargatrancaribe.Vista;

import android.location.Location;

import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.Coordenadas;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by Rafael p on 20/7/2017.
 */

public interface iMapsActivity {
    public void AgregarPuntosRecarga(double latitud,double longitud,String nombre);
    public void establecerLimitesMapa();
    public LatLng getLocationMarcador();
    public Location getLocation();
    public void dibujarpolyline(ArrayList<Coordenadas> coordenadas);
    public Boolean verificarInternet();
}
