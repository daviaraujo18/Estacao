package core.leitura;

import controllers.MainController;
import core.DadosFrequentadores;
import java.util.Map;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import utils.Log;

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
					Log.e(e);
//                    e.printStackTrace();
                }

                if (digitalHash == null || digitalHash.isEmpty()) {
                    resultado = EventoLeitura.ERRO_LEITURA;
                }else{
                    if (id > 0) {
                        /////////////aqui
                //                    resultado = EventoLeitura.DIGITAL_RECONHECIDA;
                //    }else{
                        Map<Integer, String> mapaIdInfoFrequentadores = DadosFrequentadores.getInstance().getFrequentadores();
                        String[] dados = mapaIdInfoFrequentadores.get(id).split(";");
                        String localTrabalho = dados[4];
                        boolean definido =false;
//                        System.out.println("localtrabalho: "+localTrabalho);
                        if (!localTrabalho.equals("0"))
                        {
                            String prediosIds = MainController.INSTANCE.prediosIds;
//                            System.out.println("executado");
//                            System.out.println("prediosIds: "+prediosIds.toString());
                            String [] prediosIdsArray = prediosIds.toString().split(";");
//                            System.out.println("comparando predios");
                            

                            for (String predioId : prediosIdsArray)
                            {
                                if (localTrabalho.equals(predioId))
                                {
//                                    System.out.println("predio igual: "+localTrabalho + " "+predioId);
                                    resultado = EventoLeitura.DIGITAL_RECONHECIDA;
                                    definido = true;
                                    break;
                                }
                            }
                        }
                        if (!definido)
                        {
//                            System.out.println("fim da comparação");
                            resultado = EventoLeitura.DIGITAL_RECONHECIDA_RESSALVA_PREDIO;
                        }
                        
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
