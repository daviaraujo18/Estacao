package core.leitura;

import controllers.MainController;
import core.DadosFrequentadores;
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
    };

    public abstract void execute(String digital);

}
