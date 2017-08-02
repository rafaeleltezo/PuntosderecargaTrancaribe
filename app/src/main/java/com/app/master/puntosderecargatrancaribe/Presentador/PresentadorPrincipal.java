package com.app.master.puntosderecargatrancaribe.Presentador;

import android.content.Context;

import com.app.master.puntosderecargatrancaribe.Vista.iPrincipal;

/**
 * Created by Rafael p on 2/8/2017.
 */

public class PresentadorPrincipal implements iPresentadorPrincipal {
    private Context context;
    private iPrincipal actividad;

    public PresentadorPrincipal(Context contex,iPrincipal actividad){
        this.context=contex;
        this.actividad=actividad;
    }


    @Override
    public void iniciarViewPager() {
        actividad.establecerPagerPrincipal(actividad.crearAdaptador(actividad.getFragmentos()));
    }
}
