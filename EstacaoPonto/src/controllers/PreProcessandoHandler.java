package controllers;

import core.leitura.VerificacaoDigitalHandler;
import core.leitura.VerificacaoDigitalService;
import core.leitura.EventoLeitura;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

/**
 * Created by Danilo on 12/02/14.
 */
public class PreProcessandoHandler implements EventHandler<WorkerStateEvent> {

    @Override
    public void handle(WorkerStateEvent event) {
        EventoLeitura.LEITURA_EM_ANALISE.process(MainController.INSTANCE.tela, null);
        Object digitalOb = event.getSource().getValue();
        String digitalHash = "";
        if (digitalOb != null){
            digitalHash = digitalOb.toString();
        }
        VerificacaoDigitalService capturaDigitalService = new VerificacaoDigitalService(digitalHash);
        capturaDigitalService.setOnSucceeded(new VerificacaoDigitalHandler());
        capturaDigitalService.start();
    }
}
