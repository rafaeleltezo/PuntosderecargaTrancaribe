package com.app.master.puntosderecargatrancaribe.Modelo.RestApi;

import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.ModeloBuscador.Bus;

/**
 * Created by Rafael p on 7/8/2017.
 */

public class AuxiliarBus {
    int contador;
    Bus bus;

    public AuxiliarBus(int contador, Bus bus) {
        this.contador = contador;
        this.bus = bus;

    }

    public AuxiliarBus() {
    }

    public int getContador() {
        return contador;
    }

    public void setContador(int contador) {
        this.contador = contador;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }
}
