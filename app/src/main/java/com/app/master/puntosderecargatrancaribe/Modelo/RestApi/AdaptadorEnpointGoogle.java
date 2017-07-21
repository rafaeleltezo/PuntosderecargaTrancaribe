package com.app.master.puntosderecargatrancaribe.Modelo.RestApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rafael p on 20/7/2017.
 */

public class AdaptadorEnpointGoogle {

    public Endpoin establecerConexionGoogleMaps(Gson gson){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(Endpoin.class);
    }

    public Gson construyeJsonDeserializador(){
        GsonBuilder gsonBuilder=new GsonBuilder();
        gsonBuilder.registerTypeAdapter(RespuestaCoordenadas.class,new CoordenadasDeserializador());
        return gsonBuilder.create();
    }
}
