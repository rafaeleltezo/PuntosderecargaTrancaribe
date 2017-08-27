package com.app.master.puntosderecargatrancaribe.Modelo.RestApi.ModeloBuscador;

/**
 * Created by Rafael p on 26/8/2017.
 */

public class ParaderoDistancia {
    private double distancia;
    private ParaderoBuscador paraderoBuscador;

    public ParaderoDistancia() {
    }

    public ParaderoDistancia(double distancia, ParaderoBuscador paraderoBuscador) {
        this.distancia = distancia;
        this.paraderoBuscador = paraderoBuscador;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    public ParaderoBuscador getParaderoBuscador() {
        return paraderoBuscador;
    }

    public void setParaderoBuscador(ParaderoBuscador paraderoBuscador) {
        this.paraderoBuscador = paraderoBuscador;
    }
}
