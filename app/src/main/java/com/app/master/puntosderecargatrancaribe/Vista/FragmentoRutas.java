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
import java.util.Comparator;
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
        busesCastellana.add(new Bus("xt1093","Variante"));
        busesCastellana.add(new Bus("xt1062","Bocagrande"));
       //  busesCastellana.add(new Bus("xt101","Todas las paradas"));
        // busesCastellana.add(new Bus("xt103","Crespo"));
        paraderoOrigen=new ParaderoBuscador(busesCastellana,"Castellana","Paradero de la castellana","castellana,Exito cartagena,",0,0,1);
        buscadorParaderoDestino("crespo");
        rutaCercana(paraderoOrigen,paraderoDestino);
        return vista;
    }
    //datos entrantes
    public ArrayList<ParaderoBuscador> datosParadero(){
        paraderos=new ArrayList();
        ArrayList<Bus> busesCastellana=new ArrayList();
        //buses de la castellana
        busesCastellana.add(new Bus("xt1093","Variante"));
        busesCastellana.add(new Bus("xt1062","Bocagrande"));
        //busesCastellana.add(new Bus("xt101","Todas las paradas"));
        paraderos.add(new ParaderoBuscador(busesCastellana,"Castellana","Paradero de la castellana","castellana,Exito cartagena,",0,0,1));
        //buses de cuatro vientos
        ArrayList<Bus> busesCuatroVientos=new ArrayList();
        busesCuatroVientos.add(new Bus("xt109","Bocagrande"));
        busesCuatroVientos.add(new Bus("xt106","Crespo"));
        paraderos.add(new ParaderoBuscador(busesCuatroVientos,"Cuatro vientos","paradero Cuatro viento","cuatro viento,frente sena,sena",0,9,2));
        //paradero inventado
        ArrayList<Bus> busesInventado=new ArrayList();
        busesInventado.add(new Bus("xt1093","Bocagrande"));
        busesInventado.add(new Bus("xt102","Crespo"));
        paraderos.add(new ParaderoBuscador(busesInventado,"inventado","paradero inventado","inventado. viento,",0,9,3));

        ArrayList<Bus> busesInventado2=new ArrayList();
        busesInventado2.add(new Bus("xt1093","Bocagrande"));
        busesInventado2.add(new Bus("xt100","Crespo"));
        paraderos.add(new ParaderoBuscador(busesInventado2,"inventado2","paradero inventado2","inventado. viento,",0,9,4));
        //buses de crespo
        ArrayList<Bus> busesCuatroCrespo=new ArrayList();
        busesCuatroCrespo.add(new Bus("xt102","Bocagrande"));
        busesCuatroCrespo.add(new Bus("xt100","Crespo"));
        paraderos.add(new ParaderoBuscador(busesCuatroCrespo,"crespo","paradero crespo","crespo,prueba",0,9,5));
        return paraderos;

    }


    //Buscar el paradero de destino a donde se dirige

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

    public ArrayList<Bus> obtenerBusesParaAbordar(ParaderoBuscador origen,ParaderoBuscador destino){
        ArrayList<Bus>busesParada=new ArrayList();

        for (Bus busorigen:origen.getBus()) {
            //Toast.makeText(getContext(), "Evlauando "+busorigen.getNombre()+ " con", Toast.LENGTH_SHORT).show();
            for (int i = 0; i <destino.getBus().size() ; i++) {
                if (busorigen.getNombre().equals(destino.getBus().get(i).getNombre())) {
                  //  Toast.makeText(getContext(), paraderoDestino.getBus().get(i).getNombre(), Toast.LENGTH_SHORT).show();
                    busesParada.add(destino.getBus().get(i));
                }
                //  Toast.makeText(getContext(), paraderoDestino.getBus().get(i).getNombre(), Toast.LENGTH_SHORT).show();
            }
        }
        return busesParada;
    }

    public Boolean rutaCercana(ParaderoBuscador origen,ParaderoBuscador destino){

       ArrayList<Bus>buses=new ArrayList();

        if(obtenerBusesParaAbordar(origen,destino).size()==1){
            buses.add(obtenerBusesParaAbordar(origen,destino).get(0));
            imprimir(buses);
            //Toast.makeText(getContext(), "Aborda el " + obtenerBusesParaAbordar(origen,destino).get(0).getNombre(), Toast.LENGTH_SHORT).show();
            return true;
        }else if(obtenerBusesParaAbordar(origen,destino).size()>1){
            ArrayList<Bus>bus=filtroBusMenosParadas(obtenerBusesParaAbordar(origen,destino));
            if(bus.size()==1) {
                buses.add(bus.get(0));
                imprimir(buses);
                //Toast.makeText(getContext(), "Aborda: " + bus.get(0).getNombre(), Toast.LENGTH_SHORT).show();
                return true;
            }else if(bus.size()>1) {
                buses.add(bus.get(0));
                buses.add(bus.get(1));
                imprimir(buses);
                //Toast.makeText(getContext(), "Mejor ruta es: "+ bus.get(0).getNombre(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(getContext(), "Ruta segundaria es: "+ bus.get(1).getNombre(), Toast.LENGTH_SHORT).show();
                return true;
            }

        }
       if(obtenerBusesParaAbordar(origen,destino).size()==0){

           Toast.makeText(getContext(), "Rutas alternativas", Toast.LENGTH_SHORT).show();
           rutaTransbordo();
       }


       return false;
    }

    private void imprimir(ArrayList<Bus> buses){
        for (Bus b:buses) {
            Toast.makeText(getContext(),"Aborda el:  "+ b.getNombre(), Toast.LENGTH_SHORT).show();
        }
    }
    private void rutaTransbordo(){
        int cantidadParaderos=paraderos.size();

            int paraderoPosterior=paraderoDestino.getPosicion()+1;
            int paraderoAnterior= paraderoDestino.getPosicion()-1;

                if(rutaCercana(paraderoOrigen,paraderos.get(paraderoAnterior-1))){
                    rutaCercana(paraderos.get(paraderoAnterior-1),paraderoDestino);
                }


        //ArrayList<Bus>buses=obtenerBusesParaAbordar();
        //Toast.makeText(getContext(), buses.get(0).getNombre(), Toast.LENGTH_SHORT).show();
        /*
        Toast.makeText(getContext(), paraderos.get(paraderoAnterior-1).getNombre(), Toast.LENGTH_SHORT).show();
            ArrayList<Bus>bus=filtroBusMenosParadas(paraderoOrigen.getBus(),paraderos.get(paraderoAnterior-1));
            if(bus.size()>1){
                Toast.makeText(getContext(), bus.get(1).getNombre(), Toast.LENGTH_SHORT).show();
        }*/
    }

    private ArrayList<Bus> filtroBusMenosParadas(ArrayList<Bus> buses){




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

        Collections.sort(auxiliar, new Comparator<AuxiliarBus>() {
            @Override
            public int compare(AuxiliarBus o1, AuxiliarBus o2) {
                return new Integer(o1.getContador()).compareTo(new Integer(o2.getContador()));
            }
        });
        ArrayList<AuxiliarBus>a=auxiliar;
        ArrayList<AuxiliarBus>b=auxiliar;


        for (int i=0;i<auxiliar.size();i++) {
           //Toast.makeText(getContext(),auxiliar.get(i).getBus().getNombre(), Toast.LENGTH_SHORT).show();

               for (int j = 0; j < a.size(); j++) {

                   if (auxiliar.get(i).getBus().getNombre().equals(a.get(j).getBus().getNombre()) && (auxiliar.get(i).getContador() < a.get(j).getContador())) {
                       b.remove(i);
                       //Toast.makeText(getContext(), "entre", Toast.LENGTH_SHORT).show();
                   }
               }

        }


        ArrayList<Bus>bus=new ArrayList<>();
        for (AuxiliarBus ax:b) {
            bus.add(ax.getBus());
        }
        return bus ;



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

    private ArrayList<Bus> filtroBusMenosParadas(ArrayList<Bus> buses,ParaderoBuscador paraderoDestino) {


        ArrayList<ParaderoBuscador> para = new ArrayList();

        //filtra los paraderos entre el origen y el destino devuelve un array con paraderos intermedios
        //for (Bus bus :buses) {
        //Toast.makeText(getContext(), "el bus: "+bus.getNombre()+ " para en ", Toast.LENGTH_SHORT).show();
        for (int i = paraderoOrigen.getPosicion(); i < paraderoDestino.getPosicion(); i++) {
            //Toast.makeText(getContext(), paraderos.get(i).getNombre(), Toast.LENGTH_SHORT).show();
            para.add(paraderos.get(i));
            // }
        }


        //obteniendo el origen y destino de paraderos se compara los intermedios por nombre buses que necesita

        // Object[][] cadena=new Object[3][para.size()];

        ArrayList<AuxiliarBus> auxiliar = new ArrayList();
        for (Bus b : buses) {
            String nombre = b.getNombre();
            int numero = 1;
            //Toast.makeText(getContext(), "evaluando bus: "+nombre, Toast.LENGTH_SHORT).show();
            for (ParaderoBuscador p : para) {
                //Toast.makeText(getContext(), "evaluando paraderos: "+p.getNombre(), Toast.LENGTH_SHORT).show();

                for (int i = 0; i < p.getBus().size(); i++) {
                    if (nombre.equals(p.getBus().get(i).getNombre())) {
                        // Toast.makeText(getContext(),"Encontrado: "+ p.getBus().get(i).getNombre(), Toast.LENGTH_SHORT).show();
                         /*   cadena[0][i]=p.getBus().get(i).getNombre();
                            cadena[1][i]=numero++;
                            cadena[2][i]=p.getBus().get(i);
                         */
                        auxiliar.add(new AuxiliarBus(numero++, p.getBus().get(i)));
                    }

                }
            }
        }

        Collections.sort(auxiliar, new Comparator<AuxiliarBus>() {
            @Override
            public int compare(AuxiliarBus o1, AuxiliarBus o2) {
                return new Integer(o1.getContador()).compareTo(new Integer(o2.getContador()));
            }
        });
        ArrayList<AuxiliarBus> a = auxiliar;
        ArrayList<AuxiliarBus> b = auxiliar;


        for (int i = 0; i < auxiliar.size(); i++) {
            //Toast.makeText(getContext(),auxiliar.get(i).getBus().getNombre(), Toast.LENGTH_SHORT).show();

            for (int j = 0; j < a.size(); j++) {

                if (auxiliar.get(i).getBus().getNombre().equals(a.get(j).getBus().getNombre()) && (auxiliar.get(i).getContador() < a.get(j).getContador())) {
                    b.remove(i);
                    //Toast.makeText(getContext(), "entre", Toast.LENGTH_SHORT).show();
                }
            }

        }


        ArrayList<Bus> bus = new ArrayList<>();
        for (AuxiliarBus ax : b) {
            bus.add(ax.getBus());
        }
        return bus;
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
