package com.app.master.puntosderecargatrancaribe.Modelo.RestApi;

/**
 * Created by Rafael p on 20/7/2017.
 */
public class Paradero {
    private String nombre,descripcion;
    private double latitud,longitud;

    public Paradero() {
    }

    public Paradero(String nombre, double latitud, double longitud,String descripcion) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.descripcion=descripcion;
    }


    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
