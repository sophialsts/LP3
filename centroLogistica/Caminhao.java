import java.io.*;
import java.util.concurrent.*;
import java.util.*;

public class Caminhao implements Runnable {

    private final boolean cargaComum; 
    private final CentroLogistica centro;
    
    private TipoDoca docaAlocada; 
    private static final Random random = new Random();
    private static int proximoId = 1;
    private final int id;

    public Caminhao (boolean flag, CentroLogistica centro) {
        this.cargaComum = flag;
        this.centro = centro;
        this.id = proximoId++;
    }

    @Override
    public void run() {
        try {
            this.docaAlocada = centro.tentarAlocarDoca(this);

            if(this.docaAlocada == null) {
                return; 
            }
            
            int ms = (random.nextInt(6) + 3) * 1000; 
            System.out.println(this + " está operando por " + (ms/1000) + "s...");
            Thread.sleep(ms); 

            System.out.println(this + " terminou a operação.");
            centro.liberarDoca(this); 

        } catch(InterruptedException e) {
            System.err.println(this + " foi interrompido.");
            Thread.currentThread().interrupt();
        }
    }

    public TipoDoca getDocaAlocada() {
        return docaAlocada;
    }

    public boolean isCargaComum() {
        return cargaComum;
    }

    public CentroLogistica getCentro() {
        return centro;
    }

    @Override
    public String toString() {
        return "Caminhão #" + id + " (" + (cargaComum ? "Comum" : "Refrigerado") + ")";
    }
}