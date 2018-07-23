package core.leitura;

import controllers.MainController;
import core.DadosFrequentadores;
import core.ValidarBatidaManualService;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.web.WebEngine;
import utils.The;
import utils.VerificaConexao;
import view.BloqueioTela;

import java.util.Map;
import utils.Log;

/**
 * Created by Danilo on 18/03/14.
 */
public enum Operacao {
    DESBLOQUEIO {
        @Override
        public void execute(String digital) {
            try {
                int idFre = LeitorDigital.getInstance().searchDigitalOnIndexSearchEngine(digital);
                Map<Integer,String> administradores = DadosFrequentadores.getInstance().getAdministradores();
                Boolean isAdministrador =  administradores.containsKey(idFre);
                if(isAdministrador){
                    BloqueioTela.getInstance().desbloquear();
                }

            }catch (Exception e){
				Log.e(e);
//                e.printStackTrace();

            }
            MainController.INSTANCE.getCds().restart();
        }
    },

    REGISTRO_FREQUENCIA {
        @Override
        public void execute(String result) {
            EventoLeitura.LEITURA_EM_ANALISE.process(MainController.INSTANCE.tela, null);
            String digitalHash = "";
            if (result != null){
                digitalHash = result.toString();
            }
            VerificacaoDigitalService capturaDigitalService = new VerificacaoDigitalService(digitalHash);
            capturaDigitalService.setOnSucceeded(new VerificacaoDigitalHandler());
            capturaDigitalService.start();
        }
    },
    LOGINMANUAL{
        @Override
        public void execute(String result) {
            EventoLeitura.LEITURA_EM_ANALISE.process(MainController.INSTANCE.tela, null);

            boolean temConexaoComIntranet = VerificaConexao.verificaConexao() != -1;
            if (temConexaoComIntranet) {
                String login = (String) The.inserirJavascript(MainController.INSTANCE.tela.getWebEngine(), "jQuery('input[name=accessKey]').val()");
                String senha = (String) The.inserirJavascript(MainController.INSTANCE.tela.getWebEngine(), "jQuery('input[name=plainPassword]').val()");
                Log.i("Validação Login manual..." + login+" hora:"+MainController.INSTANCE.getThreadRelogio().getMomentoAtual() );
                ValidarBatidaManualService validarBatidaManualService = new ValidarBatidaManualService(login, senha);

                validarBatidaManualService.setOnSucceeded(new VerificacaoDigitalHandler());

                validarBatidaManualService.start();
            }
            MainController.INSTANCE.getCds().loginManual = false;
            The.inserirJavascript(MainController.INSTANCE.tela.getWebEngine(), "jQuery('input[name=accessKey]').val('')");
            The.inserirJavascript(MainController.INSTANCE.tela.getWebEngine(), "jQuery('input[name=plainPassword]').val('')");

        }
    };

    public abstract void execute(String digital);

}
