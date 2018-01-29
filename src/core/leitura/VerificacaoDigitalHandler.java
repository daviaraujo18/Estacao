package core.leitura;

import controllers.MainController;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

/**
 * @author aers
 */
public class VerificacaoDigitalHandler implements EventHandler<WorkerStateEvent> {

    public VerificacaoDigitalHandler(){
    }

    @Override
    public void handle(WorkerStateEvent event) {
        Leitura result = (Leitura) event.getSource().getValue();
        result.getEvento().process(MainController.INSTANCE.tela, result);
        MainController.INSTANCE.getCds().restart();
    }
}