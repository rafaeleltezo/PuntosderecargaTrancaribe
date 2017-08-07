package com.app.master.puntosderecargatrancaribe.Modelo.RestApi.ModeloBuscador;

/**
 * Created by Rafael p on 4/8/2017.
 */

public class Bus {

    private String nombre,descripcion;

    public Bus() {
    }

    public Bus(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
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
}
