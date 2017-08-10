package com.app.master.puntosderecargatrancaribe.Modelo.RestApi.ModeloBuscador;

import java.util.ArrayList;

/**
 * Created by Rafael p on 4/8/2017.
 */

public class ParaderoBuscador {

    private double posicion;
    private ArrayList<Bus> bus;
    private String nombre,descripcion,palabrasClaves;
    private double latitud,longitud;

    public ParaderoBuscador() {
    }

    public ParaderoBuscador(ArrayList<Bus> bus, String nombre, String descripcion, String palabrasClaves, double latitud, double longitud,double posicion) {
        this.bus = bus;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.palabrasClaves = palabrasClaves;
        this.latitud = latitud;
        this.longitud = longitud;
        this.posicion=posicion;
    }

    public double getPosicion() {
        return posicion;
    }

    public void setPosicion(double posicion) {
        this.posicion = posicion;
    }

    public ArrayList<Bus> getBus() {
        return bus;
    }

    public void setBus(ArrayList<Bus> bus) {
        this.bus = bus;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPalabrasClaves() {
        return palabrasClaves;
    }

    public void setPalabrasClaves(String palabrasClaves) {
        this.palabrasClaves = palabrasClaves;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}
