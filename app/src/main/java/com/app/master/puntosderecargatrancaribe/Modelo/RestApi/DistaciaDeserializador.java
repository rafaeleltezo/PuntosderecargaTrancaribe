package com.app.master.puntosderecargatrancaribe.Modelo.RestApi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by Rafael p on 30/7/2017.
 */

public class DistaciaDeserializador implements JsonDeserializer<RespuestaRutaCorta> {
    @Override
    public RespuestaRutaCorta deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson=new Gson();
        RespuestaRutaCorta respuesta=gson.fromJson(json,RespuestaRutaCorta.class);
        JsonArray jsonArray=json.getAsJsonObject().getAsJsonArray(ConstantesJsongoogleMaps.DATOS);
        respuesta.setDistancia(deserializador(jsonArray));
        return respuesta;
    }
    private double deserializador(JsonArray jsonArray){
        JsonObject arrayObjeto = jsonArray.get(0).getAsJsonObject();
        JsonArray arrayPasos = arrayObjeto.getAsJsonObject().getAsJsonArray(ConstantesJsongoogleMaps.PIERNAs);
        JsonObject arrayobjestopasos = arrayPasos.get(0).getAsJsonObject();

        //Distancia
        JsonObject distancia = arrayobjestopasos.getAsJsonObject(ConstantesJsongoogleMaps.DISTANCIA);
        double distaciaPasos = distancia.get(ConstantesJsongoogleMaps.DURACION).getAsDouble();
        return distaciaPasos;
    }
}
