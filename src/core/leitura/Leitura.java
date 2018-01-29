package core.leitura;

import core.DadosFrequentadores;

/**
 * Created by Danilo on 10/02/14.
 */
public class Leitura {

    private String hash;
    private EventoLeitura evento;
    private String idFrequentador;
    private String momento;

    public Leitura(EventoLeitura result, String digitalHash, String id, String momento) {
        this.idFrequentador = id;
        this.hash = digitalHash;
        this.evento = result;
        this.momento = momento;
    }

    public EventoLeitura getEvento() {
        return evento;
    }

    public void setEvento(EventoLeitura evento) {
        this.evento = evento;
    }

    public String getIdFrequentador() {
        return idFrequentador;
    }

    public void setIdFrequentador(String idFrequentador) {
        this.idFrequentador = idFrequentador;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setMomento(String momento) {
        this.momento = momento;
    }

    public String getMomento() {
        return momento;
    }
}
