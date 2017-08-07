package com.app.master.puntosderecargatrancaribe.Modelo.RestApi.ModeloBuscador;

import java.util.ArrayList;

/**
 * Created by Rafael p on 4/8/2017.
 */

public class Ruta {
    private ArrayList<ParaderoBuscador> rutas;

    public Ruta() {
    }

    public Ruta(ArrayList<ParaderoBuscador> rutas) {
        this.rutas = rutas;
    }

    public ArrayList<ParaderoBuscador> getRutas() {
        return rutas;
    }

    public void setRutas(ArrayList<ParaderoBuscador> rutas) {
        this.rutas = rutas;
    }
}
