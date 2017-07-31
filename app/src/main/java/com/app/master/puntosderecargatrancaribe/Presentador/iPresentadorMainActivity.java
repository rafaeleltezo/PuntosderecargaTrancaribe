package com.app.master.puntosderecargatrancaribe.Presentador;

import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.Paradero;

import java.util.ArrayList;

/**
 * Created by Rafael p on 20/7/2017.
 */

public interface iPresentadorMainActivity {
    public void agregarPuntoRecarga();
    public void agregarLimitesMapa();
    public void obtenerRutaMapa();
    public void obtenerRutaMapa(Paradero paradero);
    public void obtenerutaCercana();
    public void dibujarRutaCortaMapa();
}
