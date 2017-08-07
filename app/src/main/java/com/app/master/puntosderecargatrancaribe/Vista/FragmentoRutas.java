package com.app.master.puntosderecargatrancaribe.Vista;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.AuxiliarBus;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.ModeloBuscador.Bus;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.ModeloBuscador.ParaderoBuscador;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.Paradero;
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.RutaCorta;
import com.app.master.puntosderecargatrancaribe.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class FragmentoRutas extends Fragment {

    private View vista;
    private ParaderoBuscador paraderoOrigen;
    private ParaderoBuscador paraderoDestino;
    private ArrayList<ParaderoBuscador> paraderos;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        vista=inflater.inflate(R.layout.fragment_rutas, container, false);

        ArrayList<Bus> busesCastellana=new ArrayList();
        busesCastellana.add(new Bus("xt103","Variante"));
        busesCastellana.add(new Bus("xt102","Bocagrande"));
       //  busesCastellana.add(new Bus("xt101","Todas las paradas"));
        // busesCastellana.add(new Bus("xt103","Crespo"));
        paraderoOrigen=new ParaderoBuscador(busesCastellana,"Castellana","Paradero de la castellana","castellana,Exito cartagena,",0,0,1);
        buscadorParaderoDestino("crespo");
        rutaCercana(paraderoOrigen);
        return vista;
    }

    //datos entrantes
    public ArrayList<ParaderoBuscador> datosParadero(){
        paraderos=new ArrayList();
        ArrayList<Bus> busesCastellana=new ArrayList();

        //buses de la castellana
        busesCastellana.add(new Bus("xt102","Variante"));
        busesCastellana.add(new Bus("xt103","Bocagrande"));
        //busesCastellana.add(new Bus("xt101","Todas las paradas"));
        paraderos.add(new ParaderoBuscador(busesCastellana,"Castellana","Paradero de la castellana","castellana,Exito cartagena,",0,0,1));

        //buses de cuatro vientos

        ArrayList<Bus> busesCuatroVientos=new ArrayList();
        busesCuatroVientos.add(new Bus("xt100","Bocagrande"));
        busesCuatroVientos.add(new Bus("xt103","Crespo"));
        paraderos.add(new ParaderoBuscador(busesCuatroVientos,"cuatro vientos","paradero Cuatro viento","cuatro viento,frente sena,sena",0,9,2));

        //paradero inventado
        ArrayList<Bus> busesInventado=new ArrayList();
        busesInventado.add(new Bus("xt100","Bocagrande"));
        busesInventado.add(new Bus("xt103","Crespo"));
        paraderos.add(new ParaderoBuscador(busesInventado,"cuatro vientos","paradero Cuatro viento","cuatro viento,frente sena,sena",0,9,3));

        //buses de crespo
        ArrayList<Bus> busesCuatroCrespo=new ArrayList();
        busesCuatroCrespo.add(new Bus("xt102","Bocagrande"));
        busesCuatroCrespo.add(new Bus("xt103","Crespo"));
        paraderos.add(new ParaderoBuscador(busesCuatroCrespo,"crespo","paradero crespo","crespo,prueba",0,9,4));

        return paraderos;
    }


    public void buscadorParaderoDestino(String destino){

        //buscador de paradero destino con el nombre solicitado por el usuario

        for (ParaderoBuscador paradero:datosParadero()) {
            for (String palabras:descomponerPalabra(paradero.getPalabrasClaves())) {
                if (palabras.equals(destino)) {
                    //Toast.makeText(getContext(), "Encontrado "+"Nombre del paradero: "+paradero.getNombre(), Toast.LENGTH_SHORT).show();
                    this.paraderoDestino=paradero;
                    //Toast.makeText(getContext(), paraderoDestino.getPalabrasClaves(), Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }

    public ArrayList<Bus> obtenerBusesParaAbordar(ParaderoBuscador origen){
        ArrayList<Bus>busesParada=new ArrayList();

        for (Bus busorigen:origen.getBus()) {
            //Toast.makeText(getContext(), "Evlauando "+busorigen.getNombre()+ " con", Toast.LENGTH_SHORT).show();
            for (int i = 0; i <paraderoDestino.getBus().size() ; i++) {
                if (busorigen.getNombre().equals(paraderoDestino.getBus().get(i).getNombre())) {
                  //  Toast.makeText(getContext(), paraderoDestino.getBus().get(i).getNombre(), Toast.LENGTH_SHORT).show();
                    busesParada.add(paraderoDestino.getBus().get(i));
                }
                //  Toast.makeText(getContext(), paraderoDestino.getBus().get(i).getNombre(), Toast.LENGTH_SHORT).show();
            }
        }
        return busesParada;
    }

    public void rutaCercana(ParaderoBuscador origen){


        if(obtenerBusesParaAbordar(origen).size()==1){
            Toast.makeText(getContext(), "Aborda el " + obtenerBusesParaAbordar(origen).get(0).getNombre(), Toast.LENGTH_SHORT).show();

        }else if(obtenerBusesParaAbordar(origen).size()>1){
            Bus bus=filtroBusMenosParadas(obtenerBusesParaAbordar(origen));
            Toast.makeText(getContext(),"Aborda el: "+bus.getNombre(), Toast.LENGTH_SHORT).show();
        }

    }

    private Bus filtroBusMenosParadas(ArrayList<Bus> buses){




        ArrayList<ParaderoBuscador> para=new ArrayList();

        //filtra los paraderos entre el origen y el destino devuelve un array con paraderos intermedios
        //for (Bus bus :buses) {
            //Toast.makeText(getContext(), "el bus: "+bus.getNombre()+ " para en ", Toast.LENGTH_SHORT).show();
            for (int i=paraderoOrigen.getPosicion();i<paraderoDestino.getPosicion();i++) {
                //Toast.makeText(getContext(), paraderos.get(i).getNombre(), Toast.LENGTH_SHORT).show();
                para.add(paraderos.get(i));
           // }
        }


        //obteniendo el origen y destino de paraderos se compara los intermedios por nombre buses que necesita

       // Object[][] cadena=new Object[3][para.size()];

        ArrayList<AuxiliarBus> auxiliar=new ArrayList();
        for (Bus b:buses) {
            String nombre=b.getNombre();
            int numero=1;
            //Toast.makeText(getContext(), "evaluando bus: "+nombre, Toast.LENGTH_SHORT).show();
                for (ParaderoBuscador p:para) {
                    //Toast.makeText(getContext(), "evaluando paraderos: "+p.getNombre(), Toast.LENGTH_SHORT).show();

                    for (int i = 0; i < p.getBus().size(); i++) {
                        if(nombre.equals(p.getBus().get(i).getNombre())){
                           // Toast.makeText(getContext(),"Encontrado: "+ p.getBus().get(i).getNombre(), Toast.LENGTH_SHORT).show();
                         /*   cadena[0][i]=p.getBus().get(i).getNombre();
                            cadena[1][i]=numero++;
                            cadena[2][i]=p.getBus().get(i);
                         */
                         auxiliar.add(new AuxiliarBus(numero++,p.getBus().get(i)));
                        }

                    }
            }
        }

        ArrayList<AuxiliarBus>a=auxiliar;
        ArrayList<AuxiliarBus>b=auxiliar;


        for (int i=0;i<auxiliar.size();i++) {
           //Toast.makeText(getContext(),auxiliar.get(i).getBus().getNombre(), Toast.LENGTH_SHORT).show();

               for (int j = 0; j < a.size(); j++) {

                   if (auxiliar.get(i).getBus().getNombre().equals(a.get(j).getBus().getNombre()) && (auxiliar.get(i).getContador() < a.get(j).getContador())) {
                       b.remove(i);
                       Toast.makeText(getContext(), "entre", Toast.LENGTH_SHORT).show();
                   }
               }

        }


        int posicion=b.get(0).getContador();
        Bus busMenosParadas=busMenosParadas=b.get(0).getBus();
        for (AuxiliarBus as :a) {

            if(as.getContador()<posicion){
                posicion=as.getContador();
                busMenosParadas=as.getBus();
            }
        }
        Toast.makeText(getContext(), busMenosParadas.getNombre(), Toast.LENGTH_SHORT).show();
        return busMenosParadas;



/*
        int posicion=(Integer) cadena[1][0];
        Bus busMenosParadas=busMenosParadas=null;
        Toast.makeText(getContext(), cadena[1][1].toString(), Toast.LENGTH_SHORT).show();


        for (int i = 0; i < cadena[1].length; i++) {


           // for (int j = 0; j <cadena[i].length ; j++) {

                //if(palabra.equals("null")){
                    //Toast.makeText(getContext(), palabra, Toast.LENGTH_SHORT).show();
                //}else {


                int n = (Integer) cadena[1][i];
                if (n <= posicion) {
                    posicion = n;

                    //busMenosParadas = (Bus) cadena[2][i];
                }

        }
*/
        //Toast.makeText(getContext(),auxiliar(auxiliar).getNombre(), Toast.LENGTH_SHORT).show();


    }



    private ArrayList<String> descomponerPalabra(String palabra){
        String[]palabras=palabra.split(",");
        ArrayList<String>palabrasClaves=new ArrayList();
        for (String palabrasc:palabras) {
            String sinEspacio=palabrasc.trim();
            palabrasClaves.add(sinEspacio.toLowerCase());
        }
        return palabrasClaves;
    }

}
