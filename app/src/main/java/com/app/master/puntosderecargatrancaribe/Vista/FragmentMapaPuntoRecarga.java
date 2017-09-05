package com.app.master.puntosderecargatrancaribe.Vista;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.app.master.puntosderecargatrancaribe.MapsActivity;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.Coordenadas;
import com.app.master.puntosderecargatrancaribe.Presentador.GpsUtil;
import com.app.master.puntosderecargatrancaribe.Presentador.PresentadorMainActivity;
import com.app.master.puntosderecargatrancaribe.Presentador.iPresentadorMainActivity;
import com.app.master.puntosderecargatrancaribe.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.zip.Inflater;

import static com.google.ads.AdRequest.LOGTAG;

/**
 * Created by Rafael p on 2/8/2017.
 */

public class FragmentMapaPuntoRecarga extends Fragment implements iMapsActivity, OnMapReadyCallback,
        /*GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,*/ LocationListener,
        View.OnClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private GoogleApiClient apiClient;
    private int PETICION_PERMISO_LOCALIZACION = 12;
    private int PETICION_CONFIG_UBICACION = 23;
    private LocationRequest locRequest;
    private Location lastLocation;
    private LatLng locationMarcador;
    private Location location;
    private iPresentadorMainActivity presentador;
    private FloatingActionButton boton, botonLimpiar;
    private Button rutaCercana;
    private Polyline polyline;
    private AdView adView;
    private AdRequest adRequest;
    private MapView mapView;
    private View vista;
    private GpsUtil gps;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            presentador = new PresentadorMainActivity(getActivity(), this);
            gps = new GpsUtil(getContext(), getActivity());
            apiClient = gps.Inicializaapi(getActivity());
            actuliazarUbicacion();
            enableLocationUpdates();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        /*try {
        presentador = new PresentadorMainActivity(getActivity(), this);
        if (apiClient == null || !apiClient.isConnected()) {

                apiClient = new GoogleApiClient.Builder(getContext())
                        .enableAutoManage(getActivity(), this)
                        .addConnectionCallbacks(this)
                        .addApi(LocationServices.API)
                        .build();



            }
            enableLocationUpdates();
            }catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

        }
*/

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (apiClient != null && apiClient.isConnected()) {
            apiClient.stopAutoManage(getActivity());
            apiClient.disconnect();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.maparecarga, container, false);
        boton = (FloatingActionButton) vista.findViewById(R.id.botonNormalFragment);
        rutaCercana = (Button) vista.findViewById(R.id.rutaCercanaFragment);
        botonLimpiar = (FloatingActionButton) vista.findViewById(R.id.botonLimpiarFragment);
        botonLimpiar.setVisibility(View.INVISIBLE);
        botonLimpiar.setOnClickListener(this);
        boton.setOnClickListener(this);
        rutaCercana.setOnClickListener(this);
        boton.setVisibility(View.INVISIBLE);
        //Agregarndo Publicidad
        adView = (AdView) vista.findViewById(R.id.adViewFragment);
        adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        return vista;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) vista.findViewById(R.id.mapa);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
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

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void dibujarpolyline(ArrayList<Coordenadas> coordenadas) {

        PolylineOptions linea = new PolylineOptions();
        for (Coordenadas puntoCoordenadas : coordenadas) {
            linea.add(new LatLng(puntoCoordenadas.getLatitud(), puntoCoordenadas.getLongitud()));
        }
        linea.color(Color.RED).geodesic(true);
        polyline = mMap.addPolyline(linea);
        botonLimpiar.setVisibility(View.VISIBLE);
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

    @Override
    public void moverCamaraMapa(CameraPosition cameraPosition) {
        CameraUpdate camara = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.animateCamera(camara);
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
    public void AgregarPuntosRecarga(double latitud, double longitud, String nombre, String descripcion) {
        try {
            Marker marcador = mMap.addMarker(new MarkerOptions().position(new LatLng(latitud, longitud)).title(nombre).snippet(descripcion));
            marcador.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.punto_recarga));
        } catch (NullPointerException e) {
            presentador.agregarPuntoRecarga();
        }


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
                //presentador.agregarPuntoRecarga();
                @SuppressWarnings("MissingPermission")
                Location lastLocation =
                        LocationServices.FusedLocationApi.getLastLocation(apiClient);

                updateUI(lastLocation);
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
                mMap.setMyLocationEnabled(true);


            } else {
                //Permiso denegado:
                //Deberíamos deshabilitar toda la funcionalidad relativa a la localización.

                Toast.makeText(getContext(), "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void inicarServicioGooglePlay(GoogleApiClient api) {

    }

    public void updateUI(Location loc) {
        if (loc != null) {
            setLocation(loc);
            // Toast.makeText(this, "Latitud: " + String.valueOf(loc.getLatitude()), Toast.LENGTH_SHORT).show();
            // Toast.makeText(this, "Latitud: " + String.valueOf(loc.getLongitude()), Toast.LENGTH_SHORT).show();
            LatLng posicion = new LatLng(loc.getLatitude(), loc.getLongitude());


        } else {
            Toast.makeText(getContext(), "latitud desconocida", Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "longitud desconocida", Toast.LENGTH_SHORT).show();
        }
    }
    private void actuliazarUbicacion(){
        Location loca=GpsUtil.getLocation();
        updateUI(loca);
        try {
            CameraPosition cameraPosition;
            cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(loca.getLatitude(), loca.getLongitude()))
                    .zoom(14)
                    .bearing(0)
                    .tilt(0)
                    .build();
            CameraUpdate camara = CameraUpdateFactory.newCameraPosition(cameraPosition);
            mMap.animateCamera(camara);
        }catch (Exception e){
            Log.d("Error de latitud","Null point");
        }
    }

    /*
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Toast.makeText(getContext(), "Error al conectarse a los servicios de google play", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            // Toast.makeText(this, "Conexion Exitosa", Toast.LENGTH_SHORT).show();

            if (ActivityCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PETICION_PERMISO_LOCALIZACION);
            } else {

                lastLocation =LocationServices.FusedLocationApi.getLastLocation(apiClient);
                try {
                    CameraPosition cameraPosition;
                    cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                            .zoom(14)
                            .bearing(0)
                            .tilt(0)
                            .build();
                    CameraUpdate camara = CameraUpdateFactory.newCameraPosition(cameraPosition);
                    mMap.animateCamera(camara);
                }catch (Exception e){
                    Log.d("Error de latitud","Null point");
                }
                updateUI(lastLocation);
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            Toast.makeText(getContext(), "Servicios de google play suspendidos", Toast.LENGTH_SHORT).show();
        }
    */
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

                        //Toast.makeText(MapsActivity.this, "Configuracion correcta", Toast.LENGTH_SHORT).show();
                        startLocationUpdates();
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            Log.i(LOGTAG, "Se requiere actuación del usuario");
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
        //mMap.setMyLocationEnabled(true);
        updateUI(location);

    }


    @Override
    public void onClick(View v) {
        if(v.getId()==boton.getId()){
            presentador.obtenerRutaMapa();
        }
        if(v.getId()==botonLimpiar.getId()){
            mMap.clear();
            presentador.agregarPuntoRecarga();
            Toast.makeText(getContext(), "Eliminanda", Toast.LENGTH_SHORT).show();
            botonLimpiar.setVisibility(View.INVISIBLE);
        }
        if(v.getId()==rutaCercana.getId()){
            presentador.dibujarRutaCortaMapa();

        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        setLocationMarcador(marker.getPosition());
        marker.showInfoWindow();
        boton.setVisibility(View.VISIBLE);
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        boton.setVisibility(View.INVISIBLE);
    }
}
