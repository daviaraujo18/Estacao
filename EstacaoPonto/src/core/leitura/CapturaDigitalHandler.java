package core.leitura;

import controllers.MainController;
import javafx.concurrent.Service;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import view.TelaPonto;

/**
 * @author aers
 */
public class CapturaDigitalHandler implements EventHandler<WorkerStateEvent> {

    private final TelaPonto tela;
    LeitorDigital ld;

    public CapturaDigitalHandler(LeitorDigital leitor, TelaPonto tela){
        this.ld = leitor;
        this.tela = tela;
    }

    @Override
    public void handle(WorkerStateEvent t) {

        Leitura result = getLeitura(t);
        result.getEvento().process(tela, result);
        Service ser = (Service) t.getSource();
        ser.restart();
    }

    private Leitura getLeitura(WorkerStateEvent t) {
        EventoLeitura resultado = EventoLeitura.NULO;
        String digitalHash = (String) t.getSource().getValue();
        int id = 0;

        try {
            id = ld.searchDigitalOnIndexSearchEngine(digitalHash);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (digitalHash == null || digitalHash.isEmpty()) {
            resultado = EventoLeitura.ERRO_LEITURA;
        }else{
            if (id > 0) {
                resultado = EventoLeitura.DIGITAL_RECONHECIDA;
            }else{
                resultado = EventoLeitura.DIGITAL_NAO_RECONHECIDA;
            }
        }

        Leitura l = new Leitura(resultado, digitalHash, String.valueOf(id), MainController.INSTANCE.getThreadRelogio().getMomentoAtual());
        return l;
    }
}