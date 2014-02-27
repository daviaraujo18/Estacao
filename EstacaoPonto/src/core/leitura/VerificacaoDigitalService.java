package core.leitura;

import controllers.MainController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;

/**
 * Created by Danilo on 12/02/14.
 */
public class VerificacaoDigitalService extends Service<Leitura>{

    String digitalHash;

    public VerificacaoDigitalService(String hash){
        this.digitalHash = hash;
    }

    @Override
    protected Task<Leitura> createTask() {
        return new Task<Leitura>() {
            @Override
            protected Leitura call() throws Exception {
                EventoLeitura resultado = EventoLeitura.NULO;
                int id = 0;

                try {
                    LeitorDigital ld = MainController.INSTANCE.getLeitorDigital();
                    id = ld.searchDigitalOnIndexSearchEngine(digitalHash);
                    ld.fecharLeitor();
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


        };
    }
}
