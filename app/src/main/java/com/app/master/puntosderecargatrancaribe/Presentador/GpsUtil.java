package com.app.master.puntosderecargatrancaribe.Presentador;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

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

/**
 * Created by Rafael p on 4/9/2017.
 */

public class GpsUtil implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks/*,LocationListener*/ {
    private static final int PETICION_CONFIG_UBICACION =21 ;
    private static GoogleApiClient apiClient;
    private Context contexto;
    private Activity actividad;
    private LocationRequest locRequest;
    private static Location location;


    private static final int PETICION_PERMISO_LOCALIZACION = 2 ;


    public GpsUtil(Context contexto, Activity actividad) {
        this.contexto = contexto;
    }

    public static Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
    public GoogleApiClient Inicializaapi(FragmentActivity actividad) {
        try {
            if (apiClient == null || !apiClient.isConnected()) {

                apiClient = new GoogleApiClient.Builder(contexto)
                        .enableAutoManage(actividad, this)
                        .addConnectionCallbacks(this)
                        .addApi(LocationServices.API)
                        .build();


            }

        } catch (Exception e) {
            Toast.makeText(contexto, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
        return apiClient;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(contexto, "Error al conectarse al gps", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(contexto,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(actividad,
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
        Toast.makeText(contexto, "Conexion suspendida", Toast.LENGTH_SHORT).show();

    }

    public  void updateUI(Location loc) {
        setLocation(loc);
    }
    /*
    public void enableLocationUpdates() {

        locRequest = new LocationRequest();
        locRequest.setInterval(2000);
        locRequest.setFastestInterval(1000);
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

                        Toast.makeText(actividad, "conexion correcta", Toast.LENGTH_SHORT).show();
                        startLocationUpdates();
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            //Log.i(LOGTAG, "Se requiere actuación del usuario");
                            Toast.makeText(contexto, "se Requiere atencion usuario", Toast.LENGTH_SHORT).show();
                            status.startResolutionForResult(actividad, PETICION_CONFIG_UBICACION);
                        } catch (IntentSender.SendIntentException e) {
                            Toast.makeText(contexto, "Error al conectarse al gps", Toast.LENGTH_SHORT).show();
                            //Log.i(LOGTAG, "Error al intentar solucionar configuración de ubicación");
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        //Log.i(LOGTAG, "No se puede cumplir la configuración de ubicación necesaria");
                        //btnActualizar.setChecked(false);
                        break;
                }
            }
        });
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(actividad,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Ojo: estamos suponiendo que ya tenemos concedido el permiso.
            //Sería recomendable implementar la posible petición en caso de no tenerlo.
            //Toast.makeText(this, "Inicio de recepción de ubicaciones", Toast.LENGTH_SHORT).show();

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    apiClient, locRequest,this);
            //mMap.setMyLocationEnabled(true);

        }
    }


    @Override
    public void onLocationChanged(Location location) {
        updateUI(location);
    }
    */
}
