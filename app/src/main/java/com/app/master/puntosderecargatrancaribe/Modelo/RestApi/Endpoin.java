package com.app.master.puntosderecargatrancaribe.Modelo.RestApi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Rafael p on 20/7/2017.
 */

public interface Endpoin {

    @GET("/maps/api/directions/json")
    public Call<RespuestaCoordenadas> getUbicacion(@Query("origin") String origin, @Query("destination")
            String destination, @Query("key") String key, @Query("mode") String modoViaje);

    @GET("/maps/api/directions/json")
    public Call<RespuestaRutaCorta> getubicacionCorta(@Query("origin") String origin, @Query("destination")
            String destination, @Query("key") String key, @Query("mode") String modoViaje);
}
