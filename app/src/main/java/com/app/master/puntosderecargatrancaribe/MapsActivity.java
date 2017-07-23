package com.app.master.puntosderecargatrancaribe;

import android.app.ProgressDialog;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.Coordenadas;
import com.app.master.puntosderecargatrancaribe.Presentador.PresentadorMainActivity;
import com.app.master.puntosderecargatrancaribe.Presentador.iPresentadorMainActivity;
import com.app.master.puntosderecargatrancaribe.Vista.iMapsActivity;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import static com.google.ads.AdRequest.LOGTAG;

public class MapsActivity extends FragmentActivity implements iMapsActivity, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,LocationListener, View.OnClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private GoogleApiClient apiClient;
    private int PETICION_PERMISO_LOCALIZACION=1;
    private int PETICION_CONFIG_UBICACION=2;
    private  LocationRequest locRequest;
    private LatLng locationMarcador;
    private Location location;
    private iPresentadorMainActivity presentador;
    private FloatingActionButton boton;
    private Polyline polyline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        presentador=new PresentadorMainActivity(this,this);
        apiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        enableLocationUpdates();

        boton=(FloatingActionButton)findViewById(R.id.botonNormal);
        boton.setOnClickListener(this);
        boton.setVisibility(View.INVISIBLE);
        presentador.agregarPuntoRecarga();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraPosition cameraPosition;
             cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(10.4027901, -75.5146382))
                    .zoom(14)
                    .bearing(0)
                    .tilt(0)
                    .build();

        CameraUpdate camara = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.animateCamera(camara);

        mMap.setMinZoomPreference(14f);
        mMap.setMaxZoomPreference(18f);
        presentador.agregarLimitesMapa();
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void dibujarpolyline(ArrayList<Coordenadas> coordenadas) {

        PolylineOptions linea = new PolylineOptions();
        for (Coordenadas puntoCoordenadas:coordenadas) {
            linea.add(new LatLng(puntoCoordenadas.getLatitud(),puntoCoordenadas.getLongitud()));
        }
        linea.color(Color.RED).geodesic(true);

        polyline = mMap.addPolyline(linea);
    }

    @Override
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

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public LatLng getLocationMarcador() {
        return locationMarcador;
    }

    public void setLocationMarcador(LatLng locationMarcador) {
        this.locationMarcador = locationMarcador;
    }

    @Override
    public void AgregarPuntosRecarga(double latitud,double longitud,String nombre){
        Marker marcador=mMap.addMarker(new MarkerOptions().position(new LatLng(latitud, longitud)).title(nombre));
        marcador.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.punto_recarga));

    }

    @Override
    public void establecerLimitesMapa() {
        LatLngBounds Cartagena = new LatLngBounds(
                //10.4027901, -75.5156382
                new LatLng(10.3027, -75.6156), new LatLng(10.6627, -75.4556));

// Constrain the camera target to the Adelaide bounds.
        mMap.setLatLngBoundsForCameraTarget(Cartagena);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PETICION_PERMISO_LOCALIZACION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Permiso concedido
                presentador.agregarPuntoRecarga();
                @SuppressWarnings("MissingPermission")
                Location lastLocation =
                        LocationServices.FusedLocationApi.getLastLocation(apiClient);

                updateUI(lastLocation);

            } else {
                //Permiso denegado:
                //Deberíamos deshabilitar toda la funcionalidad relativa a la localización.

                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void inicarServicioGooglePlay(GoogleApiClient api) {

    }

    public void updateUI(Location loc) {
        if (loc != null) {
            setLocation(loc);
            //Toast.makeText(this, "Latitud: " + String.valueOf(loc.getLatitude()), Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, "Latitud: " + String.valueOf(loc.getLongitude()), Toast.LENGTH_SHORT).show();
            LatLng posicion = new LatLng(loc.getLatitude(), loc.getLongitude());


        } else {
            Toast.makeText(this, "latitud desconocida", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "longitud desconocida", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Error al conectarse a los servicios de google play", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
       // Toast.makeText(this, "Conexion Exitosa", Toast.LENGTH_SHORT).show();

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {

            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient);

            updateUI(lastLocation);
        }

        //android.Manifest.permission.ACCESS_FINE_LOCATION
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Servicios de google play suspendidos", Toast.LENGTH_SHORT).show();
    }

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

                        Toast.makeText(MapsActivity.this, "Configuracion correcta", Toast.LENGTH_SHORT).show();
                        startLocationUpdates();
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            Log.i(LOGTAG, "Se requiere actuación del usuario");
                            status.startResolutionForResult(MapsActivity.this, PETICION_CONFIG_UBICACION);
                        } catch (IntentSender.SendIntentException e) {
                            Toast.makeText(MapsActivity.this, "Error al intentar solucionar configuración de ubicación", Toast.LENGTH_SHORT).show();

                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Toast.makeText(MapsActivity.this, "No se puede cumplir la configuración de ubicación necesaria", Toast.LENGTH_SHORT).show();


                        break;
                }
            }
        });
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Ojo: estamos suponiendo que ya tenemos concedido el permiso.
            //Sería recomendable implementar la posible petición en caso de no tenerlo.
            Toast.makeText(this, "Inicio de recepción de ubicaciones", Toast.LENGTH_SHORT).show();

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    apiClient, locRequest, MapsActivity.this);
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
    public void onClick(View v) {
        if(v.getId()==boton.getId()){

            presentador.obtenerRutaMapa();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        setLocationMarcador(marker.getPosition());
        boton.setVisibility(View.VISIBLE);
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        boton.setVisibility(View.INVISIBLE);
    }
}

