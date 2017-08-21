package com.app.master.puntosderecargatrancaribe.Modelo.RestApi.ModeloBuscador;

/**
 * Created by Rafael p on 21/8/2017.
 */

public class RutaBusParadero {
    private ParaderoBuscador paraderoBuscador;
    private String nombreBus;


    public RutaBusParadero() {
    }

    public RutaBusParadero(ParaderoBuscador paraderoBuscador, String nombreBus) {
        this.paraderoBuscador = paraderoBuscador;
        this.nombreBus = nombreBus;
    }

    public ParaderoBuscador getParaderoBuscador() {
        return paraderoBuscador;
    }

    public void setParaderoBuscador(ParaderoBuscador paraderoBuscador) {
        this.paraderoBuscador = paraderoBuscador;
    }

    public String getNombreBus() {
        return nombreBus;
    }

    public void setNombreBus(String nombreBus) {
        this.nombreBus = nombreBus;
    }
}
