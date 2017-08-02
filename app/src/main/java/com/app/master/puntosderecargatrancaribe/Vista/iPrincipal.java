package com.app.master.puntosderecargatrancaribe.Vista;

import android.support.v4.app.Fragment;

import com.app.master.puntosderecargatrancaribe.Vista.Adaptadores.AdaptadorViewPagerPrincipal;

import java.util.ArrayList;

/**
 * Created by Rafael p on 2/8/2017.
 */

public interface iPrincipal {
    public ArrayList<Fragment> getFragmentos();
    public AdaptadorViewPagerPrincipal crearAdaptador(ArrayList<Fragment> fragments);
    public void establecerPagerPrincipal(AdaptadorViewPagerPrincipal adaptador);
}
