package com.app.master.puntosderecargatrancaribe.Vista;

import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.app.master.puntosderecargatrancaribe.Presentador.PresentadorPrincipal;
import com.app.master.puntosderecargatrancaribe.Presentador.iPresentadorPrincipal;
import com.app.master.puntosderecargatrancaribe.R;
import com.app.master.puntosderecargatrancaribe.Vista.Adaptadores.AdaptadorViewPagerPrincipal;

import java.util.ArrayList;

public class Principal extends AppCompatActivity implements iPrincipal{

    private ViewPager viewPager;
    private iPresentadorPrincipal presentador;
    BottomNavigationView botonNavegacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        viewPager=(ViewPager)findViewById(R.id.viewpager);
        botonNavegacion=(BottomNavigationView)findViewById(R.id.botonNavegancion);
        presentador=new PresentadorPrincipal(this,this);
        presentador.iniciarViewPager();
    }

    @Override
    public ArrayList<Fragment> getFragmentos(){
        ArrayList<Fragment> fragments=new ArrayList();
        fragments.add(new FragmentMapaPuntoRecarga());
        return fragments;
    }

    @Override
    public AdaptadorViewPagerPrincipal crearAdaptador(ArrayList<Fragment> fragments){
        return new AdaptadorViewPagerPrincipal(getSupportFragmentManager(),fragments);
    }

    @Override
    public void establecerPagerPrincipal(AdaptadorViewPagerPrincipal adaptador){
        viewPager.setAdapter(adaptador);
    }

}
