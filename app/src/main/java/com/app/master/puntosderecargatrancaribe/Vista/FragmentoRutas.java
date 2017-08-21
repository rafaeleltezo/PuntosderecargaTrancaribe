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
import com.app.master.puntosderecargatrancaribe.Modelo.RestApi.ModeloBuscador.RutaBusParadero;
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
    private ArrayList<ParaderoBuscador> paraderoSegundarios;
    private ArrayList<RutaBusParadero> rutaBusParaderos;
    int contador;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        vista=inflater.inflate(R.layout.fragment_rutas, container, false);
        contador=0;
        rutaBusParaderos =new ArrayList();

        paraderos=datosParadero();
        paraderoSegundarios=datosParaderoSegundario();
        ArrayList<Bus> busesBombaGallo=new ArrayList();
        //buses de la castellana
        busesBombaGallo.add(new Bus("t102","Crespo"));
        busesBombaGallo.add(new Bus("t101","Portal"));
        busesBombaGallo.add(new Bus("t1031","Bocagrande"));
        busesBombaGallo.add(new Bus("t100","Expresa"));
        paraderoOrigen=new ParaderoBuscador(busesBombaGallo,"Bomba del gallo","Paradero frente Bomba del gallo","Bomba del gallo",0,0,1,"principal");
        buscadorParaderoDestino("sao");

        if(paraderoOrigen.getTipo().equals("segundario") && paraderoDestino.getTipo().equals("segundario")){
            int origenPosicion=(int)paraderoOrigen.getPosicion();
            int destinoPosicion=(int)paraderoDestino.getPosicion();
            //rutaVenidaeIda(paraderoOrigen, paraderoDestino);

            if(origenPosicion==destinoPosicion){
                rutaVenidaeIda(paraderoOrigen,paraderoDestino);
            }else{
                int destinoParadero=(int)paraderoOrigen.getPosicion()+1;
                ParaderoBuscador destinosegundario=obtenerPosicionParaderoPrincipal(destinoParadero);
                //Toast.makeText(getContext(), destinosegundario.getNombre(), Toast.LENGTH_SHORT).show();
                rutaVenidaeIda(paraderoOrigen,destinosegundario);
                //Toast.makeText(getContext(),"el origen es: "+destinosegundario.getNombre()+" el destino es: "+paraderoDestino.getNombre(), Toast.LENGTH_SHORT).show();
                rutaVenidaeIda(destinosegundario,paraderoDestino);

                int destino=(int)paraderoDestino.getPosicion()+1;
                ParaderoBuscador destinosegundarios=obtenerPosicionParaderoPrincipal(destino);
                rutaVenidaeIda(paraderoOrigen,destinosegundarios);
                rutaVenidaeIda(destinosegundarios,paraderoDestino);


            }

        }else if(paraderoOrigen.getTipo().equals("segundario")&&paraderoDestino.getTipo().equals("principal")){
            int destino=(int)paraderoOrigen.getPosicion()+1;
            ParaderoBuscador destinosegundario=obtenerPosicionParaderoPrincipal(destino);
            //Toast.makeText(getContext(), destinosegundario.getNombre(), Toast.LENGTH_SHORT).show();
            rutaVenidaeIda(paraderoOrigen,destinosegundario);
            //Toast.makeText(getContext(),"el origen es: "+destinosegundario.getNombre()+" el destino es: "+paraderoDestino.getNombre(), Toast.LENGTH_SHORT).show();
            rutaVenidaeIda(destinosegundario,paraderoDestino);



            //rutaVenidaeIda(paraderoOrigen,paraderoDestino);

        }else if(paraderoDestino.getTipo().equals("principal")&& paraderoOrigen.getTipo().equals("principal")) {
            rutaVenidaeIda(paraderoOrigen,paraderoDestino);
            Toast.makeText(getContext(), "principales", Toast.LENGTH_SHORT).show();

        }else if(paraderoDestino.getTipo().equals("segundario")&&paraderoOrigen.getTipo().equals("principal")){
            int destino=(int)paraderoDestino.getPosicion()+1;
            ParaderoBuscador destinosegundario=obtenerPosicionParaderoPrincipal(destino);
            rutaVenidaeIda(paraderoOrigen,destinosegundario);
            rutaVenidaeIda(destinosegundario,paraderoDestino);
        }

        /*
        switch (paraderoOrigen.getTipo()){
            case "segundario":


                //Toast.makeText(getContext(), obtenerPosicionParaderoPrincipal(destino).getNombre(), Toast.LENGTH_SHORT).show();
                if(paraderoDestino.getTipo().equals("segundario")){

                }
                break;
            case "principal":
                //Toast.makeText(getContext(), "primario", Toast.LENGTH_SHORT).show();
                rutaVenidaeIda(paraderoOrigen,paraderoDestino);
                break;
        }*/

        return vista;
    }



    public ArrayList<RutaBusParadero> obtenerrutas(){
        rutaBusParaderos.add(new RutaBusParadero(paraderoDestino,paraderoDestino.getNombre()));
        return rutaBusParaderos;
    }

    public ParaderoBuscador obtenerPosicionParaderoPrincipal(int posicion){
        for (ParaderoBuscador p :paraderos){
            if(p.getPosicion()==posicion){
                return p;
            }
        }
        return null;
    }
    public  void rutaVenidaeIda(ParaderoBuscador origen,ParaderoBuscador destino){
        if(destino.getPosicion()>origen.getPosicion()){
            //Toast.makeText(getContext(), "entre", Toast.LENGTH_SHORT).show();
            rutaCercana(origen,destino);

        }else if(destino.getPosicion()<origen.getPosicion()) {
            double origenes=origen.getPosicion();
            //origen.setPosicion(-1*origenes);
            convertirPosicionParaderosNegativo();
            //Toast.makeText(getContext(),"el origen es: "+ String.valueOf(origen.getPosicion())+ " el destino es: "+String.valueOf(destino.getPosicion()), Toast.LENGTH_SHORT).show();
            rutaCercana(origen,destino);

        }
    }
    public void convertirPosicionParaderosNegativo(){

        for (int i = 0; i <paraderos.size() ; i++) {
            double posicion=paraderos.get(i).getPosicion();
            paraderos.get(i).setPosicion(-1*posicion);
        }
        for (int i = 0; i <paraderoSegundarios.size() ; i++) {
            double posicion=paraderoSegundarios.get(i).getPosicion();
            paraderoSegundarios.get(i).setPosicion(-1*posicion);
        }

    }
    public ArrayList<ParaderoBuscador> datosParaderoSegundario(){
        paraderoSegundarios=new ArrayList();
        ArrayList<Bus> busesAlimentador1Castellana=new ArrayList();
        //buses de la castellana
        busesAlimentador1Castellana.add(new Bus("t106","Variante"));
        //busesAlimentador1Castellana.add(new Bus("t1018","Bocagrande"));
       // busesAlimentador1Castellana.add(new Bus("xt101","Todas las paradas"));
        paraderoSegundarios.add(new ParaderoBuscador(busesAlimentador1Castellana,"bomba el amparo","Paradero frente bomba el amparo","bomba el amparo,cai bomba el amparo,",0,0,1.1,"segundario"));

        ArrayList<Bus> busesAlimentador2Castellana=new ArrayList();
        //buses de la castellana
        busesAlimentador2Castellana.add(new Bus("t103","Variante"));
       // busesAlimentador2Castellana.add(new Bus("t1031","Bocagrande"));
        //busesCastellana.add(new Bus("xt101","Todas las paradas"));
        paraderoSegundarios.add(new ParaderoBuscador(busesAlimentador2Castellana,"sao","Paradero frente sao","sao",0,0,4.2,"segundario"));

        ArrayList<Bus> busesAlimentador3Castellana=new ArrayList();
        //buses de la castellana
        busesAlimentador3Castellana.add(new Bus("t106","Variante"));
        //busesCastellana.add(new Bus("xt101","Todas las paradas"));
        paraderoSegundarios.add(new ParaderoBuscador(busesAlimentador3Castellana,"sanjose","Paradero frente sanjose","san jose",0,0,1.3,
             "segundario"));
    return paraderoSegundarios;
    }

    //datos entrantes
    public ArrayList<ParaderoBuscador> datosParadero(){
        paraderos=new ArrayList();

        ArrayList<Bus> busesBombaGallo=new ArrayList();
        //buses de la castellana
        busesBombaGallo.add(new Bus("t102","Crespo"));
        busesBombaGallo.add(new Bus("t101","Portal"));
        busesBombaGallo.add(new Bus("t1031","Bocagrande"));
        busesBombaGallo.add(new Bus("t100","Expresa"));
        paraderos.add(new ParaderoBuscador(busesBombaGallo,"Bomba del gallo","Paradero frente Bomba del gallo","Bomba del gallo",0,0,1,"principal"));

        ArrayList<Bus> busesCastellana=new ArrayList();
        //buses de la castellana
        busesCastellana.add(new Bus("t102","Crespo"));
        busesCastellana.add(new Bus("t101","Portal"));
        busesCastellana.add(new Bus("t103","Bocagrande"));
        busesCastellana.add(new Bus("t106","Variante"));
        busesCastellana.add(new Bus("t100","Expresa"));
        paraderos.add(new ParaderoBuscador(busesCastellana,"Castellana","Paradero de la castellana","castellana,Exito cartagena,",0,0,2,"principal"));
        //buses de cuatro vientos
        ArrayList<Bus> losejecutivos=new ArrayList();
        losejecutivos.add(new Bus("t103","Bocagrande"));
        losejecutivos.add(new Bus("t106","Variante"));
        losejecutivos.add(new Bus("t101","Portal"));
        paraderos.add(new ParaderoBuscador(losejecutivos,"Cuatro vientos","paradero Cuatro viento","los ejecutivos",0,9,3,"principal"));
        //paradero inventado

        ArrayList<Bus> busesVillaOlimpica=new ArrayList();
        busesVillaOlimpica.add(new Bus("t101","Portal"));
        paraderos.add(new ParaderoBuscador(busesVillaOlimpica,"Villa olimpica","paradero Villa olimpica","Estadio jaime moron, villa olimpica,",0,9,4,"principal"));

        ArrayList<Bus> busesCuatroViento=new ArrayList();
        busesCuatroViento.add(new Bus("t101","Portal"));
        busesCuatroViento.add(new Bus("t102","Crespo"));
        busesCuatroViento.add(new Bus("t103","Bocagrande"));
        paraderos.add(new ParaderoBuscador(busesCuatroViento,"Cuatro vientos","paradero Cuatro vientos","cuatro vientos,frente sena cuatro viento,",0,9,5,"principal"));

        //buses de crespo
        ArrayList<Bus> busesMariaAuxiliadora=new ArrayList();
        busesMariaAuxiliadora.add(new Bus("t106","Variante"));
        busesMariaAuxiliadora.add(new Bus("t101","Portal"));
        paraderos.add(new ParaderoBuscador(busesMariaAuxiliadora,"Maria auxiliadora","paradero Maria auxiliadora","maria auxiliadora,cai maria auxiliadora",0,9,6,"principal"));


        ArrayList<Bus> busesBasurto=new ArrayList();
        busesBasurto.add(new Bus("t106","Variante"));
        busesBasurto.add(new Bus("t102","Crespo"));
        busesBasurto.add(new Bus("t108","Bocagrande"));
        paraderos.add(new ParaderoBuscador(busesBasurto,"Basurto","paradero Basurto","mercado Basurto",0,9,7,"principal"));

        return paraderos;
    }


    //Buscar el paradero de destino a donde se dirige

    public void buscadorParaderoDestino(String destino){

        //buscador de paradero destino con el nombre solicitado por el usuario

        for (ParaderoBuscador paradero:paraderos) {
            for (String palabras:descomponerPalabra(paradero.getPalabrasClaves())) {
                if (palabras.equals(destino)) {
                    //Toast.makeText(getContext(), "Encontrado "+"Nombre del paradero: "+paradero.getNombre(), Toast.LENGTH_SHORT).show();
                    this.paraderoDestino=paradero;
                    //Toast.makeText(getContext(), paraderoDestino.getPalabrasClaves(), Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
        for (ParaderoBuscador paradero:paraderoSegundarios) {
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
                    //Toast.makeText(getContext(), paraderoDestino.getBus().get(i).getNombre(), Toast.LENGTH_SHORT).show();
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
            rutaBusParaderos.add(new RutaBusParadero(origen,buses.get(0).getNombre()));
            //imprimir(buses);

            //Toast.makeText(getContext(), "Aborda el " + obtenerBusesParaAbordar(origen,destino).get(0).getNombre(), Toast.LENGTH_SHORT).show();
            return true;
        }else if(obtenerBusesParaAbordar(origen,destino).size()>1){
            ArrayList<Bus>bus=filtroBusMenosParadas(obtenerBusesParaAbordar(origen,destino));
            //filtroBusMenosParadas(bus);
            //Toast.makeText(getContext(),"son "+String.valueOf(bus.size()) , Toast.LENGTH_SHORT).show();
            if(bus.size()==0){
                buses.add(obtenerBusesParaAbordar(origen,destino).get(0));
                rutaBusParaderos.add(new RutaBusParadero(origen,buses.get(0).getNombre()));
                //imprimir(buses);
            }else if(bus.size()==1) {
                buses.add(bus.get(0));
                rutaBusParaderos.add(new RutaBusParadero(origen,buses.get(0).getNombre()));
                //imprimir(buses);
                //Toast.makeText(getContext(), "Aborda: " + bus.get(0).getNombre(), Toast.LENGTH_SHORT).show();
                return true;
            }else if(bus.size()>1) {
                buses.add(bus.get(0));
                rutaBusParaderos.add(new RutaBusParadero(origen,buses.get(0).getNombre()));
                //buses.add(bus.get(1));
                //imprimir(buses);
                //Toast.makeText(getContext(), "Mejor ruta es: "+ bus.get(0).getNombre(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(getContext(), "Ruta segundaria es: "+ bus.get(1).getNombre(), Toast.LENGTH_SHORT).show();
                return true;
            }

        }
       if(obtenerBusesParaAbordar(origen,destino).size()==0){

           Toast.makeText(getContext(), "Rutas alternativas", Toast.LENGTH_SHORT).show();
           //Toast.makeText(getContext(), paraderoDestino.getNombre(), Toast.LENGTH_SHORT).show();
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
        int cantidadParaderos = paraderos.size();

       // if(paraderoOrigen.getPosicion()>0 && paraderoDestino.getPosicion()>0) {
            contador++;
            int paraderoPosterior = (int)paraderoDestino.getPosicion() + contador;
            int paraderoAnterior  = (int)paraderoDestino.getPosicion() - contador;

        String cadena=String.valueOf(paraderoDestino.getPosicion());





        try {
            //Toast.makeText(getContext(), String.valueOf(paraderoPosterior), Toast.LENGTH_SHORT).show();
            if (paraderoOrigen.getPosicion() > 0 && paraderoDestino.getPosicion() > 0) {
                if (rutaCercana(paraderoOrigen, paraderos.get( paraderoAnterior - 1))) {
                    rutaCercana(paraderos.get( paraderoAnterior - 1), paraderoDestino);
                    //Toast.makeText(getContext(), paraderos.get( paraderoAnterior - 1).getNombre(), Toast.LENGTH_SHORT).show();
                }
            } else if (paraderoOrigen.getPosicion() < 0 && paraderoDestino.getPosicion() < 0) {
                if (rutaCercana(paraderoOrigen, paraderos.get(-1 * ( paraderoAnterior - 1)))) {
                    rutaCercana(paraderos.get(-1 * ( paraderoAnterior - 1)), paraderoDestino);
                }
            }
        }catch (ArrayIndexOutOfBoundsException e){
            if (paraderoOrigen.getPosicion() > 0 && paraderoDestino.getPosicion() > 0) {
                if (rutaCercana(paraderoOrigen, paraderos.get( paraderoPosterior + 1))) {
                    rutaCercana(paraderos.get( paraderoPosterior + 1), paraderoDestino);
                    //Toast.makeText(getContext(), paraderos.get( paraderoPosterior + 1).getNombre(), Toast.LENGTH_SHORT).show();
                }
            } else if (paraderoOrigen.getPosicion() < 0 && paraderoDestino.getPosicion() < 0) {
                if (rutaCercana(paraderoOrigen, paraderos.get(-1 * ( paraderoPosterior + 1)))) {
                    rutaCercana(paraderos.get(-1 * ( paraderoPosterior + 1)), paraderoDestino);
                }
            }
        }



       // }else{
         //   Toast.makeText(getContext(), "paradero menor", Toast.LENGTH_SHORT).show();
        //}
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
           // for (int i=paraderoOrigen.getPosicion();i<paraderoDestino.getPosicion();i++) {
                //Toast.makeText(getContext(), paraderos.get(i).getNombre(), Toast.LENGTH_SHORT).show();
                //para.add(paraderos.get(i));

           // }
       // }

        if(paraderoOrigen.getPosicion()>0 && paraderoDestino.getPosicion()>0) {
             for (double i=paraderoOrigen.getPosicion();i<paraderoDestino.getPosicion();i++) {
            //Toast.makeText(getContext(), paraderos.get((int)i).getNombre(), Toast.LENGTH_SHORT).show();
                     para.add(paraderos.get((int)i));
             }

        }else if(paraderoOrigen.getPosicion()<0 && paraderoDestino.getPosicion()<0) {
            for (double i = paraderoOrigen.getPosicion(); i < paraderoDestino.getPosicion(); i++) {
                para.add(paraderos.get((-1 * ((int)i)) - 1));
            }
        }


        //obteniendo el origen y destino de paraderos se compara los intermedios por nombre buses que necesita


        ArrayList<AuxiliarBus> auxiliar=new ArrayList();
        for (Bus b:buses) {
            String nombre=b.getNombre();
            int numero=1;

                for (ParaderoBuscador p:para) {


                    for (int i = 0; i < p.getBus().size(); i++) {
                        if(nombre.equals(p.getBus().get(i).getNombre())){
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


               for (int j = 0; j < a.size(); j++) {

                   if (auxiliar.get(i).getBus().getNombre().equals(a.get(j).getBus().getNombre()) && (auxiliar.get(i).getContador() < a.get(j).getContador())) {
                       b.remove(i);

                   }
               }

        }


        ArrayList<Bus>bus=new ArrayList<>();
        for (AuxiliarBus ax:b) {
            bus.add(ax.getBus());
            //Toast.makeText(getContext(), ax.getBus().getNombre(), Toast.LENGTH_SHORT).show();
        }

        return bus ;

    }

    private ArrayList<Bus> filtroBusMenosParadas(ArrayList<Bus> buses,ParaderoBuscador paraderoDestino) {


        ArrayList<ParaderoBuscador> para = new ArrayList();

        //filtra los paraderos entre el origen y el destino devuelve un array con paraderos intermedios
        //for (Bus bus :buses) {
        //Toast.makeText(getContext(), "el bus: "+bus.getNombre()+ " para en ", Toast.LENGTH_SHORT).show();
        for (double i = paraderoOrigen.getPosicion(); i < paraderoDestino.getPosicion(); i++) {
            //Toast.makeText(getContext(), paraderos.get(i).getNombre(), Toast.LENGTH_SHORT).show();
            para.add(paraderos.get((int)i));
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
