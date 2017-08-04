package com.app.master.puntosderecargatrancaribe.Vista;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.master.puntosderecargatrancaribe.R;


public class Rutas extends Fragment {

  private View vista;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        vista=inflater.inflate(R.layout.fragment_rutas, container, false);

        return vista;
    }

}
