package com.app.master.puntosderecargatrancaribe.Modelo.RestApi;

/**
 * Created by Rafael p on 30/7/2017.
 */

public class RutaCorta {
    Paradero paradero;
    double distacia;

    public RutaCorta(Paradero paradero, double distacia) {
        this.paradero = paradero;
        this.distacia = distacia;
    }

    public RutaCorta() {
    }

    public Paradero getParadero() {
        return paradero;
    }

    public void setParadero(Paradero paradero) {
        this.paradero = paradero;
    }

    public double getDistacia() {
        return distacia;
    }

    public void setDistacia(double distacia) {
        this.distacia = distacia;
    }
}
