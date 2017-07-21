package com.app.master.puntosderecargatrancaribe.Modelo.RestApi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Rafael p on 20/7/2017.
 */
public class CoordenadasDeserializador implements JsonDeserializer<RespuestaCoordenadas> {


    @Override
    public RespuestaCoordenadas deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new Gson();
        RespuestaCoordenadas respuesta = gson.fromJson(json, RespuestaCoordenadas.class);
        JsonArray contactoResponData = json.getAsJsonObject().getAsJsonArray(ConstantesJsongoogleMaps.DATOS);
        respuesta.setCoordenadas(deserializador(contactoResponData));
        return respuesta;
    }

    private ArrayList<Coordenadas> deserializador(JsonArray jsonArray) {

        ArrayList<Coordenadas> coordenadas = new ArrayList();

        JsonObject arrayObjeto = jsonArray.get(0).getAsJsonObject();
        JsonArray arrayPasos = arrayObjeto.getAsJsonObject().getAsJsonArray(ConstantesJsongoogleMaps.PIERNAs);
        JsonObject arrayobjestopasos = arrayPasos.get(0).getAsJsonObject();

        //Distancia
        JsonObject distancia = arrayobjestopasos.getAsJsonObject(ConstantesJsongoogleMaps.DISTANCIA);
        double distaciaPasos = distancia.get(ConstantesJsongoogleMaps.DURACION).getAsDouble();

        //pasos

        JsonArray arrayPasosCoordenadas = arrayobjestopasos.getAsJsonObject().getAsJsonArray(ConstantesJsongoogleMaps.PASOS);


        for (int i = 0; i < arrayPasosCoordenadas.size(); i++) {

            JsonObject pasoActual = arrayPasosCoordenadas.get(i).getAsJsonObject();
            JsonObject ultimaLocalizacion = pasoActual.getAsJsonObject(ConstantesJsongoogleMaps.LOCALIZACION_FINAL);
            double latitud = ultimaLocalizacion.get(ConstantesJsongoogleMaps.LATITUD_DESTINO).getAsDouble();
            double longitud = ultimaLocalizacion.get(ConstantesJsongoogleMaps.LONGITUD_DESTINO).getAsDouble();
            coordenadas.add(new Coordenadas(latitud, longitud, distaciaPasos));
        }
        return coordenadas;
    }
}

