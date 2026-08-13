package core.leitura;

import controllers.MainController;
import core.DadosFrequentadores;
import core.SincronizacaoImediata;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import utils.ArquivoRegistros;
import utils.LogAplicacao;

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

                LeitorDigital ld = MainController.INSTANCE.getLeitorDigital();
                id = ld.searchDigitalOnIndexSearchEngine(digitalHash);
                ld.fecharLeitor();

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
//                            System.out.println("fim da compara��o");
                            resultado = EventoLeitura.DIGITAL_RECONHECIDA_RESSALVA_PREDIO;
                        }
                        
                    }else{
                        resultado = EventoLeitura.DIGITAL_NAO_RECONHECIDA;
                    }
                }

                // Mesma fonte de horário do login manual (ValidarBatidaManualService):
                // relógio real do Windows (new Date()), não o ThreadRelogio
                // simulado. O ThreadRelogio só sincroniza com o servidor na
                // carga da página e depois conta o tempo sozinho — se o
                // relógio do Windows estiver desviado nesse instante (drift
                // de VM/QEMU), a base fica errada e todas as batidas
                // biométricas seguintes saem com um horário diferente do
                // login manual. Usando new Date() em ambos os fluxos, os
                // dois SEMPRE ficam no mesmo relógio, ainda que o relógio da
                // VM esteja desviado da hora real.
                String momento = new SimpleDateFormat("dd:MM:yyyy:HH:mm:ss").format(new Date());

                // Sincroniza na hora (mesmo padrão do login manual,
                // ValidarBatidaManualService) em vez de esperar o ciclo
                // periódico de ArquivoRegistros/ThreadRelogio — assim a
                // batida biométrica já está confirmada no servidor a tempo
                // do reload da tela mostrar a mesma mensagem de confirmação
                // que o login manual mostra. Só grava na fila local
                // (ArquivoRegistros) se essa chamada falhar — se gravasse
                // sempre, a MESMA batida seria reenviada de novo pelo ciclo
                // periódico, criando um TimeRecord duplicado fora de ordem
                // e quebrando a alternância entrada/saída.
                if (resultado == EventoLeitura.DIGITAL_RECONHECIDA || resultado == EventoLeitura.DIGITAL_RECONHECIDA_RESSALVA_PREDIO) {
                    boolean sincronizado = false;
                    try {
                        sincronizado = SincronizacaoImediata.sincronizar(id, momento, "biometric");
                    } catch (Exception e) {
                        LogAplicacao.e(e);
                    }
                    if (!sincronizado) {
                        ArquivoRegistros.escreverRegistro(id + "-" + momento);
                    }
                }

                Leitura l = new Leitura(resultado, digitalHash, String.valueOf(id), momento);
                return l;
            }


        };
    }
}
