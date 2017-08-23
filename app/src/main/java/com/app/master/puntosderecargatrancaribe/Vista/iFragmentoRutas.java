package com.app.master.puntosderecargatrancaribe.Vista;

import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.ModeloBuscador.RutaBusParadero;

import java.util.ArrayList;

/**
 * Created by Rafael p on 21/8/2017.
 */

public interface iFragmentoRutas {
    public ArrayList<RutaBusParadero> obtenerrutas();
    public void buscadorParaderoDestino(String destino);
}
