package controllers;

import async.PreProcessandoService;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

/**
 * Created by Danilo on 12/02/14.
 */
public class PreProcessandoHandler implements EventHandler<WorkerStateEvent> {

    @Override
    public void handle(WorkerStateEvent event) {
        PreProcessandoService.Result result = (PreProcessandoService.Result) event.getSource().getValue();
        if(result != null){
            result.process();
        }else{

        }
        MainController.INSTANCE.getCds().clickDesbloqueioTela = false;

    }

}
